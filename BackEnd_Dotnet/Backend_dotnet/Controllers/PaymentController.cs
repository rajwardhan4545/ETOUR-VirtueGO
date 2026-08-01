using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/payment")]
    public class PaymentController : ControllerBase
    {
        private readonly IPaymentService _paymentService;
        private readonly ILogger<PaymentController> _logger;

        public PaymentController(IPaymentService paymentService, ILogger<PaymentController> logger)
        {
            _paymentService = paymentService;
            _logger = logger;
        }

        /// <summary>
        /// Make a payment for a booking
        /// </summary>
        [HttpPost("pay")]
        public async Task<IActionResult> Pay(
            [FromQuery] int bookingId,
            [FromQuery] string paymentMode,
            [FromQuery] string transactionRef,
            [FromQuery] string paymentStatus,
            [FromQuery] decimal paymentAmount)
        {
            try
            {
                _logger.LogInformation("Processing payment for booking {BookingId}", bookingId);
                var result = await _paymentService.MakePayment(bookingId, paymentMode, transactionRef, paymentStatus, paymentAmount);
                return Ok(result);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Payment failed for booking {BookingId}", bookingId);
                return BadRequest(new { message = ex.Message });
            }
        }

        /// <summary>
        /// Get payment by ID
        /// </summary>
        [HttpGet("{id}")]
        public IActionResult GetPayment(int id)
        {
            try
            {
                _logger.LogInformation("Getting payment {PaymentId}", id);
                var payment = _paymentService.GetPaymentById(id);
                return Ok(payment);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to get payment {PaymentId}", id);
                return NotFound(new { message = ex.Message });
            }
        }

        /// <summary>
        /// Get successful payment receipt for a booking
        /// </summary>
        [HttpGet("receipt/{bookingId}")]
        public IActionResult GetReceipt(int bookingId)
        {
            try
            {
                _logger.LogInformation("Getting receipt for booking {BookingId}", bookingId);
                var payment = _paymentService.GetSuccessfulPayment(bookingId);
                return Ok(payment);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to get receipt for booking {BookingId}", bookingId);
                return NotFound(new { message = ex.Message });
            }
        }
    }
}