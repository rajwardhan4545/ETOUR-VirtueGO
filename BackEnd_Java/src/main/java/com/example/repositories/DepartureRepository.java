package com.example.repositories;

import com.example.entities.DepartureMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartureRepository extends JpaRepository<DepartureMaster, Integer> {
    List<DepartureMaster> findByCategoryId(Integer categoryId);
}
