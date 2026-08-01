package com.example.actuator;

import com.example.repositories.BookingRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "booking-stats")
public class BookingStatsEndpoint {

    private final BookingRepository bookingRepository;

    public BookingStatsEndpoint(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @ReadOperation
    public Map<String, Object> bookingStats() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalBookings", bookingRepository.count());
        return map;
    }
}
