using Backend_dotnet.Configuration;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;
using MailKit.Net.Smtp;
using Microsoft.Extensions.Options;
using MimeKit;

namespace Backend_dotnet.Services.Implementations
{
    public class EmailService : IEmailService
    {
        private readonly EmailSettings _emailSettings;
        private readonly IPaymentRepository _paymentRepository;
        private readonly ILogger<EmailService> _logger;

        public EmailService(
            IOptions<EmailSettings> emailSettings,
            IPaymentRepository paymentRepository,
            ILogger<EmailService> logger)
        {
            _emailSettings = emailSettings.Value;
            _paymentRepository = paymentRepository;
            _logger = logger;
        }

        public async Task SendSimpleEmailAsync(string toEmail, string subject, string body)
        {
            var message = new MimeMessage();
            message.From.Add(new MailboxAddress(_emailSettings.SenderName, _emailSettings.SenderEmail));
            message.To.Add(new MailboxAddress("", toEmail));
            message.Subject = subject;

            var bodyBuilder = new BodyBuilder { TextBody = body };
            message.Body = bodyBuilder.ToMessageBody();

            await SendMessageAsync(message);
            _logger.LogInformation("Simple email sent to {Email}", toEmail);
        }

        public async Task SendBookingConfirmationAsync(int paymentId)
        {
            var payment = await _paymentRepository.GetPaymentWithBookingAndCustomerAsync(paymentId);

            if (payment == null)
                throw new Exception("Payment not found");

            var booking = payment.booking;
            if (booking?.customer == null)
                throw new Exception("Booking or customer not found");

            var email = booking.customer.email;
            var name = booking.customer.first_name;

            if (string.IsNullOrWhiteSpace(email))
                throw new Exception("Customer email not found");

            var subject = $"Booking Confirmed – Booking #{booking.booking_id}";
            var body = $@"Hello {name},

                    Your booking has been successfully confirmed.

                    Booking ID: {booking.booking_id}

                    Thank you for choosing VirtuGo!

                    Regards,
                    VirtuGo Team";

            await SendSimpleEmailAsync(email, subject, body);
            _logger.LogInformation("Booking confirmation sent for payment {PaymentId}", paymentId);
        }

        public async Task SendInvoiceWithAttachmentAsync(int paymentId, byte[] pdfBytes)
        {
            var payment = await _paymentRepository.GetPaymentWithBookingAndCustomerAsync(paymentId);

            if (payment == null)
                throw new Exception("Payment not found");

            var booking = payment.booking;
            if (booking?.customer == null)
                throw new Exception("Booking or customer not found");

            var email = booking.customer.email;
            var name = booking.customer.first_name;

            if (string.IsNullOrWhiteSpace(email))
                throw new Exception("Customer email not found");

            var message = new MimeMessage();
            message.From.Add(new MailboxAddress(_emailSettings.SenderName, _emailSettings.SenderEmail));
            message.To.Add(new MailboxAddress(name, email));
            message.Subject = $"Invoice – Booking #{booking.booking_id}";

            var bodyBuilder = new BodyBuilder
            {
                TextBody = $@"Hello {name},

                    Please find your invoice attached.

                    Booking ID: {booking.booking_id}

                    Thank you for choosing VirtuGo!

                    Regards,
                    VirtuGo Team"
            };

            // Attach PDF
            bodyBuilder.Attachments.Add($"Invoice_{booking.booking_id}.pdf", pdfBytes, new ContentType("application", "pdf"));
            message.Body = bodyBuilder.ToMessageBody();

            await SendMessageAsync(message);
            _logger.LogInformation("Invoice email sent for payment {PaymentId} to {Email}", paymentId, email);
        }

        private async Task SendMessageAsync(MimeMessage message)
        {
            try
            {
                using var client = new SmtpClient();
                await client.ConnectAsync(_emailSettings.SmtpServer, _emailSettings.SmtpPort, MailKit.Security.SecureSocketOptions.StartTls);
                await client.AuthenticateAsync(_emailSettings.SenderEmail, _emailSettings.SenderPassword);
                await client.SendAsync(message);
                await client.DisconnectAsync(true);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to send email");
                throw new Exception("Failed to send email", ex);
            }
        }
    }
}
