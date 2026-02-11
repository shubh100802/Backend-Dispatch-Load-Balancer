package com.assignment.dispatch.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HaversineUtilTest {

    @Test
    void shouldReturnZeroWhenCoordinatesAreSame() {
        double distance = HaversineUtil.calculateDistance(12.9716, 77.5946, 12.9716, 77.5946);
        assertEquals(0.0, distance, 0.000001);
    }

    @Test
    void shouldCalculateKnownDistanceWithinAcceptableRange() {
        double distance = HaversineUtil.calculateDistance(40.7128, -74.0060, 51.5074, -0.1278);
        assertTrue(distance > 5500 && distance < 5650,
                "Distance should be within expected range, actual: " + distance);
    }
}
