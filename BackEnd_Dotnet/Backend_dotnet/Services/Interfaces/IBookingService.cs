using Backend_dotnet.DTOs;
using Backend_dotnet.DTOs.Common;

namespace Backend_dotnet.Services.Interfaces
{
    
    /// Service interface for booking business logic
    
    public interface IBookingService
    {
        Task<BookingResponseDto> GetByIdAsync(int id);
        Task<IEnumerable<BookingResponseDto>> GetAllAsync();
        Task<IEnumerable<BookingResponseDto>> GetCustomerBookingsAsync(int customerId);
        Task<BookingResponseDto> CreateAsync(BookingCreateDto dto);
        Task<int> GetPaymentStatusAsync(int bookingId);
    }
}