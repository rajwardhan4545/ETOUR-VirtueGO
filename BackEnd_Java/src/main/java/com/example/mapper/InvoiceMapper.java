package com.example.mapper;

import com.example.dto.InvoiceDTO;
import com.example.entities.BookingHeader;
import com.example.entities.DepartureMaster;
import com.example.entities.PaymentMaster;
import com.example.entities.TourMaster;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InvoiceMapper {

	@Transactional(readOnly = true)
	public InvoiceDTO toInvoiceDTO(PaymentMaster payment) {

	    BookingHeader booking = payment.getBooking();
	    TourMaster tour = booking.getTour();          // ✅ must exist
	    DepartureMaster departure = tour.getDeparture(); // ✅ now works

	    InvoiceDTO dto = new InvoiceDTO();

	    dto.setCustomerName(
	            booking.getCustomer().getFirstName() + " " +
	            booking.getCustomer().getLastName()
	    );

	    dto.setTourName(
	            tour.getCategory().getCategoryName()
	            + " | " + departure.getNoOfDays() + " Days"
	            + " | " + departure.getDepartDate()
	            + " to " + departure.getEndDate()
	    );

	    dto.setPassengers(booking.getNoOfPax());
	    dto.setBaseAmount(booking.getTourAmount());
	    dto.setTaxAmount(booking.getTaxes());
	    dto.setTotalAmount(booking.getTotalAmount());

	    return dto;
	}

}
