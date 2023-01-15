package com.yzh.reggie.controller;

import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 杨振华
 * @since 2023/1/15
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private Order order;

    /**
     * 提交
     *
     * @param order 订单
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Order order){


    }
}
