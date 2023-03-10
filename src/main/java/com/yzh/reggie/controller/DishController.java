package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.yzh.reggie.common.R;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.DishFlavor;
import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.service.CategoryService;
import com.yzh.reggie.service.DishFlavorService;
import com.yzh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品控制器
 *
 * @author 杨振华
 * @since 2023/1/13
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private CategoryService categoryService;

    /**
     *  新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        // 分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        // 这里因为需要有categoryName这一属性，
        // 所以需要引入DishDto，所以需要进行copy操作
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);


        // 条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper
                .like(name != null,Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);


        dishService.page(pageInfo,dishLambdaQueryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 拷贝忽略records
            BeanUtils.copyProperties(item,dishDto);

            // 得到分类id
            Long categoryId = item.getCategoryId();
            // 得到分类名称
            Category category = categoryService.getById(categoryId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id修改菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 保存更新
     *
     * @param dishDto 菜dto
     * @return {@link R}<{@link String}>
     */
    @PutMapping()
    public R<String> saveUpdate(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        return R.success("菜品修改成功！");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper
                .eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId())
                .eq(Dish::getStatus,1)// 只查启售
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        return R.success(list);

    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper
                .eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId())
                .eq(Dish::getStatus,1)// 只查启售
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 拷贝忽略records
            BeanUtils.copyProperties(item,dishDto);

            // 得到分类id
            Long categoryId = item.getCategoryId();
            // 得到分类名称
            Category category = categoryService.getById(categoryId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }
            // 菜品id
            Long itemId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,itemId);
            // SQL：select * from dish_flavor where dish_id = ?

            dishDto.setFlavors(dishFlavorService.list(dishFlavorLambdaQueryWrapper));

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);

    }

    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        return R.success("菜品删除成功！");
    }


    /**
     * 菜品停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> statusStop(@RequestParam List<Long> ids){
        // 根据输入的ids，进行停售
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper
                .in(Dish::getId,ids)
                .eq(Dish::getStatus,1);

        int count = dishService.count(queryWrapper);
        if(count > 0) {
            for (Long id : ids) {
                Dish dish = dishService.getById(id);
                dish.setStatus(0);
                dishService.updateById(dish);
            }
        }
        return R.success("菜品已经停售！");
    }

    /**
     * 菜品启售
     *
     * @param ids id
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/status/1")
    public R<String> statusStart(@RequestParam List<Long> ids){
        // 根据输入的ids，进行停售
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper
                .in(Dish::getId,ids)
                .eq(Dish::getStatus,0);

        int count = dishService.count(queryWrapper);
        if(count > 0) {
            for (Long id : ids) {
                Dish dish = dishService.getById(id);
                dish.setStatus(1);
                dishService.updateById(dish);
            }
        }
        return R.success("菜品已经启售！");
    }
}
