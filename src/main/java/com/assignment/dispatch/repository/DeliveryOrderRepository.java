package com.assignment.dispatch.repository;

import com.assignment.dispatch.model.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, String> {
}
