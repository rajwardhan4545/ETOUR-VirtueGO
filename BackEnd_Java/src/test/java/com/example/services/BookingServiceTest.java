package com.example.services;

import com.example.dto.BookingCreateRequestDTO;
import com.example.dto.BookingResponseDTO;
import com.example.entities.*;
import com.example.repositories.BookingRepository;
import com.example.services.impl.BookingServicesImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private EntityManager entityManager;

    @InjectMocks private BookingServicesImpl bookingService;

    @Test
    void testSaveBooking() {

        BookingCreateRequestDTO dto = new BookingCreateRequestDTO();
        dto.setCustomerId(10);
        dto.setTourId(20);
        dto.setStatusId(1);
        dto.setNoOfPax(3);
        dto.setTourAmount(new BigDecimal("10000"));
        dto.setTaxes(new BigDecimal("1500"));

        CustomerMaster customer = new CustomerMaster();
        TourMaster tour = new TourMaster();
        BookingStatusMaster status = new BookingStatusMaster();

        when(entityManager.getReference(CustomerMaster.class, 10)).thenReturn(customer);
        when(entityManager.getReference(TourMaster.class, 20)).thenReturn(tour);
        when(entityManager.getReference(BookingStatusMaster.class, 1)).thenReturn(status);

        BookingHeader saved = new BookingHeader();
        saved.setId(99);
        saved.setBookingDate(LocalDate.now());
        saved.setNoOfPax(3);
        saved.setTotalAmount(new BigDecimal("11500"));
        saved.setStatus(status);

        when(bookingRepository.save(any(BookingHeader.class))).thenReturn(saved);

        BookingResponseDTO resp = bookingService.saveBooking(dto);

        assertEquals(99, resp.getBookingId());
        assertEquals(3, resp.getNoOfPax());
        assertEquals("11500", resp.getTotalAmount().toString());

        verify(bookingRepository, times(1)).save(any(BookingHeader.class));
    }
}

