package com.assignment.dispatch.service;

import com.assignment.dispatch.dto.OrderRequestDto;
import java.util.List;

public interface OrderService {
    void saveOrders(List<OrderRequestDto> orderRequestDtos);
}
