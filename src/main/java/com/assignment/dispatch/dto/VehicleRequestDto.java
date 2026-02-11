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
public class VehicleRequestDto {

    @NotBlank(message = "vehicleId is required")
    private String vehicleId;

    @NotNull(message = "capacity is required")
    @Positive(message = "capacity must be positive")
    private Double capacity;

    @NotNull(message = "currentLatitude is required")
    private Double currentLatitude;

    @NotNull(message = "currentLongitude is required")
    private Double currentLongitude;

    @NotBlank(message = "currentAddress is required")
    private String currentAddress;
}
