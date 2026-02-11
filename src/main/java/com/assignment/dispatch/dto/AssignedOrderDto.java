package com.assignment.dispatch.dto;

import com.assignment.dispatch.model.Priority;
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
public class AssignedOrderDto {
    private String orderId;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double packageWeight;
    private Priority priority;
}
