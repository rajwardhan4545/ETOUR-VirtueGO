using Backend_dotnet.Configuration;
using MailKit.Net.Smtp;
using Microsoft.Extensions.Options;
using MimeKit;

namespace Backend_dotnet.Utilities.Helpers
{
    /// <summary>
    /// Email sending helper using MailKit
    /// </summary>
    public class EmailHelper
    {
        private readonly EmailSettings _emailSettings;
        private readonly ILogger<EmailHelper> _logger;

        public EmailHelper(IOptions<EmailSettings> emailSettings, ILogger<EmailHelper> logger)
        {
            _emailSettings = emailSettings.Value;
            _logger = logger;
        }

        public async Task<bool> SendEmailAsync(string toEmail, string subject, string body, bool isHtml = true)
        {
            try
            {
                var message = new MimeMessage();
                message.From.Add(new MailboxAddress(_emailSettings.SenderName, _emailSettings.SenderEmail));
                message.To.Add(new MailboxAddress("", toEmail));
                message.Subject = subject;

                var bodyBuilder = new BodyBuilder();
                if (isHtml)
                {
                    bodyBuilder.HtmlBody = body;
                }
                else
                {
                    bodyBuilder.TextBody = body;
                }

                message.Body = bodyBuilder.ToMessageBody();

                using var client = new SmtpClient();
                await client.ConnectAsync(_emailSettings.SmtpServer, _emailSettings.SmtpPort, MailKit.Security.SecureSocketOptions.StartTls);
                await client.AuthenticateAsync(_emailSettings.SenderEmail, _emailSettings.SenderPassword);
                await client.SendAsync(message);
                await client.DisconnectAsync(true);

                _logger.LogInformation("Email sent successfully to {Email}", toEmail);
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to send email to {Email}", toEmail);
                return false;
            }
        }

        public async Task<bool> SendBookingConfirmationAsync(string toEmail, string customerName, int bookingId)
        {
            var subject = $"Booking Confirmation - #{bookingId}";
            var body = $@"
                <html>
                <body>
                    <h2>Booking Confirmation</h2>
                    <p>Dear {customerName},</p>
                    <p>Your booking has been confirmed.</p>
                    <p>Booking ID: <strong>{bookingId}</strong></p>
                    <p>Thank you for choosing E-Tour!</p>
                </body>
                </html>
            ";

            return await SendEmailAsync(toEmail, subject, body);
        }

        public async Task<bool> SendPasswordResetAsync(string toEmail, string resetToken)
        {
            var subject = "Password Reset Request";
            var body = $@"
                <html>
                <body>
                    <h2>Password Reset</h2>
                    <p>You requested a password reset.</p>
                    <p>Reset Token: <strong>{resetToken}</strong></p>
                    <p>This token expires in 1 hour.</p>
                </body>
                </html>
            ";

            return await SendEmailAsync(toEmail, subject, body);
        }
    }
}