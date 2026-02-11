package com.assignment.dispatch.service.impl;

import com.assignment.dispatch.dto.AssignedOrderDto;
import com.assignment.dispatch.dto.DispatchPlanResponseDto;
import com.assignment.dispatch.dto.VehicleDispatchPlanDto;
import com.assignment.dispatch.model.DeliveryOrder;
import com.assignment.dispatch.model.Priority;
import com.assignment.dispatch.model.Vehicle;
import com.assignment.dispatch.repository.DeliveryOrderRepository;
import com.assignment.dispatch.repository.VehicleRepository;
import com.assignment.dispatch.service.DispatchPlanService;
import com.assignment.dispatch.util.HaversineUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DispatchPlanServiceImpl implements DispatchPlanService {
    private static final Logger log = LoggerFactory.getLogger(DispatchPlanServiceImpl.class);

    // ========== REPOSITORY DEPENDENCIES ========== //
    private final DeliveryOrderRepository deliveryOrderRepository;
    private final VehicleRepository vehicleRepository;

    // ========== MAIN DISPATCH PLAN FLOW ========== //
    @Override
    @Transactional(readOnly = true)
    public DispatchPlanResponseDto generateDispatchPlan() {
        List<DeliveryOrder> orders = deliveryOrderRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .sorted(Comparator.comparing(Vehicle::getVehicleId))
                .toList();
        log.info("Generating plan for {} orders and {} vehicles", orders.size(), vehicles.size());
        List<DeliveryOrder> sortedOrders = orders.stream()
                .sorted(Comparator.comparingInt(order -> priorityRank(order.getPriority())))
                .toList();

        List<VehicleState> vehicleStates = vehicles.stream()
                .map(VehicleState::fromVehicle)
                .toList();

        List<AssignedOrderDto> unassignedOrders = new ArrayList<>();
        for (DeliveryOrder order : sortedOrders) {
            VehicleState bestVehicle = findBestVehicle(vehicleStates, order);
            if (bestVehicle == null) {
                unassignedOrders.add(mapOrder(order));
                continue;
            }

            assignOrder(bestVehicle, order);
        }

        List<VehicleDispatchPlanDto> dispatchPlan = vehicleStates.stream()
                .map(this::mapVehiclePlan)
                .toList();

        log.info("Generated plan with {} vehicles and {} unassigned orders", dispatchPlan.size(), unassignedOrders.size());
        return DispatchPlanResponseDto.builder()
                .dispatchPlan(dispatchPlan)
                .unassignedOrders(unassignedOrders)
                .build();
    }

    // ========== PICK NEAREST FEASIBLE VEHICLE ========== //
    private VehicleState findBestVehicle(List<VehicleState> vehicleStates, DeliveryOrder order) {
        VehicleState bestVehicle = null;
        double minDistance = Double.MAX_VALUE;

        for (VehicleState vehicleState : vehicleStates) {
            if (vehicleState.getRemainingCapacity() < order.getPackageWeight()) {
                continue;
            }

            double distance = HaversineUtil.calculateDistance(
                    vehicleState.getCurrentLatitude(),
                    vehicleState.getCurrentLongitude(),
                    order.getLatitude(),
                    order.getLongitude()
            );

            if (distance < minDistance) {
                minDistance = distance;
                bestVehicle = vehicleState;
            }
        }

        return bestVehicle;
    }

    // ========== APPLY ASSIGNMENT AND UPDATE VEHICLE STATE ========== //
    private void assignOrder(VehicleState vehicleState, DeliveryOrder order) {
        double distance = HaversineUtil.calculateDistance(
                vehicleState.getCurrentLatitude(),
                vehicleState.getCurrentLongitude(),
                order.getLatitude(),
                order.getLongitude()
        );

        vehicleState.setRemainingCapacity(vehicleState.getRemainingCapacity() - order.getPackageWeight());
        vehicleState.setTotalLoad(vehicleState.getTotalLoad() + order.getPackageWeight());
        vehicleState.setTotalDistance(vehicleState.getTotalDistance() + distance);
        vehicleState.setCurrentLatitude(order.getLatitude());
        vehicleState.setCurrentLongitude(order.getLongitude());
        vehicleState.getAssignedOrders().add(mapOrder(order));
    }

    // ========== VEHICLE PLAN RESPONSE MAPPING ========== //
    private VehicleDispatchPlanDto mapVehiclePlan(VehicleState vehicleState) {
        return VehicleDispatchPlanDto.builder()
                .vehicleId(vehicleState.getVehicleId())
                .totalLoad(vehicleState.getTotalLoad())
                .totalDistance(String.format(Locale.US, "%.2f km", vehicleState.getTotalDistance()))
                .assignedOrders(vehicleState.getAssignedOrders())
                .build();
    }

    // ========== ORDER RESPONSE MAPPING ========== //
    private AssignedOrderDto mapOrder(DeliveryOrder order) {
        return AssignedOrderDto.builder()
                .orderId(order.getOrderId())
                .latitude(order.getLatitude())
                .longitude(order.getLongitude())
                .address(order.getAddress())
                .packageWeight(order.getPackageWeight())
                .priority(order.getPriority())
                .build();
    }

    // ========== PRIORITY WEIGHTING ========== //
    private int priorityRank(Priority priority) {
        return switch (priority) {
            case HIGH -> 0;
            case MEDIUM -> 1;
            case LOW -> 2;
        };
    }

    // ========== IN-MEMORY VEHICLE STATE DURING PLANNING ========== //
    @Getter
    @Setter
    private static class VehicleState {
        private String vehicleId;
        private Double remainingCapacity;
        private Double currentLatitude;
        private Double currentLongitude;
        private Double totalLoad;
        private Double totalDistance;
        private List<AssignedOrderDto> assignedOrders;

        // ========== VEHICLE ENTITY TO STATE OBJECT ========== //
        private static VehicleState fromVehicle(Vehicle vehicle) {
            VehicleState state = new VehicleState();
            state.setVehicleId(vehicle.getVehicleId());
            state.setRemainingCapacity(vehicle.getCapacity());
            state.setCurrentLatitude(vehicle.getCurrentLatitude());
            state.setCurrentLongitude(vehicle.getCurrentLongitude());
            state.setTotalLoad(0.0);
            state.setTotalDistance(0.0);
            state.setAssignedOrders(new ArrayList<>());
            return state;
        }
    }
}
