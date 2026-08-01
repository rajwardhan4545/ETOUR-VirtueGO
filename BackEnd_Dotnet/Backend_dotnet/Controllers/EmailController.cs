using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/email")]
    public class EmailController : ControllerBase
    {
        private readonly IEmailService _emailService;
        private readonly IInvoicePdfService _invoicePdfService;
        private readonly ILogger<EmailController> _logger;

        public EmailController(
            IEmailService emailService,
            IInvoicePdfService invoicePdfService,
            ILogger<EmailController> logger)
        {
            _emailService = emailService;
            _invoicePdfService = invoicePdfService;
            _logger = logger;
        }

        [HttpPost("booking-confirmation")]
        public async Task<IActionResult> SendBookingMail([FromQuery] int paymentId)
        {
            try
            {
                _logger.LogInformation(
                    "Sending booking confirmation for payment {PaymentId}",
                    paymentId);

                await _emailService.SendBookingConfirmationAsync(paymentId);

                return Ok($"Booking confirmation email sent for paymentId = {paymentId}");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex,
                    "Failed to send booking confirmation for payment {PaymentId}",
                    paymentId);

                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpPost("invoice")]
        public async Task<IActionResult> SendInvoiceMail([FromQuery] int paymentId)
        {
            try
            {
                _logger.LogInformation(
                    "Sending invoice email for payment {PaymentId}",
                    paymentId);

                // ✅ Generate PDF using correct service
                var pdf =
                    await _invoicePdfService.GenerateInvoiceAsync(paymentId);

                // ✅ Send email
                await _emailService.SendInvoiceWithAttachmentAsync(paymentId, pdf);

                return Ok($"Invoice email sent for paymentId = {paymentId}");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex,
                    "Failed to send invoice email for payment {PaymentId}",
                    paymentId);

                return BadRequest(new { message = ex.Message });
            }
        }
    }
}
