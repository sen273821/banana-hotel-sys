package com.banana.hotel.module.order;

import com.banana.hotel.common.Result;
import com.banana.hotel.module.order.dto.CheckinRequest;
import com.banana.hotel.module.order.entity.BookingOrder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        IPage<BookingOrder> result = orderService.getAdminOrders(page, size, status, keyword);
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("current", result.getCurrent());
        return Result.success(data);
    }

    @PutMapping("/{orderNo}/checkin")
    public Result<BookingOrder> checkin(@PathVariable String orderNo,
                                        @RequestBody CheckinRequest request) {
        BookingOrder order = orderService.checkin(orderNo, request.getRoomId());
        return Result.success(order);
    }

    @PutMapping("/{orderNo}/checkout")
    public Result<BookingOrder> checkout(@PathVariable String orderNo) {
        BookingOrder order = orderService.checkout(orderNo);
        return Result.success(order);
    }
}
