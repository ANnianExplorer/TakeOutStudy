package com.yzh.reggie.dto;

import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
