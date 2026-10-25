package com.example.services;

import com.example.dto.PassengerDTO;
import com.example.entities.BookingHeader;
import com.example.entities.Passenger;
import com.example.repositories.BookingRepository;
import com.example.repositories.PassengerRepository;
import com.example.services.impl.PassengerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private BookingHeader booking;
    private Passenger passenger;
    private PassengerDTO passengerDTO;

    @BeforeEach
    void setUp() {
        booking = new BookingHeader();
        booking.setId(101);

        passenger = new Passenger();
        passenger.setId(1);
        passenger.setPaxName("Alice Smith");
        passenger.setPaxBirthdate(LocalDate.of(1995, 5, 20));
        passenger.setPaxType("Adult");
        passenger.setPaxAmount(new BigDecimal("5000"));
        passenger.setBooking(booking);

        passengerDTO = new PassengerDTO();
        passengerDTO.setBookingId(101);
        passengerDTO.setPaxName("Alice Smith");
        passengerDTO.setPaxBirthdate(LocalDate.of(1995, 5, 20));
        passengerDTO.setPaxAmount(new BigDecimal("5000"));
    }

    @Test
    @DisplayName("addPassenger: Valid booking creates and returns passenger")
    void testAddPassenger_Success() {
        when(bookingRepository.findById(101)).thenReturn(Optional.of(booking));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        PassengerDTO result = passengerService.addPassenger(passengerDTO);

        assertNotNull(result);
        assertEquals("Alice Smith", result.getPaxName());
        verify(passengerRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("addPassenger: Non-existent booking throws RuntimeException")
    void testAddPassenger_BookingNotFound_ThrowsException() {
        when(bookingRepository.findById(999)).thenReturn(Optional.empty());
        passengerDTO.setBookingId(999);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passengerService.addPassenger(passengerDTO));
        assertTrue(ex.getMessage().contains("Booking not found"));
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    @DisplayName("getPassengerById: Existing ID returns passenger DTO")
    void testGetPassengerById_Success() {
        when(passengerRepository.findById(1)).thenReturn(Optional.of(passenger));

        PassengerDTO result = passengerService.getPassengerById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Alice Smith", result.getPaxName());
    }

    @Test
    @DisplayName("getPassengerById: Missing ID throws RuntimeException")
    void testGetPassengerById_NotFound_ThrowsException() {
        when(passengerRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passengerService.getPassengerById(999));
        assertTrue(ex.getMessage().contains("Passenger not found"));
    }

    @Test
    @DisplayName("getPassengersByBookingId: Returns list of passengers")
    void testGetPassengersByBookingId_Success() {
        when(passengerRepository.findByBookingId(101)).thenReturn(List.of(passenger));

        List<PassengerDTO> result = passengerService.getPassengersByBookingId(101);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice Smith", result.get(0).getPaxName());
    }
}
