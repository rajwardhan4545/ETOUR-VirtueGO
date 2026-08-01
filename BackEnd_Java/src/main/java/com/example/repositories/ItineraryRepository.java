package com.example.repositories;

import com.example.entities.ItineraryMaster;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryRepository extends JpaRepository<ItineraryMaster, Integer> {
    List<ItineraryMaster> findByCategoryId(Integer categoryId);
}
