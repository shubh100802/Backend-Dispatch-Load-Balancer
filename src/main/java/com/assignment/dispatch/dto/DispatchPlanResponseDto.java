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
public class DispatchPlanResponseDto {
    private List<VehicleDispatchPlanDto> dispatchPlan;
    private List<AssignedOrderDto> unassignedOrders;
}
