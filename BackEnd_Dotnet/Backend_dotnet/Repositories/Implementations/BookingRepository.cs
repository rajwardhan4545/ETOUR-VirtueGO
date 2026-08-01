using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Repositories.Implementations
{
    
    /// Repository implementation for booking operations
    
    public class BookingRepository : GenericRepository<booking_header>, IBookingRepository
    {
        public BookingRepository(AppDbContext context) : base(context)
        {
        }

        public override async Task<IEnumerable<booking_header>> GetAllAsync()
        {
            return await _context.booking_header
                .Include(b => b.customer)
                .Include(b => b.tour)
                    .ThenInclude(t => t.category)
                .Include(b => b.tour)
                    .ThenInclude(t => t.tour_guide)
                .Include(b => b.status)
                .ToListAsync();
        }


        public async Task<booking_header> GetBookingWithDetailsAsync(int bookingId)
        {
            return await _dbSet
                .Include(b => b.customer)
                .Include(b => b.tour)
                    .ThenInclude(t => t.category)
                .Include(b => b.tour)
                    .ThenInclude(t => t.departure)
                .Include(b => b.tour)
                    .ThenInclude(t => t.tour_guide)
                .Include(b => b.status)
                .Include(b => b.passenger)
                .Include(b => b.payment_master)
                .FirstOrDefaultAsync(b => b.booking_id == bookingId);
        }

        public async Task<IEnumerable<booking_header>> GetCustomerBookingsAsync(int customerId)
        {
            return await _dbSet
                .Include(b => b.tour)
                    .ThenInclude(t => t.category)
                .Include(b => b.tour)
                    .ThenInclude(t => t.departure)
                .Include(b => b.tour)
                    .ThenInclude(t => t.tour_guide)
                .Include(b => b.status)
                .Where(b => b.customer_id == customerId)
                .OrderByDescending(b => b.booking_date)
                .ToListAsync();
        }

        public async Task<IEnumerable<booking_header>> GetBookingsByStatusAsync(int statusId)
        {
            return await _dbSet
                .Include(b => b.customer)
                .Include(b => b.tour)
                    .ThenInclude(t => t.category)
                .Include(b => b.status)
                .Where(b => b.status_id == statusId)
                .OrderByDescending(b => b.booking_date)
                .ToListAsync();
        }

        public async Task<IEnumerable<booking_header>> GetTourBookingsAsync(int tourId)
        {
            return await _dbSet
                .Include(b => b.customer)
                .Include(b => b.status)
                .Where(b => b.tour_id == tourId)
                .OrderByDescending(b => b.booking_date)
                .ToListAsync();
        }

        public async Task<IEnumerable<booking_header>> GetBookingsByDateRangeAsync(DateOnly startDate, DateOnly endDate)
        {
            return await _dbSet
                .Include(b => b.customer)
                .Include(b => b.tour)
                    .ThenInclude(t => t.category)
                .Include(b => b.status)
                .Where(b => b.booking_date >= startDate && b.booking_date <= endDate)
                .OrderByDescending(b => b.booking_date)
                .ToListAsync();
        }

        public async Task<IEnumerable<booking_header>> GetRecentBookingsAsync(int days = 30)
        {
            var cutoffDate = DateOnly.FromDateTime(DateTime.Now.AddDays(-days));

            return await _dbSet
                .Include(b => b.customer)
                .Include(b => b.tour)
                    .ThenInclude(t => t.category)
                .Include(b => b.status)
                .Where(b => b.booking_date >= cutoffDate)
                .OrderByDescending(b => b.booking_date)
                .ToListAsync();
        }

        public async Task<bool> HasActiveBookingsAsync(int customerId)
        {
            return await _dbSet.AnyAsync(b =>
                b.customer_id == customerId &&
                (b.status_id == 1 || b.status_id == 2)); // Pending or Confirmed
        }

        public async Task<Dictionary<string, int>> GetBookingCountByStatusAsync()
        {
            return await _dbSet
                .Include(b => b.status)
                .GroupBy(b => b.status.status_name)
                .Select(g => new { Status = g.Key, Count = g.Count() })
                .ToDictionaryAsync(x => x.Status, x => x.Count);
        }

        public async Task<bool> UpdateBookingStatusAsync(int bookingId, int statusId)
        {
            var booking = await _dbSet.FindAsync(bookingId);
            if (booking == null)
                return false;

            booking.status_id = statusId;
            await _context.SaveChangesAsync();
            return true;
        }
    }
}