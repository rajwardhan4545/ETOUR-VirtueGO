package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.services.ItineraryService;

@RestController
@RequestMapping("/api/admin/itineraries")
public class ItineraryUploadController {

	private final ItineraryService iService;

	public ItineraryUploadController(ItineraryService iService) {
		this.iService = iService;
	}

	// http://localhost:8080/api/admin/itineraries/upload-csv
	@PostMapping("/upload-csv")
	public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {

		iService.importCsv(file);

		return ResponseEntity.ok("CSV uploaded successfully");
	}
}
