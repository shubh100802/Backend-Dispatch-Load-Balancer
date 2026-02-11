package com.assignment.dispatch.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDispatchPlanDto {
    private String vehicleId;
    private Double totalLoad;
    private String totalDistance;
    private List<AssignedOrderDto> assignedOrders;
}
