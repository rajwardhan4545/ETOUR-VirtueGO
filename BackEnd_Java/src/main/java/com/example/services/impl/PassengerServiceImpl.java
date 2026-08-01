package com.example.services.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import com.example.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.PassengerDTO;
import com.example.entities.BookingHeader;
import com.example.entities.Passenger;
import com.example.repositories.BookingRepository;
import com.example.repositories.PassengerRepository;

@Service
public class PassengerServiceImpl implements PassengerService {

	@Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private BookingRepository bookingRepository; 

    public PassengerDTO addPassenger(PassengerDTO dto) {
     
        BookingHeader booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + dto.getBookingId()));

        Passenger passenger = new Passenger();
        passenger.setPaxName(dto.getPaxName());
        passenger.setPaxBirthdate(dto.getPaxBirthdate());
        
        passenger.setBooking(booking); 
        
        passenger.setPaxType(determinePaxType(dto.getPaxBirthdate()));
        passenger.setPaxAmount(dto.getPaxAmount());
        
        Passenger saved = passengerRepository.save(passenger);
        return convertToDTO(saved);
    }
   
    public List<PassengerDTO> getPassengersByBookingId(Integer bookingId) {
        List<Passenger> passengers = passengerRepository.findByBookingId(bookingId);
        return passengers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PassengerDTO getPassengerById(Integer id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger not found with ID: " + id));
        return convertToDTO(passenger);
    }
    

    private String determinePaxType(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age >= 12) return "Adult";
        if (age >= 1) return "Child";
        return "Infant";
    }

    private PassengerDTO convertToDTO(Passenger p) {
        PassengerDTO dto = new PassengerDTO();
        dto.setId(p.getId());
        dto.setPaxName(p.getPaxName());
        dto.setPaxBirthdate(p.getPaxBirthdate());
        dto.setPaxType(p.getPaxType());
        dto.setPaxAmount(p.getPaxAmount());
        dto.setBookingId(p.getBooking().getId());
        return dto;
    }
}
