package com.example.repositories;

import com.example.entities.CostMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostRepository extends JpaRepository<CostMaster, Integer> {
    List<CostMaster> findByCategoryId(Integer categoryId);
}
