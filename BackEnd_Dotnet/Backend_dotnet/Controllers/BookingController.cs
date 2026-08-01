using Backend_dotnet.DTOs;
using Backend_dotnet.DTOs.Common;
using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class BookingController : ControllerBase
    {
        private readonly IBookingService _bookingService;
        private readonly ILogger<BookingController> _logger;

        public BookingController(IBookingService bookingService, ILogger<BookingController> logger)
        {
            _bookingService = bookingService;
            _logger = logger;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            _logger.LogInformation("Getting all bookings");
            var result = await _bookingService.GetAllAsync();
            return Ok(result);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<BookingResponseDto>> GetById(int id)
        {
            _logger.LogInformation("Getting booking {BookingId}", id);
            var booking = await _bookingService.GetByIdAsync(id);
            return Ok(booking);
        }

        [HttpGet("customer/{customerId}")]
        public async Task<ActionResult<IEnumerable<BookingResponseDto>>> GetCustomerBookings(int customerId)
        {
            _logger.LogInformation("Getting bookings for customer {CustomerId}", customerId);
            var bookings = await _bookingService.GetCustomerBookingsAsync(customerId);
            return Ok(bookings);
        }

        [HttpPost]
        public async Task<ActionResult<BookingResponseDto>> Create([FromBody] BookingCreateDto dto)
        {
            _logger.LogInformation("Creating new booking for customer {CustomerId}", dto.CustomerId);
            var booking = await _bookingService.CreateAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = booking.BookingId }, booking);
        }

        [HttpGet("status/{bookingId}")]
        public async Task<ActionResult<int>> GetPaymentStatus(int bookingId)
        {
            _logger.LogInformation("Getting payment status for booking {BookingId}", bookingId);
            var status = await _bookingService.GetPaymentStatusAsync(bookingId);
            return Ok(status);
        }
    }
}