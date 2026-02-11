package com.assignment.dispatch.service.impl;

import com.assignment.dispatch.dto.VehicleRequestDto;
import com.assignment.dispatch.exception.BadRequestException;
import com.assignment.dispatch.model.Vehicle;
import com.assignment.dispatch.repository.VehicleRepository;
import com.assignment.dispatch.service.VehicleService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private static final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

    // ========== REPOSITORY DEPENDENCY ========== //
    private final VehicleRepository vehicleRepository;

    // ========== SAVE INCOMING VEHICLES ========== //
    @Override
    @Transactional
    public void saveVehicles(List<VehicleRequestDto> vehicleRequestDtos) {
        validateUniqueVehicleIds(vehicleRequestDtos);

        List<Vehicle> vehicles = vehicleRequestDtos.stream()
                .map(this::mapToEntity)
                .toList();

        vehicleRepository.saveAll(vehicles);
        log.info("Saved {} vehicles successfully", vehicles.size());
    }

    // ========== DTO TO ENTITY MAPPING ========== //
    private Vehicle mapToEntity(VehicleRequestDto dto) {
        return Vehicle.builder()
                .vehicleId(dto.getVehicleId().trim())
                .capacity(dto.getCapacity())
                .currentLatitude(dto.getCurrentLatitude())
                .currentLongitude(dto.getCurrentLongitude())
                .currentAddress(dto.getCurrentAddress().trim())
                .build();
    }

    // ========== DUPLICATE VEHICLE ID CHECK ========== //
    private void validateUniqueVehicleIds(List<VehicleRequestDto> vehicleRequestDtos) {
        Set<String> uniqueIds = new HashSet<>();
        for (VehicleRequestDto vehicleRequestDto : vehicleRequestDtos) {
            String normalizedId = vehicleRequestDto.getVehicleId().trim();
            if (!uniqueIds.add(normalizedId)) {
                throw new BadRequestException("Duplicate vehicleId found in request: " + normalizedId);
            }
        }
    }
}
