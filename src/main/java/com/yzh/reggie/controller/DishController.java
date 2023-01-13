package com.yzh.reggie.controller;

import com.yzh.reggie.common.R;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.service.DishFlavorService;
import com.yzh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}
