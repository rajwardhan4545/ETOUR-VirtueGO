package com.example.entities;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "booking_status_master")
public class BookingStatusMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id", nullable = false)
    private Integer id;

    @Column(name = "status_name", nullable = false, length = 50)
    private String statusName;

    @OneToMany(mappedBy = "status")
    private Set<BookingHeader> bookingHeaders = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Set<BookingHeader> getBookingHeaders() {
        return bookingHeaders;
    }

    public void setBookingHeaders(Set<BookingHeader> bookingHeaders) {
        this.bookingHeaders = bookingHeaders;
    }

}