package com.banana.hotel.module.order.mapper;

import com.banana.hotel.module.order.entity.BookingOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<BookingOrder> {
}
