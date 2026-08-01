package com.example.repositories;

import com.example.entities.TourMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<TourMaster, Integer> {

    // existing
    List<TourMaster> findByCategory_Id(Integer categoryId);

    List<TourMaster> findByCategory_IdIn(List<Integer> categoryIds);

    // ✅ REQUIRED FOR BOOKING
    Optional<TourMaster> findByCategory_IdAndDeparture_Id(
            Integer categoryId,
            Integer departureId
    );
}
