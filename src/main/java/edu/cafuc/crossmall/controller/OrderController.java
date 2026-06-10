package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Logistics;
import edu.cafuc.crossmall.pojo.Order;
import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.vo.OrderVO;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /** 下单（从购物车生成 order + order_item） */
    @PostMapping
    public Result addOrder(Integer payType, String address, String receiverName, String receiverPhone, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        try {
            Order order = orderService.addOrder(user.getId(), payType, 0, address, receiverName, receiverPhone);
            return Result.okData(order);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /** 买家订单列表 */
    @GetMapping
    public Result getOrders(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        List<Order> orders = orderService.selectOrderByUserId(user.getId());
        return Result.okList(orders, orders.size());
    }

    /** 卖家订单列表（字面量路径须在 /{orderNo} 之前） */
    @GetMapping("/seller")
    public Result getSellerOrders(HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        List<Order> orders = orderService.selectOrderListForSeller(user.getId());
        return Result.okList(orders, orders.size());
    }

    /** 卖家订单详情 */
    @GetMapping("/{orderNo}/seller")
    public Result getSellerOrder(@PathVariable String orderNo, HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        OrderVO orderVO = orderService.selectOrderForSeller(orderNo, user.getId());
        return orderVO != null ? Result.okData(orderVO) : Result.fail("获取订单失败");
    }

    /** 买家订单详情 */
    @GetMapping("/{orderNo}")
    public Result getOrder(@PathVariable String orderNo, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        OrderVO orderVO = orderService.selectOrderByOrderNo(orderNo, user.getId());
        return orderVO != null ? Result.okData(orderVO) : Result.fail("获取订单失败");
    }

    /** 改收货信息（仅 status=0） */
    @PutMapping("/{orderNo}/receiver")
    public Result updateReceiverInfo(@PathVariable String orderNo, Integer payType, String address,
                                     String receiverName, String receiverPhone, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        Integer rows = orderService.updateReceiverInfo(orderNo, user.getId(), payType, address, receiverName, receiverPhone);
        return rows > 0 ? Result.ok() : Result.fail("更新收货信息失败");
    }

    /** 买家模拟支付（0→1） */
    @PostMapping("/{orderNo}/pay")
    public Result payOrder(@PathVariable String orderNo, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        try {
            Integer rows = orderService.payOrder(orderNo, user.getId());
            return rows > 0 ? Result.ok() : Result.fail("支付失败，订单不存在或状态不允许");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /** 确认收货（2→3） */
    @PostMapping("/{orderNo}/confirm")
    public Result confirmOrder(@PathVariable String orderNo, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        Integer rows = orderService.confirmReceipt(orderNo, user.getId());
        return rows > 0 ? Result.ok() : Result.fail("确认收货失败，订单可能未发货");
    }

    /** 删除待支付订单（status=0） */
    @DeleteMapping("/{orderNo}")
    public Result deleteOrder(@PathVariable String orderNo, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        Integer rows = orderService.deleteOrderByOrderNo(orderNo, user.getId());
        return rows > 0 ? Result.ok() : Result.fail("删除失败，订单不存在或状态不允许");
    }

    /** 卖家发货：自动生成运单号，订单 1→2 */
    @PostMapping("/{orderNo}/ship")
    public Result shipOrder(@PathVariable String orderNo, String company,
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedArrival,
                            String content, HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        try {
            Logistics logistics = orderService.shipOrder(orderNo, user.getId(), company, estimatedArrival, content);
            return Result.okData(logistics);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /** 卖家补全/更新物流信息 */
    @PutMapping("/{orderNo}/logistics")
    public Result updateLogistics(@PathVariable String orderNo, String company,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedArrival,
                                  Integer status, HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        try {
            Integer rows = orderService.updateLogistics(orderNo, user.getId(), company, estimatedArrival, status);
            return rows > 0 ? Result.ok() : Result.fail("更新物流失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /** 卖家追加物流轨迹 */
    @PostMapping("/{orderNo}/tracks")
    public Result addLogisticsTrack(@PathVariable String orderNo, String content,
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime trackTime,
                                    HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        try {
            Integer rows = orderService.addLogisticsTrack(orderNo, user.getId(), content, trackTime);
            return rows > 0 ? Result.ok() : Result.fail("添加轨迹失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    private Result sellerAuth(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        if (user.getRole() == null || user.getRole() != 1) {
            return Result.fail("仅卖家可操作");
        }
        return null;
    }
}
