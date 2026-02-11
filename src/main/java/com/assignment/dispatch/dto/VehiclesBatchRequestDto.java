package com.assignment.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class VehiclesBatchRequestDto {

    @NotEmpty(message = "vehicles must not be empty")
    private List<@Valid VehicleRequestDto> vehicles;
}
