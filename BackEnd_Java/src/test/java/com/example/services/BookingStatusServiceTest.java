package com.example.services;

import com.example.dto.BookingStatusDTO;
import com.example.entities.BookingStatusMaster;
import com.example.repositories.BookingStatusRepository;
import com.example.services.impl.BookingStatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingStatusServiceTest {

    @Mock
    private BookingStatusRepository statusRepository;

    @InjectMocks
    private BookingStatusServiceImpl bookingStatusService;

    private BookingStatusMaster statusMaster;

    @BeforeEach
    void setUp() {
        statusMaster = new BookingStatusMaster();
        statusMaster.setId(1);
        statusMaster.setStatusName("CONFIRMED");
    }

    @Test
    @DisplayName("getAllStatuses: Returns all status DTOs")
    void testGetAllStatuses_Success() {
        when(statusRepository.findAll()).thenReturn(List.of(statusMaster));

        List<BookingStatusDTO> result = bookingStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatusName());
    }

    @Test
    @DisplayName("getStatusById: Existing ID returns status DTO")
    void testGetStatusById_Success() {
        when(statusRepository.findById(1)).thenReturn(Optional.of(statusMaster));

        BookingStatusDTO result = bookingStatusService.getStatusById(1);

        assertNotNull(result);
        assertEquals(1, result.getStatusId());
        assertEquals("CONFIRMED", result.getStatusName());
    }

    @Test
    @DisplayName("getStatusById: Non-existent ID throws RuntimeException")
    void testGetStatusById_NotFound_ThrowsException() {
        when(statusRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingStatusService.getStatusById(999));
        assertEquals("Status not found", ex.getMessage());
    }
}
