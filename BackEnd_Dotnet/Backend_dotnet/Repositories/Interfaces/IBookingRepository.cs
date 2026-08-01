using Backend_dotnet.Models.Entities;

namespace Backend_dotnet.Repositories.Interfaces
{
     
    /// Repository interface for booking operations
     
    public interface IBookingRepository : IGenericRepository<booking_header>
    {
         
        /// Get booking with all related data (customer, tour, passengers, payments)
         
        Task<booking_header> GetBookingWithDetailsAsync(int bookingId);

         
        /// Get all bookings for a specific customer
         
        Task<IEnumerable<booking_header>> GetCustomerBookingsAsync(int customerId);

         
        /// Get bookings by status
         
        Task<IEnumerable<booking_header>> GetBookingsByStatusAsync(int statusId);

         
        /// Get bookings for a specific tour
         
        Task<IEnumerable<booking_header>> GetTourBookingsAsync(int tourId);

         
        /// Get bookings within date range
         
        Task<IEnumerable<booking_header>> GetBookingsByDateRangeAsync(DateOnly startDate, DateOnly endDate);

         
        /// Get recent bookings (last N days)
         
        Task<IEnumerable<booking_header>> GetRecentBookingsAsync(int days = 30);

         
        /// Check if customer has active bookings
         
        Task<bool> HasActiveBookingsAsync(int customerId);

         
        /// Get booking count by status
        
        Task<Dictionary<string, int>> GetBookingCountByStatusAsync();

         
        /// Update booking status
         
        Task<bool> UpdateBookingStatusAsync(int bookingId, int statusId);
    }
}