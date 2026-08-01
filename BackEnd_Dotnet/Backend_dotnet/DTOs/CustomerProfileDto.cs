namespace Backend_dotnet.DTOs
{
    public class CustomerProfileDto
    {
        public int CustomerId { get; set; }
        public string Email { get; set; } = string.Empty;
        public string FirstName { get; set; } = string.Empty;
        public string LastName { get; set; } = string.Empty;
        public string? Phone { get; set; }
        public string? Address { get; set; }
        public string CustomerRole { get; set; } = "CUSTOMER";
        public string AuthProvider { get; set; } = "LOCAL";
        public bool ProfileCompleted { get; set; }
    }
}
