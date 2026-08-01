package com.example.dto;

import java.time.LocalDate;

public class DepartureDTO {
    private Integer id;
    private LocalDate departDate;
    private LocalDate endDate;
    private Integer noOfDays;

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDepartDate() { return departDate; }
    public void setDepartDate(LocalDate departDate) { this.departDate = departDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getNoOfDays() { return noOfDays; }
    public void setNoOfDays(Integer noOfDays) { this.noOfDays = noOfDays; }
}
