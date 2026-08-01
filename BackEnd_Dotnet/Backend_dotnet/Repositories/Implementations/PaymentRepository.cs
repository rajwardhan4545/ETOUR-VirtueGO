using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Repositories.Implementations
{
    public class PaymentRepository : GenericRepository<payment_master>,IPaymentRepository
    {
        

        public PaymentRepository(AppDbContext context):base(context)
        {
            
        }

        public bool ExistsByBookingIdAndStatus(int bookingId, string status)
        {
            return _context.payment_master
                .Any(p => p.booking_id == bookingId && p.payment_status == status);
        }

        public payment_master? FindByBookingIdAndStatus(int bookingId, string status)
        {
            return _context.payment_master
                .FirstOrDefault(p => p.booking_id == bookingId && p.payment_status == status);
        }

        public payment_master? FindByTransactionRef(string transactionRef)
        {
            return _context.payment_master
                .FirstOrDefault(p => p.transaction_ref == transactionRef);
        }

        public List<payment_master> FindAllByBookingId(int bookingId)
        {
            return _context.payment_master
                .Where(p => p.booking_id == bookingId)
                .ToList();
        }

        public payment_master Save(payment_master payment)
        {
            _context.payment_master.Update(payment);
            _context.SaveChanges();
            return payment;
        }

        public async Task<payment_master?> GetPaymentWithBookingAndCustomerAsync(int paymentId)
        {
            return await _context.payment_master
                .Include(p => p.booking)
                    .ThenInclude(b => b.customer)
                .FirstOrDefaultAsync(p => p.payment_id == paymentId);
        }
    }
}
