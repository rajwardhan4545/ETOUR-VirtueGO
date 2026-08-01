package com.example.entities;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tour_master")
public class TourMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryMaster category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_id", nullable = false)
    private DepartureMaster departure;

    @OneToMany(mappedBy = "tour")
    private Set<BookingHeader> bookingHeaders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "tour")
    private Set<TourGuide> tourGuides = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CategoryMaster getCategory() {
        return category;
    }

    public void setCategory(CategoryMaster category) {
        this.category = category;
    }

    public DepartureMaster getDeparture() {
        return departure;
    }

    public void setDeparture(DepartureMaster departure) {
        this.departure = departure;
    }

    public Set<BookingHeader> getBookingHeaders() {
        return bookingHeaders;
    }

    public void setBookingHeaders(Set<BookingHeader> bookingHeaders) {
        this.bookingHeaders = bookingHeaders;
    }

    public Set<TourGuide> getTourGuides() {
        return tourGuides;
    }

    public void setTourGuides(Set<TourGuide> tourGuides) {
        this.tourGuides = tourGuides;
    }

}