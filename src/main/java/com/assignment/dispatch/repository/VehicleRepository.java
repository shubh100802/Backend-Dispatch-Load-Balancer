package com.assignment.dispatch.repository;

import com.assignment.dispatch.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}
