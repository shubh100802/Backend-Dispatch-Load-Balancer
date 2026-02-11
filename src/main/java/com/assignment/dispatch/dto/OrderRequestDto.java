package com.assignment.dispatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class OrderRequestDto {

    @NotBlank(message = "orderId is required")
    private String orderId;

    @NotNull(message = "latitude is required")
    private Double latitude;

    @NotNull(message = "longitude is required")
    private Double longitude;

    @NotBlank(message = "address is required")
    private String address;

    @NotNull(message = "packageWeight is required")
    @Positive(message = "packageWeight must be positive")
    private Double packageWeight;

    @NotBlank(message = "priority is required")
    private String priority;
}
