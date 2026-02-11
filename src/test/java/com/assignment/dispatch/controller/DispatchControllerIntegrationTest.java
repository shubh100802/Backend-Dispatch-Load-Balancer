package com.assignment.dispatch.controller;

import com.assignment.dispatch.model.DeliveryOrder;
import com.assignment.dispatch.model.Priority;
import com.assignment.dispatch.model.Vehicle;
import com.assignment.dispatch.repository.DeliveryOrderRepository;
import com.assignment.dispatch.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class DispatchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        deliveryOrderRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    @Test
    void shouldReturnDispatchPlanFromEndpoint() throws Exception {
        vehicleRepository.save(Vehicle.builder()
                .vehicleId("VEH001")
                .capacity(100.0)
                .currentLatitude(12.9716)
                .currentLongitude(77.5946)
                .currentAddress("Bangalore")
                .build());

        deliveryOrderRepository.save(DeliveryOrder.builder()
                .orderId("ORD001")
                .latitude(12.9721)
                .longitude(77.5933)
                .address("MG Road")
                .packageWeight(10.0)
                .priority(Priority.HIGH)
                .build());
        mockMvc.perform(get("/api/dispatch/plan").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan[0].vehicleId").value("VEH001"))
                .andExpect(jsonPath("$.dispatchPlan[0].assignedOrders[0].orderId").value("ORD001"))
                .andExpect(jsonPath("$.unassignedOrders").isArray());
    }
}
