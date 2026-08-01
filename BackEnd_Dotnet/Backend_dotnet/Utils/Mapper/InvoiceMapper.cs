using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;

namespace Backend_dotnet.Utils.Mapper
{
    public static class InvoiceMapper
    {

        public static InvoiceDto ToInvoiceDto(payment_master payment)
        {
            var booking = payment.booking;
            var tour = booking.tour;
            var departure = tour.departure;

            var dto = new InvoiceDto();

            dto.BookingId = booking.booking_id;

            dto.CustomerName =
                booking.customer.first_name + " " +
                booking.customer.last_name;

            dto.TourName =
                $"{tour.category.category_name} | " +
                $"{departure.no_of_days} Days | " +
                $"{departure.depart_date} to {departure.end_date}";

            dto.Passengers = booking.no_of_pax;

            dto.BaseAmount = booking.tour_amount;
            dto.TaxAmount = booking.taxes;
            dto.TotalAmount = booking.total_amount;

            dto.PaymentMode = payment.payment_mode;
            dto.TransactionRef = payment.transaction_ref;

            dto.BookingDate = booking.booking_date;

            return dto;
        }
    }
}
