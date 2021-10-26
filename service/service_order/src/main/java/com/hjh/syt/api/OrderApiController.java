package com.hjh.syt.api;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hjh.syt.AuthContextHolder;
import com.hjh.syt.Result;
import com.hjh.syt.ResultCodeEnum;
import com.hjh.syt.entity.order.OrderInfo;
import com.hjh.syt.enums.OrderStatusEnum;
import com.hjh.syt.service.OrderService;
import com.hjh.syt.vo.order.OrderCountQueryVo;
import com.hjh.syt.vo.order.OrderQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Resource
    private OrderService orderService;

    //生成挂号订单
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result savaOrders(@PathVariable String scheduleId,
                             @PathVariable Long patientId) {
        Long orderId = orderService.saveOrder(scheduleId,patientId);
        return Result.builds(orderId, ResultCodeEnum.SUCCESS);
    }

    //根据订单id查询订单详情
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId) {
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.builds(orderInfo,ResultCodeEnum.SUCCESS);
    }

    //订单列表（条件查询带分页）
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo, HttpServletRequest request) {
        //设置当前用户id
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel =
                orderService.selectPage(pageParam,orderQueryVo);
        return Result.builds(pageModel,ResultCodeEnum.SUCCESS);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public Result getStatusList() {
        return Result.builds(OrderStatusEnum.getStatusList(),ResultCodeEnum.SUCCESS);
    }

    //取消预约
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId) {
        Boolean isOrder = orderService.cancelOrder(orderId);
        return Result.builds(isOrder,ResultCodeEnum.SUCCESS);
    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }
}



