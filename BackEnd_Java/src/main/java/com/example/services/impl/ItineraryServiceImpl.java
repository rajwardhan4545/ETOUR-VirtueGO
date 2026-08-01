package com.example.services.impl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.CategoryMaster;
import com.example.entities.ItineraryMaster;
import com.example.repositories.CategoryRepository;
import com.example.repositories.ItineraryRepository;
import com.example.services.ItineraryService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Service
public class ItineraryServiceImpl implements ItineraryService {

	private final ItineraryRepository repository;
	private final CategoryRepository categoryRepo;

	public ItineraryServiceImpl(ItineraryRepository repository, CategoryRepository categoryRepo) {
		this.repository = repository;
		this.categoryRepo = categoryRepo;
	}

	@Override
	public void importCsv(MultipartFile file) {

		try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {

			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();

			List<String[]> rows = csvReader.readAll();

			List<ItineraryMaster> list = new ArrayList<>();

			for (String[] row : rows) {

				Integer categoryId = Integer.parseInt(row[0]);
				Integer dayNo = Integer.parseInt(row[1]);
				String detail = row[2];
				String image = row.length > 3 ? row[3] : null;

				CategoryMaster category = categoryRepo.findById(categoryId)
						.orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

				ItineraryMaster it = new ItineraryMaster();
				it.setCategory(category);
				it.setDayNo(dayNo);
				it.setItineraryDetail(detail);
				it.setDayWiseImage(image);

				list.add(it);
			}

//			repository.saveAll(list);

			for (ItineraryMaster it : list) {
				try {
					repository.save(it);
				} catch (org.springframework.dao.DataIntegrityViolationException ex) {
					// duplicate entry -> skip silently
					System.out.println(
							"duplicate skipped for category=" + it.getCategory().getId() + ", day=" + it.getDayNo());
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("CSV upload failed", e);
		}

	}

}
