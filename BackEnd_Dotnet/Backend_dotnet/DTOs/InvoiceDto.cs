namespace Backend_dotnet.DTOs
{
    public class InvoiceDto
    {
        public int BookingId { get; set; }

        public string CustomerName { get; set; } = null!;

        public string TourName { get; set; } = null!;

        public int Passengers { get; set; }

        public decimal BaseAmount { get; set; }

        public decimal TaxAmount { get; set; }

        public decimal? TotalAmount { get; set; }

        public string PaymentMode { get; set; } = null!;

        public string? TransactionRef { get; set; }

        public DateOnly BookingDate { get; set; }
    }

}
