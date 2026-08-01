using Backend_dotnet.Models.Entities;

namespace Backend_dotnet.Repositories.Interfaces
{
    public interface ICustomerRepository
    {
        Task<customer_master?> GetByEmailAsync(string email);
        Task<customer_master?> GetByIdAsync(int id);
        Task<bool> ExistsAsync(string email);
        Task<customer_master> AddAsync(customer_master customer);
        Task<customer_master> UpdateAsync(customer_master customer);
    }
}
