package com.example.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.example.services.BookingStatusService;
import org.springframework.stereotype.Service;

import com.example.dto.BookingStatusDTO;
import com.example.entities.BookingStatusMaster;
import com.example.repositories.BookingStatusRepository;

@Service
public class BookingStatusServiceImpl  implements BookingStatusService {

    private final BookingStatusRepository statusRepository;

    public BookingStatusServiceImpl(BookingStatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }
    @Override
    public List<BookingStatusDTO> getAllStatuses(){

        return statusRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BookingStatusDTO getStatusById(Integer statusId) {
        BookingStatusMaster status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));
        return mapToDTO(status);
    }

    private BookingStatusDTO mapToDTO(BookingStatusMaster status) {

        BookingStatusDTO dto= new BookingStatusDTO();
        dto.setStatusId(status.getId());
        dto.setStatusName(status.getStatusName());
        return dto;
    }

}