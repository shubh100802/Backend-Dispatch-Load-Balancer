package com.assignment.dispatch.service.impl;

import com.assignment.dispatch.dto.DispatchPlanResponseDto;
import com.assignment.dispatch.model.DeliveryOrder;
import com.assignment.dispatch.model.Priority;
import com.assignment.dispatch.model.Vehicle;
import com.assignment.dispatch.repository.DeliveryOrderRepository;
import com.assignment.dispatch.repository.VehicleRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchPlanServiceImplTest {

    @Mock
    private DeliveryOrderRepository deliveryOrderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DispatchPlanServiceImpl dispatchPlanService;

    @Test
    void shouldPrioritizeHighOrderBeforeLowOrder() {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VEH001")
                .capacity(10.0)
                .currentLatitude(28.6139)
                .currentLongitude(77.2090)
                .currentAddress("Connaught Place")
                .build();

        DeliveryOrder highPriority = DeliveryOrder.builder()
                .orderId("ORD-HIGH")
                .latitude(28.7041)
                .longitude(77.1025)
                .address("Karol Bagh")
                .packageWeight(10.0)
                .priority(Priority.HIGH)
                .build();

        DeliveryOrder lowPriority = DeliveryOrder.builder()
                .orderId("ORD-LOW")
                .latitude(28.6139)
                .longitude(77.2090)
                .address("Connaught Place")
                .packageWeight(10.0)
                .priority(Priority.LOW)
                .build();

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(deliveryOrderRepository.findAll()).thenReturn(List.of(lowPriority, highPriority));

        DispatchPlanResponseDto response = dispatchPlanService.generateDispatchPlan();

        assertEquals(1, response.getDispatchPlan().size());
        assertEquals(1, response.getDispatchPlan().get(0).getAssignedOrders().size());
        assertEquals("ORD-HIGH", response.getDispatchPlan().get(0).getAssignedOrders().get(0).getOrderId());
        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals("ORD-LOW", response.getUnassignedOrders().get(0).getOrderId());
    }

    @Test
    void shouldAssignOrderToNearestVehicle() {
        Vehicle nearVehicle = Vehicle.builder()
                .vehicleId("VEH-NEAR")
                .capacity(100.0)
                .currentLatitude(12.9716)
                .currentLongitude(77.5946)
                .currentAddress("Bangalore")
                .build();

        Vehicle farVehicle = Vehicle.builder()
                .vehicleId("VEH-FAR")
                .capacity(100.0)
                .currentLatitude(13.0827)
                .currentLongitude(80.2707)
                .currentAddress("Chennai")
                .build();

        DeliveryOrder order = DeliveryOrder.builder()
                .orderId("ORD001")
                .latitude(12.9721)
                .longitude(77.5933)
                .address("MG Road")
                .packageWeight(10.0)
                .priority(Priority.HIGH)
                .build();

        when(vehicleRepository.findAll()).thenReturn(List.of(nearVehicle, farVehicle));
        when(deliveryOrderRepository.findAll()).thenReturn(List.of(order));

        DispatchPlanResponseDto response = dispatchPlanService.generateDispatchPlan();

        assertEquals("ORD001", response.getDispatchPlan().stream()
                .filter(plan -> "VEH-NEAR".equals(plan.getVehicleId()))
                .findFirst()
                .orElseThrow()
                .getAssignedOrders()
                .get(0)
                .getOrderId());
    }

    @Test
    void shouldKeepOrderUnassignedWhenNoVehicleCanCarryWeight() {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VEH001")
                .capacity(15.0)
                .currentLatitude(28.6139)
                .currentLongitude(77.2090)
                .currentAddress("Delhi")
                .build();

        DeliveryOrder heavyOrder = DeliveryOrder.builder()
                .orderId("ORD-HEAVY")
                .latitude(28.7041)
                .longitude(77.1025)
                .address("Karol Bagh")
                .packageWeight(20.0)
                .priority(Priority.HIGH)
                .build();

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(deliveryOrderRepository.findAll()).thenReturn(List.of(heavyOrder));

        DispatchPlanResponseDto response = dispatchPlanService.generateDispatchPlan();

        assertTrue(response.getDispatchPlan().get(0).getAssignedOrders().isEmpty());
        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals("ORD-HEAVY", response.getUnassignedOrders().get(0).getOrderId());
    }

    @Test
    void shouldReturnAllOrdersAsUnassignedWhenNoVehiclesAvailable() {
        DeliveryOrder orderOne = DeliveryOrder.builder()
                .orderId("ORD001")
                .latitude(28.6139)
                .longitude(77.2090)
                .address("Connaught Place")
                .packageWeight(10.0)
                .priority(Priority.HIGH)
                .build();

        DeliveryOrder orderTwo = DeliveryOrder.builder()
                .orderId("ORD002")
                .latitude(28.7041)
                .longitude(77.1025)
                .address("Karol Bagh")
                .packageWeight(8.0)
                .priority(Priority.MEDIUM)
                .build();

        when(vehicleRepository.findAll()).thenReturn(List.of());
        when(deliveryOrderRepository.findAll()).thenReturn(List.of(orderOne, orderTwo));

        DispatchPlanResponseDto response = dispatchPlanService.generateDispatchPlan();

        assertTrue(response.getDispatchPlan().isEmpty());
        assertEquals(2, response.getUnassignedOrders().size());
    }

    @Test
    void shouldReturnEmptyAssignmentsWhenNoOrdersAvailable() {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VEH001")
                .capacity(100.0)
                .currentLatitude(28.6139)
                .currentLongitude(77.2090)
                .currentAddress("Delhi")
                .build();

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(deliveryOrderRepository.findAll()).thenReturn(List.of());

        DispatchPlanResponseDto response = dispatchPlanService.generateDispatchPlan();

        assertEquals(1, response.getDispatchPlan().size());
        assertTrue(response.getDispatchPlan().get(0).getAssignedOrders().isEmpty());
        assertTrue(response.getUnassignedOrders().isEmpty());
    }

    @Test
    void shouldLeaveOrderUnassignedWhenAllVehiclesAreFull() {
        Vehicle vehicleOne = Vehicle.builder()
                .vehicleId("VEH001")
                .capacity(5.0)
                .currentLatitude(28.6139)
                .currentLongitude(77.2090)
                .currentAddress("Delhi")
                .build();

        Vehicle vehicleTwo = Vehicle.builder()
                .vehicleId("VEH002")
                .capacity(6.0)
                .currentLatitude(28.5355)
                .currentLongitude(77.3910)
                .currentAddress("Noida")
                .build();

        DeliveryOrder order = DeliveryOrder.builder()
                .orderId("ORD001")
                .latitude(28.7041)
                .longitude(77.1025)
                .address("Karol Bagh")
                .packageWeight(10.0)
                .priority(Priority.HIGH)
                .build();

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicleOne, vehicleTwo));
        when(deliveryOrderRepository.findAll()).thenReturn(List.of(order));

        DispatchPlanResponseDto response = dispatchPlanService.generateDispatchPlan();

        assertTrue(response.getDispatchPlan().stream().allMatch(plan -> plan.getAssignedOrders().isEmpty()));
        assertEquals(1, response.getUnassignedOrders().size());
    }
}
