package com.example.repositories;

import com.example.entities.BookingHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingHeader, Integer> {

    @Modifying
    @Query("update BookingHeader b set b.status.id = :statusId where b.id = :bookingId")
    void updateBookingStatus(@Param("bookingId") Integer bookingId,
                             @Param("statusId") Integer statusId);

    // Find bookings by customer
    List<BookingHeader> findByCustomerId(Integer customerId);
    
    @Query("select b.status.id from BookingHeader b where b.id = :bookingId")
    Integer findStatusIdByBookingId(@Param("bookingId") Integer bookingId);

}