package com.assignment.dispatch.controller;

import com.assignment.dispatch.dto.ApiResponseDto;
import com.assignment.dispatch.dto.DispatchPlanResponseDto;
import com.assignment.dispatch.dto.OrdersBatchRequestDto;
import com.assignment.dispatch.dto.VehiclesBatchRequestDto;
import com.assignment.dispatch.service.DispatchPlanService;
import com.assignment.dispatch.service.OrderService;
import com.assignment.dispatch.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {
    private static final Logger log = LoggerFactory.getLogger(DispatchController.class);

    // ========== SERVICE DEPENDENCIES ========== //
    private final OrderService orderService;
    private final VehicleService vehicleService;
    private final DispatchPlanService dispatchPlanService;

    // ========== CREATE ORDERS ENDPOINT ========== //
    @PostMapping("/orders")
    public ResponseEntity<ApiResponseDto> saveOrders(@Valid @RequestBody OrdersBatchRequestDto requestDto) {
        log.info("Received {} orders for ingestion", requestDto.getOrders().size());
        orderService.saveOrders(requestDto.getOrders());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.builder()
                .message("Delivery orders accepted.")
                .status("success")
                .build());
    }

    // ========== CREATE VEHICLES ENDPOINT ========== //
    @PostMapping("/vehicles")
    public ResponseEntity<ApiResponseDto> saveVehicles(@Valid @RequestBody VehiclesBatchRequestDto requestDto) {
        log.info("Received {} vehicles for ingestion", requestDto.getVehicles().size());
        vehicleService.saveVehicles(requestDto.getVehicles());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.builder()
                .message("Vehicle details accepted.")
                .status("success")
                .build());
    }

    // ========== DISPATCH PLAN ENDPOINT ========== //
    @GetMapping("/plan")
    public ResponseEntity<DispatchPlanResponseDto> getDispatchPlan() {
        log.info("Generating dispatch plan");
        return ResponseEntity.ok(dispatchPlanService.generateDispatchPlan());
    }
}
