using Backend_dotnet.DTOs;

namespace Backend_dotnet.Services.Interfaces
{
    public interface ICustomerService
    {
        Task<CustomerProfileDto?> GetProfileAsync(string email);
        Task<int?> GetCustomerIdAsync(string email);
        Task<CustomerProfileDto?> UpdateProfileAsync(string email, CustomerDTO dto);
        Task<bool> ChangePasswordAsync(string email, ChangePasswordDto dto);
    }
}
