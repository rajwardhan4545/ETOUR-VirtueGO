package com.example.services;

import com.example.dto.PassengerDTO;
import com.example.entities.BookingHeader;
import com.example.entities.Passenger;
import com.example.repositories.BookingRepository;
import com.example.repositories.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;


public interface PassengerService {

	PassengerDTO addPassenger(PassengerDTO dto);
    List<PassengerDTO> getPassengersByBookingId(Integer bookingId);
    PassengerDTO getPassengerById(Integer id);
   
}