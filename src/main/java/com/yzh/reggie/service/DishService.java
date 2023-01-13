package com.yzh.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对于的口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

}
