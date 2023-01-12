package com.yzh.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;// 身份证号码

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // @TableField(exist = false) 注解加载bean属性上，
    // 表示当前属性不是数据库的字段，但在项目中必须使用，
    // 这样在新增等使用bean的时候，mybatis-plus就会忽略这个，不会报错。
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
