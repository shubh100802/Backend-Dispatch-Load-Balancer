package com.assignment.dispatch.service;

import com.assignment.dispatch.dto.VehicleRequestDto;
import java.util.List;

public interface VehicleService {
    void saveVehicles(List<VehicleRequestDto> vehicleRequestDtos);
}
