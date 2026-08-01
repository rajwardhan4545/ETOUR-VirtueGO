using Backend_dotnet.Models.Entities;

namespace Backend_dotnet.Repositories.Interfaces
{
    public interface IPassengerRepository : IGenericRepository<Passenger>
    {
        // Equivalent to Spring Data JPA: findByBookingId(Integer bookingId)
        Task<IEnumerable<Passenger>> GetByBookingIdAsync(int bookingId);
    }
}
