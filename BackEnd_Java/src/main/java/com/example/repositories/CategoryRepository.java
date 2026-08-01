package com.example.repositories;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entities.CategoryMaster;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryMaster, Integer> {

	List<CategoryMaster> findBySubcatCode(String subcatCode);
	
	@Query("SELECT c.id FROM CategoryMaster c WHERE c.subcatCode = :subcatCode")
    List<Integer> findCategoryIdsBySubcatCode(@Param("subcatCode") String subcatCode);
	
	@Query("""
		    SELECT c.id
		    FROM CategoryMaster c
		    WHERE c.subcatCode = '^'
		""")
		List<Integer> findHomeCategoryIds();


}