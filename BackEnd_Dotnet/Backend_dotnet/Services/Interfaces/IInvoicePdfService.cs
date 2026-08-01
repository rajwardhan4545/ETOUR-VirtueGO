namespace Backend_dotnet.Services.Interfaces
{
    public interface IInvoicePdfService
    {
        Task<byte[]> GenerateInvoiceAsync(int paymentId);
    }
}
