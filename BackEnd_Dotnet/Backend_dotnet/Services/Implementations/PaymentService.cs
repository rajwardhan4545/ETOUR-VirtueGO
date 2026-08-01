using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;

namespace Backend_dotnet.Services.Implementations
{
    public class PaymentService : IPaymentService
    {
        private readonly IPaymentRepository _paymentRepository;

        private readonly IBookingRepository _bookingRepository;

        public PaymentService(IPaymentRepository paymentRepository, IBookingRepository bookingRepository)
        {
            _paymentRepository = paymentRepository;
            _bookingRepository = bookingRepository;
        }

        public async Task<PaymentDto> MakePayment(int bookingId, string paymentMode, string transactionRef, string paymentStatus, decimal amount)
        {
            var booking = await _bookingRepository.GetByIdAsync(bookingId);
            if (booking == null)
                throw new Exception("Booking not found");

            if (_paymentRepository.ExistsByBookingIdAndStatus(bookingId, "SUCCESS"))
                throw new Exception("Payment already completed for this booking");

            var existingPayment = _paymentRepository.FindByTransactionRef(transactionRef);
            if (existingPayment != null)
                throw new Exception("Duplicate transaction reference");

            // Assuming total_amount is nullable decimal? based on booking_header.cs
            if (booking.total_amount != amount) 
                throw new Exception("Payment amount mismatch");

            var payment = new payment_master
            {
                booking_id = bookingId,
                payment_amount = amount,
                payment_status = paymentStatus,
                transaction_ref = transactionRef,
                payment_mode = paymentMode,
                payment_date = DateTime.Now
            };

            _paymentRepository.Save(payment);
            return MapToDto(payment);
        }

        public PaymentDto GetPaymentById(int paymentId)
        {
            var payment = _paymentRepository
                .FindAllByBookingId(paymentId)
                .FirstOrDefault();

            if (payment == null)
                throw new Exception("Payment not found");

            return MapToDto(payment);
        }

        public PaymentDto GetSuccessfulPayment(int bookingId)
        {
            var payment = _paymentRepository
                .FindByBookingIdAndStatus(bookingId, "SUCCESS");

            if (payment == null)
                throw new Exception("No successful payment");

            return MapToDto(payment);
        }

        public async Task<int> GetPaymentIdByBookingAsync(int bookingId)
        {
            var payment = await Task.Run(() => _paymentRepository
                .FindByBookingIdAndStatus(bookingId, "SUCCESS"));

            if (payment == null)
            {
                // Fallback: Try to find ANY payment to generate invoice (or throw)
                var anyPayment = _paymentRepository.FindAllByBookingId(bookingId).FirstOrDefault();
                if (anyPayment != null) return anyPayment.payment_id;

                throw new Exception("No payment record found for this booking.");
            }

            return payment.payment_id;
        }

        private PaymentDto MapToDto(payment_master p)
        {
            return new PaymentDto
            {
                PaymentId = p.payment_id,
                BookingId = p.booking_id,
                PaymentAmount = p.payment_amount,
                PaymentStatus = p.payment_status,
                PaymentMode = p.payment_mode,
                TransactionRef = p.transaction_ref ?? string.Empty,
                PaymentDate = p.payment_date
            };
        }
    }
}
