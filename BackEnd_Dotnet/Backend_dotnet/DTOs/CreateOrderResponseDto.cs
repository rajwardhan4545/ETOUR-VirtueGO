namespace Backend_dotnet.DTOs
{
    public class CreateOrderResponseDto
    {
        public string OrderId { get; set; } = string.Empty;
        public long Amount { get; set; }
        public string Currency { get; set; } = "INR";
    }
}