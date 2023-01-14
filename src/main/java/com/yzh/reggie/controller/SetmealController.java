package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.yzh.reggie.common.CustomException;
import com.yzh.reggie.common.R;
import com.yzh.reggie.dto.SetmealDto;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.entity.SetmealDish;
import com.yzh.reggie.service.CategoryService;
import com.yzh.reggie.service.SetmealDishService;
import com.yzh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨振华
 * @since 2023/1/14
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetmealService setmealService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody SetmealDto setmealDto ){
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveDish(setmealDto);
        return R.success("套餐新增成功！");
    }

    /**
     * 套餐分页查询
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        // 分页
        Page<Setmeal> pageInfo = new Page<Setmeal>(page,pageSize);
        Page<SetmealDto> DtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper
                //.eq(Setmeal::getStatus,1)// 只查启售
                .like(name != null,Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,setmealLambdaQueryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,DtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list =
                records.stream().map((item) -> {
                    SetmealDto setmealDto = new SetmealDto();

                    BeanUtils.copyProperties(item,setmealDto);

                    Long categoryId = item.getCategoryId();
                    Category category = categoryService.getById(categoryId);
                    if (category!=null){
                        setmealDto.setCategoryName(category.getName());
                    }
                    return setmealDto;
                }).collect(Collectors.toList());
        DtoPage.setRecords(list);
        return R.success(DtoPage);
    }

    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功！");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 修改页面展示信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> upadte(@PathVariable Long id){
        SetmealDto setmealByDish = setmealService.getSetmealByDish(id);
        return R.success(setmealByDish);
    }

    /**
     * 保存更新
     *
     * @param setmealDto setmeal dto
     * @return {@link R}<{@link String}>
     */
    @PutMapping()
    public R<String> saveUpdate(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("菜品修改成功！");
    }

    /**
     * 商品停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> statusStop(@RequestParam List<Long> ids){
        // 根据输入的ids，进行停售
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper
                .in(Setmeal::getId,ids)
                .eq(Setmeal::getStatus,1);

        int count = setmealService.count(queryWrapper);
        if(count > 0) {
            for (Long id : ids) {
                Setmeal setmeal = setmealService.getById(id);
                setmeal.setStatus(0);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("套餐已经停售！");
    }

    /**
     * 商品启售
     *
     * @param ids id
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/status/1")
    public R<String> statusStart(@RequestParam List<Long> ids){
        // 根据输入的ids，进行停售
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper
                .in(Setmeal::getId,ids)
                .eq(Setmeal::getStatus,0);

        int count = setmealService.count(queryWrapper);
        if(count > 0) {
            for (Long id : ids) {
                Setmeal setmeal = setmealService.getById(id);
                setmeal.setStatus(1);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("套餐已经启售！");
    }
}
