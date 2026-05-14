package com.banana.hotel.module.order;

import com.banana.hotel.common.Result;
import com.banana.hotel.module.order.dto.CreateOrderRequest;
import com.banana.hotel.module.order.dto.PayRequest;
import com.banana.hotel.module.order.entity.BookingOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<BookingOrder> create(@Validated @RequestBody CreateOrderRequest request) {
        Long userId = getCurrentUserId();
        BookingOrder order = orderService.createOrder(userId, request);
        return Result.success(order);
    }

    @GetMapping("/{orderNo}")
    public Result<BookingOrder> getByOrderNo(@PathVariable String orderNo) {
        BookingOrder order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/my")
    public Result<List<BookingOrder>> myOrders() {
        Long userId = getCurrentUserId();
        return Result.success(orderService.getMyOrders(userId));
    }

    @PutMapping("/{orderNo}/pay")
    public Result<BookingOrder> pay(@PathVariable String orderNo,
                                    @RequestBody PayRequest request) {
        BookingOrder order = orderService.payOrder(orderNo, request.getPayMethod());
        return Result.success(order);
    }

    @PutMapping("/{orderNo}/cancel")
    public Result<BookingOrder> cancel(@PathVariable String orderNo) {
        BookingOrder order = orderService.cancelOrder(orderNo);
        return Result.success(order);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}
