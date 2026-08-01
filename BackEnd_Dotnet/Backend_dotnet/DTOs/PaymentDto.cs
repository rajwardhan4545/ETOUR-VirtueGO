namespace Backend_dotnet.DTOs
{
    public class PaymentDto
    {
        public int PaymentId { get; set; }
        public int BookingId { get; set; }
        public decimal PaymentAmount { get; set; }
        public string PaymentStatus { get; set; } = string.Empty;
        public string PaymentMode { get; set; } = "RAZORPAY";
        public string TransactionRef { get; set; } = string.Empty;
        public DateTime PaymentDate { get; set; }
    }
}
