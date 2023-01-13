package com.yzh.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.DishFlavor;
import com.yzh.reggie.mapper.DishMapper;
import com.yzh.reggie.service.DishFlavorService;
import com.yzh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，保存口味
     * @param dishDto
     */
    @Override
    @Transactional// 多张表操作，需要事务控制，启动类也要加注解
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        // dishId
        Long dishDtoId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
