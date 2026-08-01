using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Repositories.Implementations
{
    public class PassengerRepository 
        : GenericRepository<Passenger>, IPassengerRepository
    {
        public PassengerRepository(AppDbContext context) : base(context)
        {
        }

        // Equivalent of Spring JPA method: findByBookingId
        public async Task<IEnumerable<Passenger>> GetByBookingIdAsync(int bookingId)
        {
            return await _dbSet
                .AsNoTracking()
                .Where(p => p.booking_id == bookingId)
                .ToListAsync();
        }
    }
}
