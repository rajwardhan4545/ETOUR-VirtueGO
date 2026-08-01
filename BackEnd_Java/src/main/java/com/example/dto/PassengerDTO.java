package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PassengerDTO {
	private Integer id;
	private Integer bookingId;
	private String paxName;
	private LocalDate paxBirthdate;
	private String paxType; // Adult, Child, Infant
	private BigDecimal paxAmount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public String getPaxName() {
		return paxName;
	}

	public void setPaxName(String paxName) {
		this.paxName = paxName;
	}

	public LocalDate getPaxBirthdate() {
		return paxBirthdate;
	}

	public void setPaxBirthdate(LocalDate paxBirthdate) {
		this.paxBirthdate = paxBirthdate;
	}

	public String getPaxType() {
		return paxType;
	}

	public void setPaxType(String paxType) {
		this.paxType = paxType;
	}

	public BigDecimal getPaxAmount() {
		return paxAmount;
	}

	public void setPaxAmount(BigDecimal paxAmount) {
		this.paxAmount = paxAmount;
	}

}
