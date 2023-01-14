package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.common.CustomException;
import com.yzh.reggie.dto.SetmealDto;
import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.entity.SetmealDish;
import com.yzh.reggie.mapper.SetmealMapper;
import com.yzh.reggie.service.SetmealDishService;
import com.yzh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨振华
 * @date 2023/01/13
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    @Override
    public void saveDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存套餐和菜品关联信息
        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 根据id删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // select count(*) from setmeal where id in (1,2,3) and status = 1
        setmealLambdaQueryWrapper
                .in(Setmeal::getId,ids)
                .eq(Setmeal::getStatus,1);
        int count = this.count(setmealLambdaQueryWrapper);
        if (count > 0){
            throw new CustomException("套餐正在售卖中，不能删除！");
        }
        // 如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        // 删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper
                .in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}