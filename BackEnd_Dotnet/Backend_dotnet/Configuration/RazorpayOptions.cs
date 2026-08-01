namespace Backend_dotnet.Configuration
{
    public class RazorpayOptions
    {
        public string KeyId { get; set; } = string.Empty;
        public string KeySecret { get; set; } = string.Empty;
        public string WebhookSecret { get; set; } = string.Empty;
    }
}
