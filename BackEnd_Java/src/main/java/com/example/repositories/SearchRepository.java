package com.example.repositories;

import com.example.entities.TourMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<TourMaster, Integer> {

	// 🔹 SEARCH BY LOCATION (CATEGORY NAME OR CODE)
	@Query("""
			    SELECT DISTINCT c.id
			    FROM CategoryMaster c
			    WHERE (LOWER(c.categoryName) LIKE LOWER(CONCAT(:query, '%'))
			       OR LOWER(c.catCode) LIKE LOWER(CONCAT(:query, '%')))
			      AND c.jumpFlag = true
			""")
	List<Integer> getCategoryIdsByName(@Param("query") String query);

	// 🔹 SEARCH BY MAX COST
	@Query("""
			    SELECT DISTINCT c.category.id
			    FROM CostMaster c
			    WHERE c.singlePersonCost <= :maxCost
			      AND c.category.jumpFlag = true
			""")
	List<Integer> getCategoryIdsByMaxCost(@Param("maxCost") BigDecimal maxCost);

	// 🔹 SEARCH BY DATE RANGE
	@Query("""
			    SELECT DISTINCT d.category.id
			    FROM DepartureMaster d
			    WHERE d.departDate >= :fromDate
			      AND d.departDate <= :toDate
			      AND d.category.jumpFlag = true
			""")
	List<Integer> getCategoryIdsByDateRange(
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate);

}
