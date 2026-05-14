package com.banana.hotel.module.order;

import com.banana.hotel.module.order.dto.CreateOrderRequest;
import com.banana.hotel.module.order.entity.BookingOrder;
import com.banana.hotel.module.order.mapper.OrderMapper;
import com.banana.hotel.module.room.entity.Room;
import com.banana.hotel.module.room.entity.RoomType;
import com.banana.hotel.module.room.mapper.RoomMapper;
import com.banana.hotel.module.room.mapper.RoomTypeMapper;
import com.banana.hotel.module.room.service.RoomStatusService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final RoomMapper roomMapper;
    private final RoomStatusService roomStatusService;

    public OrderService(OrderMapper orderMapper, RoomTypeMapper roomTypeMapper,
                        RoomMapper roomMapper, RoomStatusService roomStatusService) {
        this.orderMapper = orderMapper;
        this.roomTypeMapper = roomTypeMapper;
        this.roomMapper = roomMapper;
        this.roomStatusService = roomStatusService;
    }

    @Transactional
    public BookingOrder createOrder(Long userId, CreateOrderRequest request) {
        RoomType roomType = roomTypeMapper.selectById(request.getRoomTypeId());
        if (roomType == null) {
            throw new RuntimeException("房型不存在");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new RuntimeException("退房日期必须晚于入住日期");
        }

        BigDecimal totalPrice = roomType.getPrice().multiply(BigDecimal.valueOf(nights));

        BookingOrder order = new BookingOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setRoomTypeId(request.getRoomTypeId());
        order.setGuestName(request.getGuestName());
        order.setGuestPhone(request.getGuestPhone());
        order.setCheckInDate(request.getCheckInDate());
        order.setCheckOutDate(request.getCheckOutDate());
        order.setNights((int) nights);
        order.setTotalPrice(totalPrice);
        order.setStatus(0);
        orderMapper.insert(order);
        return order;
    }

    @Transactional
    public BookingOrder payOrder(String orderNo, String payMethod) {
        BookingOrder order = getByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许支付");
        }
        order.setStatus(1);
        order.setPayMethod(payMethod);
        order.setPaidAt(LocalDateTime.now());
        orderMapper.updateById(order);
        return order;
    }

    @Transactional
    public BookingOrder cancelOrder(String orderNo) {
        BookingOrder order = getByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() > 1) {
            throw new RuntimeException("订单状态不允许取消");
        }
        order.setStatus(4);
        orderMapper.updateById(order);
        return order;
    }

    @Transactional
    public BookingOrder checkin(String orderNo, Long roomId) {
        BookingOrder order = getByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许入住");
        }

        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (room.getStatus() != 0) {
            throw new RuntimeException("房间不可用");
        }

        order.setRoomId(roomId);
        order.setStatus(2);
        orderMapper.updateById(order);

        roomStatusService.changeStatus(roomId, "OCCUPIED", "订单入住: " + orderNo);
        return order;
    }

    @Transactional
    public BookingOrder checkout(String orderNo) {
        BookingOrder order = getByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不允许退房");
        }

        order.setStatus(3);
        orderMapper.updateById(order);

        if (order.getRoomId() != null) {
            roomStatusService.changeStatus(order.getRoomId(), "DIRTY", "订单退房: " + orderNo);
        }
        return order;
    }

    public BookingOrder getByOrderNo(String orderNo) {
        return orderMapper.selectOne(
                new LambdaQueryWrapper<BookingOrder>().eq(BookingOrder::getOrderNo, orderNo));
    }

    public List<BookingOrder> getMyOrders(Long userId) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<BookingOrder>()
                        .eq(BookingOrder::getUserId, userId)
                        .orderByDesc(BookingOrder::getCreatedAt));
    }

    public IPage<BookingOrder> getAdminOrders(int page, int size, Integer status, String keyword) {
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(BookingOrder::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(BookingOrder::getOrderNo, keyword)
                    .or().like(BookingOrder::getGuestName, keyword)
                    .or().like(BookingOrder::getGuestPhone, keyword));
        }
        wrapper.orderByDesc(BookingOrder::getCreatedAt);
        return orderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public int getAvailableRoomCount(Long roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        int totalRooms = roomMapper.selectCount(
                new LambdaQueryWrapper<Room>().eq(Room::getRoomTypeId, roomTypeId)).intValue();

        int bookedRooms = orderMapper.selectCount(
                new LambdaQueryWrapper<BookingOrder>()
                        .eq(BookingOrder::getRoomTypeId, roomTypeId)
                        .in(BookingOrder::getStatus, 1, 2)
                        .lt(BookingOrder::getCheckInDate, checkOut)
                        .gt(BookingOrder::getCheckOutDate, checkIn)).intValue();

        return Math.max(0, totalRooms - bookedRooms);
    }

    private String generateOrderNo() {
        return "BH" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
