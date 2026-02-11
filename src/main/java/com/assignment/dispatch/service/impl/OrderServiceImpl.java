package com.assignment.dispatch.service.impl;

import com.assignment.dispatch.dto.OrderRequestDto;
import com.assignment.dispatch.exception.BadRequestException;
import com.assignment.dispatch.model.DeliveryOrder;
import com.assignment.dispatch.model.Priority;
import com.assignment.dispatch.repository.DeliveryOrderRepository;
import com.assignment.dispatch.service.OrderService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    // ========== REPOSITORY DEPENDENCY ========== //
    private final DeliveryOrderRepository deliveryOrderRepository;

    // ========== SAVE INCOMING ORDERS ========== //
    @Override
    @Transactional
    public void saveOrders(List<OrderRequestDto> orderRequestDtos) {
        validateUniqueOrderIds(orderRequestDtos);

        List<DeliveryOrder> deliveryOrders = orderRequestDtos.stream()
                .map(this::mapToEntity)
                .toList();

        deliveryOrderRepository.saveAll(deliveryOrders);
        log.info("Saved {} orders successfully", deliveryOrders.size());
    }

    // ========== DTO TO ENTITY MAPPING ========== //
    private DeliveryOrder mapToEntity(OrderRequestDto dto) {
        return DeliveryOrder.builder()
                .orderId(dto.getOrderId().trim())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress().trim())
                .packageWeight(dto.getPackageWeight())
                .priority(parsePriority(dto.getPriority()))
                .build();
    }

    // ========== PRIORITY PARSING ========== //
    private Priority parsePriority(String priorityRaw) {
        try {
            return Priority.valueOf(priorityRaw.trim().toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid priority. Allowed values: HIGH, MEDIUM, LOW");
        }
    }

    // ========== DUPLICATE ORDER ID CHECK ========== //
    private void validateUniqueOrderIds(List<OrderRequestDto> orderRequestDtos) {
        Set<String> uniqueIds = new HashSet<>();
        for (OrderRequestDto orderRequestDto : orderRequestDtos) {
            String normalizedId = orderRequestDto.getOrderId().trim();
            if (!uniqueIds.add(normalizedId)) {
                throw new BadRequestException("Duplicate orderId found in request: " + normalizedId);
            }
        }
    }
}
