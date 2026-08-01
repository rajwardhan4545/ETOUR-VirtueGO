using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Utilities;
using Backend_dotnet.Utilities.Helpers;
using Google.Apis.Auth;
using Microsoft.Extensions.Configuration;

namespace Backend_dotnet.Services.Implementations
{
    public class AuthService
    {
        private readonly ICustomerRepository _customerRepository;
        private readonly JwtService _jwtService;
        private readonly EmailHelper _emailHelper;
        private readonly ILogger<AuthService> _logger;

        private readonly IConfiguration _configuration;

        public AuthService(
            ICustomerRepository customerRepository,
            JwtService jwtService,
            EmailHelper emailHelper,
            ILogger<AuthService> logger,
            IConfiguration configuration)
        {
            _customerRepository = customerRepository;
            _jwtService = jwtService;
            _emailHelper = emailHelper;
            _logger = logger;
            _configuration = configuration;
        }

        /// <summary>
        /// Register a new customer
        /// </summary>
        public async Task<CustomerProfileDto> RegisterAsync(RegisterRequestDto dto)
        {
            _logger.LogInformation("Attempting to register user with email: {Email}", dto.Email);

            // Check if email already exists
            if (await _customerRepository.ExistsAsync(dto.Email))
            {
                _logger.LogWarning("Registration failed: Email {Email} already exists", dto.Email);
                throw new InvalidOperationException("Email already registered");
            }

            // Create new customer
            var customer = new customer_master
            {
                email = dto.Email,
                first_name = dto.FirstName,
                last_name = dto.LastName,
                phone = dto.Phone,
                address = dto.Address,
                password = BCrypt.Net.BCrypt.HashPassword(dto.Password),
                customer_role = "CUSTOMER",
                auth_provider = "LOCAL",
                profile_completed = false
            };

            var saved = await _customerRepository.AddAsync(customer);
            _logger.LogInformation("User registered successfully: {Email}", saved.email);

            return MapToProfileDto(saved);
        }

        /// <summary>
        /// Login with email and password
        /// </summary>
        public async Task<string?> LoginAsync(LoginRequestDto dto)
        {
            _logger.LogInformation("Attempting to login user: {Email}", dto.Email);

            var customer = await _customerRepository.GetByEmailAsync(dto.Email);

            if (customer == null)
            {
                _logger.LogWarning("Login failed: User not found for {Email}", dto.Email);
                return null;
            }

            // Check if user registered with OAuth
            if (customer.auth_provider != "LOCAL")
            {
                _logger.LogWarning("Login failed: User {Email} registered with {Provider}", dto.Email, customer.auth_provider);
                throw new InvalidOperationException($"Please login using {customer.auth_provider}");
            }

            // Verify password
            if (string.IsNullOrEmpty(customer.password) || !BCrypt.Net.BCrypt.Verify(dto.Password, customer.password))
            {
                _logger.LogWarning("Login failed: Invalid password for {Email}", dto.Email);
                return null;
            }

            // Generate JWT token
            var token = _jwtService.GenerateToken(customer.email!, customer.customer_role);
            _logger.LogInformation("Login successful for: {Email}", dto.Email);

            return token;
        }

        /// <summary>
        /// Send password reset email
        /// </summary>
        public async Task SendResetTokenAsync(string email)
        {
            _logger.LogInformation("Password reset requested for email: {Email}", email);

            var customer = await _customerRepository.GetByEmailAsync(email);

            if (customer == null)
            {
                _logger.LogWarning("Password reset failed: User not found for {Email}", email);
                throw new KeyNotFoundException("User not found");
            }

            // Generate reset token
            var token = _jwtService.GenerateResetToken(email);

            // Send email
            var resetLink = $"http://localhost:5173/reset-password?token={token}";
            
            await _emailHelper.SendEmailAsync(
                email,
                "Reset Your Password",
                $"Click the link below to reset your password:\n\n{resetLink}\n\nThis link is valid for 15 minutes."
            );

            _logger.LogInformation("Password reset email sent to: {Email}", email);
        }

        /// <summary>
        /// Reset password using token
        /// </summary>
        public async Task ResetPasswordAsync(ResetPasswordDto dto)
        {
            _logger.LogInformation("Attempting to reset password");

            // Validate reset token
            if (!_jwtService.IsResetTokenValid(dto.Token))
            {
                throw new InvalidOperationException("Invalid or expired reset token");
            }

            // Extract email from token
            var email = _jwtService.ExtractEmail(dto.Token);
            if (string.IsNullOrEmpty(email))
            {
                throw new InvalidOperationException("Invalid reset token");
            }

            var customer = await _customerRepository.GetByEmailAsync(email);
            if (customer == null)
            {
                throw new KeyNotFoundException("User not found");
            }

            // Update password
            customer.password = BCrypt.Net.BCrypt.HashPassword(dto.NewPassword);
            await _customerRepository.UpdateAsync(customer);

            // Send confirmation email
            await _emailHelper.SendEmailAsync(
                email,
                "Password Updated",
                "Your password has been successfully updated."
            );

            _logger.LogInformation("Password reset successful for: {Email}", email);
        }

        /// <summary>
        /// Handle Google OAuth login/register
        /// </summary>
        /// <summary>
        /// Handle Google OAuth login/register
        /// </summary>
        public async Task<string> HandleGoogleLoginAsync(string googleIdToken)
        {
            _logger.LogInformation("Google OAuth login attempted");

            GoogleJsonWebSignature.Payload payload;
            try
            {
                var settings = new GoogleJsonWebSignature.ValidationSettings()
                {
                    Audience = new List<string>() { _configuration["Google:ClientId"] }
                };
                payload = await GoogleJsonWebSignature.ValidateAsync(googleIdToken, settings);
            }
            catch (Exception ex)
            {
                _logger.LogWarning("Google token validation failed: {Message}", ex.Message);
                throw new InvalidOperationException("Invalid Google token");
            }

            var email = payload.Email;
            var firstName = payload.GivenName;
            var lastName = payload.FamilyName;

            _logger.LogInformation("Google token validated for: {Email}", email);

            var customer = await _customerRepository.GetByEmailAsync(email);

            if (customer == null)
            {
                // Register new user via Google
                customer = new customer_master
                {
                    email = email,
                    first_name = firstName ?? "Google User",
                    last_name = lastName ?? "",
                    password = null, // No password for OAuth users
                    customer_role = "CUSTOMER",
                    auth_provider = "GOOGLE",
                    profile_completed = false
                };

                customer = await _customerRepository.AddAsync(customer);
                _logger.LogInformation("New Google user registered: {Email}", email);
            }
            else if (customer.auth_provider != "GOOGLE")
            {
                // Optional: Link account or merge?
                // For now, strict check as per original design, but allowing if email matches might be better UX if we supported linking.
                // Current policy: Strict check
                _logger.LogWarning("Google login failed: User {Email} registered with {Provider}", email, customer.auth_provider);
                throw new InvalidOperationException($"This email is registered with {customer.auth_provider}. Please login using that method.");
            }

            // Generate JWT token
            return _jwtService.GenerateToken(email, customer.customer_role);
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