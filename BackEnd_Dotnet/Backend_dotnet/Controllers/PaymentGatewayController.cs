using Backend_dotnet.DTOs;
using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("payment-gateway")]
    public class PaymentGatewayController : ControllerBase
    {
        private readonly IPaymentGatewayService _service;

        public PaymentGatewayController(IPaymentGatewayService service)
        {
            _service = service;
        }

        [HttpPost("create-order")]
        public async Task<IActionResult> CreateOrder([FromBody] CreateOrderRequestDto dto)
        {
            var result = await _service.CreateOrder(dto);
            return Ok(result);
        }

        [HttpPost("confirm-payment")]
        public async Task<IActionResult> ConfirmPayment(
            [FromQuery] string orderId,
            [FromQuery] string paymentId,
            [FromQuery] long amount)
        {
            return Ok(await _service.ConfirmPayment(orderId, paymentId, amount));
        }

        [HttpPost("webhook")]
        public async Task<IActionResult> Webhook(
            [FromBody] string payload,
            [FromHeader(Name = "X-Razorpay-Signature")] string signature)
        {
            await _service.HandleWebhook(payload, signature);
            return Ok();
        }
    }
}
