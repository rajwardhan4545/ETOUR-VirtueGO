using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/invoices")]
    public class InvoiceController : ControllerBase
    {
        private readonly IInvoicePdfService _invoiceService;
        private readonly IPaymentService _paymentService;

        public InvoiceController(
            IInvoicePdfService invoiceService,
            IPaymentService paymentService)
        {
            _invoiceService = invoiceService;
            _paymentService = paymentService;
        }

        [HttpGet("{bookingId}/download")]
        public async Task<IActionResult> DownloadInvoice(int bookingId)
        {
            // 🔵 Payment ID from booking
            int paymentId =
                await _paymentService.GetPaymentIdByBookingAsync(bookingId);

            // 🔵 Generate PDF
            byte[] pdf =
                await _invoiceService.GenerateInvoiceAsync(paymentId);

            return File(
                pdf,
                "application/pdf",
                $"invoice_{paymentId}.pdf");
        }
    }

}
