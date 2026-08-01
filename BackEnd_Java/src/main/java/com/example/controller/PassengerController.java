package com.example.controller;

import com.example.dto.PassengerDTO;
import com.example.services.PassengerService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @PostMapping("/add")
    public ResponseEntity<PassengerDTO> addPassenger(@RequestBody PassengerDTO passengerDTO) {
        return ResponseEntity.ok(passengerService.addPassenger(passengerDTO));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PassengerDTO>> getPassengersByBooking(@PathVariable Integer bookingId) {
        return ResponseEntity.ok(passengerService.getPassengersByBookingId(bookingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDTO> getPassenger(@PathVariable Integer id) {
        return ResponseEntity.ok(passengerService.getPassengerById(id));
    }
}