using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Repositories.Implementations
{
    public class CustomerRepository : ICustomerRepository
    {
        private readonly AppDbContext _context;

        public CustomerRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<customer_master?> GetByEmailAsync(string email)
        {
            return await _context.customer_master
                .FirstOrDefaultAsync(c => c.email == email);
        }

        public async Task<customer_master?> GetByIdAsync(int id)
        {
            return await _context.customer_master.FindAsync(id);
        }

        public async Task<bool> ExistsAsync(string email)
        {
            return await _context.customer_master
                .AnyAsync(c => c.email == email);
        }

        public async Task<customer_master> AddAsync(customer_master customer)
        {
            _context.customer_master.Add(customer);
            await _context.SaveChangesAsync();
            return customer;
        }

        public async Task<customer_master> UpdateAsync(customer_master customer)
        {
            _context.customer_master.Update(customer);
            await _context.SaveChangesAsync();
            return customer;
        }
    }
}
