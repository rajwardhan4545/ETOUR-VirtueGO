package com.example.services.impl;

import com.example.dto.BookingCreateRequestDTO;
import com.example.dto.BookingResponseDTO;
import com.example.dto.TourGuideDTO;
import com.example.entities.*;
import com.example.repositories.BookingRepository;
import com.example.services.BookingService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookingServicesImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;

    public BookingServicesImpl(
            BookingRepository bookingRepository,
            EntityManager entityManager) {
        this.bookingRepository = bookingRepository;
        this.entityManager = entityManager;
    }

    // NEW METHOD
    @Override
    public List<BookingResponseDTO> getBookingsByCustomerId(Integer customerId) {
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDTO saveBooking(BookingCreateRequestDTO dto) {

        // ✅ ID-only references (NO FETCH)
        CustomerMaster customerRef = entityManager.getReference(CustomerMaster.class, dto.getCustomerId());

        TourMaster tourRef = entityManager.getReference(TourMaster.class, dto.getTourId());

        BookingStatusMaster statusRef = entityManager.getReference(BookingStatusMaster.class, dto.getStatusId());

        // ✅ Safe BigDecimal handling
        BigDecimal tourAmount = dto.getTourAmount() != null ? dto.getTourAmount() : BigDecimal.ZERO;

        BigDecimal taxes = dto.getTaxes() != null ? dto.getTaxes() : BigDecimal.ZERO;

        // ✅ Create BookingHeader
        BookingHeader booking = new BookingHeader();
        booking.setBookingDate(LocalDate.now());
        booking.setCustomer(customerRef);
        booking.setTour(tourRef);
        booking.setStatus(statusRef);
        booking.setNoOfPax(dto.getNoOfPax());
        booking.setTourAmount(tourAmount);
        booking.setTaxes(taxes);

        // ✅ Save
        BookingHeader saved = bookingRepository.save(booking);

        // ✅ Map to response DTO
        return mapToResponseDTO(saved);
    }

    @Override
    public BookingResponseDTO getBookingById(Integer bookingId) {

        BookingHeader booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return mapToResponseDTO(booking);
    }

    // 🔁 COMMON MAPPER
    private BookingResponseDTO mapToResponseDTO(BookingHeader booking) {

        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setNoOfPax(booking.getNoOfPax());
        dto.setTotalAmount(booking.getTotalAmount());

        // ✅ STATUS STRING
        if (booking.getStatus() != null) {
            dto.setStatus(booking.getStatus().getStatusName());
        }

        // ✅ TOUR GUIDES
        if (booking.getTour() != null && booking.getTour().getTourGuides() != null) {
            List<TourGuideDTO> guides = booking.getTour().getTourGuides().stream()
                    .map(g -> {
                        TourGuideDTO gDto = new TourGuideDTO();
                        gDto.setId(g.getId());
                        gDto.setName(g.getName());
                        gDto.setEmail(g.getEmail());
                        gDto.setPhone(g.getPhone());
                        return gDto;
                    })
                    .collect(Collectors.toList());
            dto.setGuides(guides);
        }

        return dto;
    }

    @Override
    public Integer getPaymentStatus(Integer bookingId) {

        Integer statusId = bookingRepository.findStatusIdByBookingId(bookingId);

        if (statusId == null) {
            throw new RuntimeException("Booking not found");
        }

        return statusId;
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

}