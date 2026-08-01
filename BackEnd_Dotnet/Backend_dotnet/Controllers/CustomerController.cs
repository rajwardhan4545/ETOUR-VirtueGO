using Backend_dotnet.DTOs;
using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/customer")]
    public class CustomerController : ControllerBase
    {
        private readonly ICustomerService _customerService;
        private readonly ILogger<CustomerController> _logger;

        public CustomerController(ICustomerService customerService, ILogger<CustomerController> logger)
        {
            _customerService = customerService;
            _logger = logger;
        }

        /// <summary>
        /// Get current user's profile
        /// </summary>
        [HttpGet("profile")]
        public async Task<IActionResult> GetProfile()
        {
            var email = HttpContext.Items["UserEmail"] as string;

            if (string.IsNullOrEmpty(email))
            {
                return Unauthorized(new { message = "Not authenticated" });
            }

            var profile = await _customerService.GetProfileAsync(email);

            if (profile == null)
            {
                return NotFound(new { message = "Customer not found" });
            }

            return Ok(profile);
        }

        /// <summary>
        /// Get current user's customer ID
        /// </summary>
        [HttpGet("id")]
        public async Task<IActionResult> GetProfileId()
        {
            var email = HttpContext.Items["UserEmail"] as string;

            if (string.IsNullOrEmpty(email))
            {
                return Unauthorized(new { message = "Not authenticated" });
            }

            var customerId = await _customerService.GetCustomerIdAsync(email);

            if (customerId == null)
            {
                return NotFound(new { message = "Customer not found" });
            }

            return Ok(new { customerId });
        }

        /// <summary>
        /// Update current user's profile
        /// </summary>
        [HttpPut("profile")]
        public async Task<IActionResult> UpdateProfile([FromBody] CustomerDTO dto)
        {
            var email = HttpContext.Items["UserEmail"] as string;

            if (string.IsNullOrEmpty(email))
            {
                return Unauthorized(new { message = "Not authenticated" });
            }

            var profile = await _customerService.UpdateProfileAsync(email, dto);

            if (profile == null)
            {
                return NotFound(new { message = "Customer not found" });
            }

            return Ok(profile);
        }

        /// <summary>
        /// Change current user's password
        /// </summary>
        [HttpPost("change-password")]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordDto dto)
        {
            var email = HttpContext.Items["UserEmail"] as string;

            if (string.IsNullOrEmpty(email))
            {
                return Unauthorized(new { message = "Not authenticated" });
            }

            try
            {
                var result = await _customerService.ChangePasswordAsync(email, dto);

                if (!result)
                {
                    return NotFound(new { message = "Customer not found" });
                }

                return Ok(new { message = "Password changed successfully" });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
    }
}
