package com.example.services;

import java.util.List;

import com.example.dto.BookingStatusDTO;

public interface BookingStatusService {
	
	List<BookingStatusDTO> getAllStatuses();
	
	BookingStatusDTO getStatusById(Integer statusId);
	
	

}
