package com.example.dto;

public class CustomerIdDTO {
	private Integer customerId;

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public CustomerIdDTO(Integer customerId) {
		super();
		this.customerId = customerId;
	}
}