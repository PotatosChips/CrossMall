package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Logistics;
import edu.cafuc.crossmall.pojo.Order;
import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.vo.OrderVO;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@PreAuthorize("isAuthenticated()")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result addOrder(Integer payType, String address, String receiverName, String receiverPhone,
                           @AuthenticationPrincipal User user) {
        Order order = orderService.addOrder(user.getId(), payType, 0, address, receiverName, receiverPhone);
        return Result.okData(order);
    }

    @GetMapping
    public Result getOrders(@AuthenticationPrincipal User user) {
        List<Order> orders = orderService.selectOrderByUserId(user.getId());
        return Result.okList(orders, orders.size());
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public Result getSellerOrders(@AuthenticationPrincipal User user) {
        List<Order> orders = orderService.selectOrderListForSeller(user.getId());
        return Result.okList(orders, orders.size());
    }

    @GetMapping("/{orderNo}/seller")
    @PreAuthorize("hasRole('SELLER')")
    public Result getSellerOrder(@PathVariable String orderNo, @AuthenticationPrincipal User user) {
        OrderVO orderVO = orderService.selectOrderForSeller(orderNo, user.getId());
        return orderVO != null ? Result.okData(orderVO) : Result.fail("获取订单失败");
    }

    @GetMapping("/{orderNo}")
    public Result getOrder(@PathVariable String orderNo, @AuthenticationPrincipal User user) {
        OrderVO orderVO = orderService.selectOrderByOrderNo(orderNo, user.getId());
        return orderVO != null ? Result.okData(orderVO) : Result.fail("获取订单失败");
    }

    @PutMapping("/{orderNo}/receiver")
    public Result updateReceiverInfo(@PathVariable String orderNo, Integer payType, String address,
                                     String receiverName, String receiverPhone,
                                     @AuthenticationPrincipal User user) {
        Integer rows = orderService.updateReceiverInfo(orderNo, user.getId(), payType, address, receiverName, receiverPhone);
        return rows > 0 ? Result.ok() : Result.fail("更新收货信息失败");
    }

    @PostMapping("/{orderNo}/pay")
    public Result payOrder(@PathVariable String orderNo, @AuthenticationPrincipal User user) {
        Integer rows = orderService.payOrder(orderNo, user.getId());
        return rows > 0 ? Result.ok() : Result.fail("支付失败，订单不存在或状态不允许");
    }

    @PostMapping("/{orderNo}/confirm")
    public Result confirmOrder(@PathVariable String orderNo, @AuthenticationPrincipal User user) {
        Integer rows = orderService.confirmReceipt(orderNo, user.getId());
        return rows > 0 ? Result.ok() : Result.fail("确认收货失败，订单可能未发货");
    }

    @DeleteMapping("/{orderNo}")
    public Result deleteOrder(@PathVariable String orderNo, @AuthenticationPrincipal User user) {
        Integer rows = orderService.deleteOrderByOrderNo(orderNo, user.getId());
        return rows > 0 ? Result.ok() : Result.fail("删除失败，订单不存在或状态不允许");
    }

    @PostMapping("/{orderNo}/ship")
    @PreAuthorize("hasRole('SELLER')")
    public Result shipOrder(@PathVariable String orderNo, String company,
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedArrival,
                            String content, @AuthenticationPrincipal User user) {
        Logistics logistics = orderService.shipOrder(orderNo, user.getId(), company, estimatedArrival, content);
        return Result.okData(logistics);
    }

    @PutMapping("/{orderNo}/logistics")
    @PreAuthorize("hasRole('SELLER')")
    public Result updateLogistics(@PathVariable String orderNo, String company,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedArrival,
                                  Integer status, @AuthenticationPrincipal User user) {
        Integer rows = orderService.updateLogistics(orderNo, user.getId(), company, estimatedArrival, status);
        return rows > 0 ? Result.ok() : Result.fail("更新物流失败");
    }

    @PostMapping("/{orderNo}/tracks")
    @PreAuthorize("hasRole('SELLER')")
    public Result addLogisticsTrack(@PathVariable String orderNo, String content,
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime trackTime,
                                    @AuthenticationPrincipal User user) {
        Integer rows = orderService.addLogisticsTrack(orderNo, user.getId(), content, trackTime);
        return rows > 0 ? Result.ok() : Result.fail("添加轨迹失败");
    }
}
