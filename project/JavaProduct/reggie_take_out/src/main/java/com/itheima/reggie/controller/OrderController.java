package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService ordersService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    //支付后查看订单功能
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> pageDto = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //这里是直接把当前用户分页的全部结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //这里是把所有的订单分页查询出来
        ordersService.page(pageInfo,queryWrapper);

        //对OrderDto进行属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) ->{//item其实就是分页查询出来的每个订单对象
            OrderDto orderDto = new OrderDto();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();//获取订单id
            //调用根据订单id条件查询订单明细数据的方法，把查询出来订单明细数据存入orderDetailList
            List<OrderDetail> orderDetailList = ordersService.getOrderDetailListByOrderId(orderId);

            BeanUtils.copyProperties(item,orderDto);//把订单对象的数据复制到orderDto中
            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        //将订单分页查询的订单数据以外的内容复制到pageDto中，不清楚可以对着图看
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }


    /**
     * 再来一单
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<String> againSubmit(@RequestBody Map<String,String> map) {
            String ids = map.get("id");

            long id = Long.parseLong(ids);

            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, id);
            //获取该订单对应的所有的订单明细表
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

            //通过用户id把原来的购物车给清空，这里的clean方法就是之前的购物车清空方法，我给写到service中去了，这样可以通过接口复用代码
             LambdaQueryWrapper<ShoppingCart> queryWrapper2 = new LambdaQueryWrapper<>();
             queryWrapper2.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
             shoppingCartService.remove(queryWrapper2);

            //获取用户id
            Long userId = BaseContext.getCurrentId();
            List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
                //把从order表中和order_details表中获取到的数据赋值给这个购物车对象
                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.setUserId(userId);
                shoppingCart.setImage(item.getImage());
                Long dishId = item.getDishId();
                Long setmealId = item.getSetmealId();
                if (dishId != null) {
                    //如果是菜品那就添加菜品的查询条件
                    shoppingCart.setDishId(dishId);
                } else {
                    //添加到购物车的是套餐
                    shoppingCart.setSetmealId(setmealId);
                }
                shoppingCart.setName(item.getName());
                shoppingCart.setDishFlavor(item.getDishFlavor());
                shoppingCart.setNumber(item.getNumber());
                shoppingCart.setAmount(item.getAmount());
                shoppingCart.setCreateTime(LocalDateTime.now());
                return shoppingCart;
            }).collect(Collectors.toList());

            //把携带数据的购物车批量插入购物车表  这个批量保存的方法要使用熟练！！！
            shoppingCartService.saveBatch(shoppingCartList);

            return R.success("操作成功");
        }

    /**
     * 订单信息分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime)
    {

        //构造分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(number != null,Orders::getId,number);
        queryWrapper.gt(beginTime !=null,Orders::getOrderTime,beginTime);
        queryWrapper.lt(endTime !=null, Orders::getOrderTime,endTime);
//        queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());

        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //执行分页查询
        ordersService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,orderDtoPage,"records");

        List<Orders> records = pageInfo.getRecords();

        List<OrderDto> list = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();

            BeanUtils.copyProperties(item,orderDto);

//            Long ordersId =item.getId();//分类id
////            根据id查询分类对象
//            Orders orders = ordersService.getById(ordersId);
//
//            if(orders != null){
//                String userName = orders.getUserName();
//                orderDto.setConsignee(userName);
//            }

            return orderDto;
        }).collect(Collectors.toList());

        orderDtoPage.setRecords(list);

        return R.success(orderDtoPage);
    }

    //修改配送状态
    @PutMapping
    public R<String> status() {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();

        //根据传入的id集合进行批量查询
        List<Orders> list = ordersService.list(queryWrapper);

        for (Orders orders : list) {
            if (orders != null) {
                if (orders.getStatus().equals(2)) {
                    orders.setStatus(3);
                    ordersService.updateById(orders);
                    return R.success("派送成功！");
                }else if (orders.getStatus().equals(3)) {
                    orders.setStatus(4);
                    ordersService.updateById(orders);
                    return R.success("订单已完成！");
                }

            }

        }
        return R.error("未知错误！");
    }


}