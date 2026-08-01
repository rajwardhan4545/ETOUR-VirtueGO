using Backend_dotnet.DTOs;

namespace Backend_dotnet.Services.Interfaces
{
    public interface IPaymentService
    {
        Task<PaymentDto> MakePayment(int bookingId, string paymentMode, string transactionRef, string paymentStatus, decimal amount);
        PaymentDto GetPaymentById(int paymentId);
        PaymentDto GetSuccessfulPayment(int bookingId);
        Task<int> GetPaymentIdByBookingAsync(int bookingId);
    }
}
