namespace Backend_dotnet.Services.Interfaces
{
    public interface IEmailService
    {
        /// <summary>
        /// Send a simple email
        /// </summary>
        Task SendSimpleEmailAsync(string toEmail, string subject, string body);

        /// <summary>
        /// Send booking confirmation email
        /// </summary>
        Task SendBookingConfirmationAsync(int paymentId);

        /// <summary>
        /// Send invoice email with PDF attachment
        /// </summary>
        Task SendInvoiceWithAttachmentAsync(int paymentId, byte[] pdfBytes);
    }
}
