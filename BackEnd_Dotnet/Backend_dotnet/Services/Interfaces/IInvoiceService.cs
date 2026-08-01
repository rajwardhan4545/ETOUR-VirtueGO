namespace Backend_dotnet.Services.Interfaces
{
    public interface IInvoiceService
    {
        /// <summary>
        /// Generate invoice PDF for a payment
        /// </summary>
        Task<byte[]> GenerateInvoiceAsync(int paymentId);

        /// <summary>
        /// Get payment ID for a booking (helper method)
        /// </summary>
        int? GetPaymentIdByBookingId(int bookingId);
    }
}
