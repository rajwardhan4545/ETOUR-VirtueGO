using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;

namespace Backend_dotnet.Services.Implementations
{
    public class CustomerService : ICustomerService
    {
        private readonly ICustomerRepository _customerRepository;
        private readonly ILogger<CustomerService> _logger;

        public CustomerService(
            ICustomerRepository customerRepository,
            ILogger<CustomerService> logger)
        {
            _customerRepository = customerRepository;
            _logger = logger;
        }

        /// <summary>
        /// Get customer profile by email
        /// </summary>
        public async Task<CustomerProfileDto?> GetProfileAsync(string email)
        {
            _logger.LogInformation("Fetching profile for: {Email}", email);

            var customer = await _customerRepository.GetByEmailAsync(email);
            if (customer == null)
            {
                _logger.LogWarning("Customer not found: {Email}", email);
                return null;
            }

            return MapToProfileDto(customer);
        }

        /// <summary>
        /// Get customer ID by email
        /// </summary>
        public async Task<int?> GetCustomerIdAsync(string email)
        {
            _logger.LogInformation("Fetching customer ID for: {Email}", email);

            var customer = await _customerRepository.GetByEmailAsync(email);
            return customer?.customer_id;
        }

        /// <summary>
        /// Update customer profile
        /// </summary>
        public async Task<CustomerProfileDto?> UpdateProfileAsync(string email, CustomerDTO dto)
        {
            _logger.LogInformation("Updating profile for: {Email}", email);

            var customer = await _customerRepository.GetByEmailAsync(email);
            if (customer == null)
            {
                _logger.LogWarning("Customer not found for update: {Email}", email);
                return null;
            }

            // Update fields
            customer.first_name = dto.FirstName ?? customer.first_name;
            customer.last_name = dto.LastName ?? customer.last_name;
            customer.phone = dto.Phone ?? customer.phone;
            customer.address = dto.Address ?? customer.address;
            customer.profile_completed = true;

            // Update password if provided
            if (!string.IsNullOrEmpty(dto.Password))
            {
                customer.password = BCrypt.Net.BCrypt.HashPassword(dto.Password);
            }

            var updated = await _customerRepository.UpdateAsync(customer);
            _logger.LogInformation("Profile updated for: {Email}", email);

            return MapToProfileDto(updated);
        }

        /// <summary>
        /// Change customer password
        /// </summary>
        public async Task<bool> ChangePasswordAsync(string email, ChangePasswordDto dto)
        {
            _logger.LogInformation("Changing password for: {Email}", email);

            var customer = await _customerRepository.GetByEmailAsync(email);
            if (customer == null)
            {
                _logger.LogWarning("Customer not found for password change: {Email}", email);
                return false;
            }

            // Verify old password
            if (string.IsNullOrEmpty(customer.password) || 
                !BCrypt.Net.BCrypt.Verify(dto.OldPassword, customer.password))
            {
                _logger.LogWarning("Invalid old password for: {Email}", email);
                throw new InvalidOperationException("Current password is incorrect");
            }

            // Update password
            customer.password = BCrypt.Net.BCrypt.HashPassword(dto.NewPassword);
            await _customerRepository.UpdateAsync(customer);

            _logger.LogInformation("Password changed for: {Email}", email);
            return true;
        }

        private CustomerProfileDto MapToProfileDto(customer_master customer)
        {
            return new CustomerProfileDto
            {
                CustomerId = customer.customer_id,
                Email = customer.email ?? "",
                FirstName = customer.first_name,
                LastName = customer.last_name,
                Phone = customer.phone,
                Address = customer.address,
                CustomerRole = customer.customer_role,
                AuthProvider = customer.auth_provider,
                ProfileCompleted = customer.profile_completed
            };
        }
    }
}
