package com.example.services;

import org.springframework.web.multipart.MultipartFile;

public interface ItineraryService {

	void importCsv(MultipartFile file);
	
	
	
	
}
