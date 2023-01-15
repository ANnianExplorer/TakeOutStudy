package com.yzh.reggie.controller;

import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.Orders;
import com.yzh.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
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
    private OrderService orderService;

    /**
     * 提交
     *
     * @param orders 订单
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功！");
    }
}
