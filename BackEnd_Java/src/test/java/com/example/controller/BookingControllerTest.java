package com.example.controller;

import com.example.dto.BookingCreateRequestDTO;
import com.example.dto.BookingResponseDTO;
import com.example.services.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void testCreateBooking() throws Exception {

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(777);
        response.setBookingDate(LocalDate.now());
        response.setTotalAmount(new BigDecimal("15000"));

        Mockito.when(bookingService.saveBooking(Mockito.any())).thenReturn(response);

        BookingCreateRequestDTO req = new BookingCreateRequestDTO();
        req.setCustomerId(11);
        req.setTourId(22);
        req.setStatusId(1);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(777));
    }

    @Test
    void testGetBooking() throws Exception {

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(1001);

        Mockito.when(bookingService.getBookingById(1001)).thenReturn(response);

        mockMvc.perform(get("/api/bookings/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1001));
    }
}
