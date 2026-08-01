using System.ComponentModel.DataAnnotations;

namespace Backend_dotnet.DTOs
{
    public class CustomerDTO
    {
        public int? Id { get; set; }

        [Required(ErrorMessage = "Email is required")]
        [EmailAddress(ErrorMessage = "Invalid email format")]
        public string Email { get; set; } = string.Empty;

        [RegularExpression(
            "^[6-9]\\d{9}$",
            ErrorMessage = "Invalid phone number"
        )]
        public string? Phone { get; set; }

        public string? Address { get; set; }

        [Required(ErrorMessage = "First name is required")]
        [StringLength(50, MinimumLength = 3, ErrorMessage = "First name must be at least 3 characters")]
        [RegularExpression("^[A-Za-z]+$", ErrorMessage = "First name must contain only letters")]
        public string FirstName { get; set; } = string.Empty;

        [Required(ErrorMessage = "Last name is required")]
        [StringLength(50, MinimumLength = 1, ErrorMessage = "Last name must be at least 3 characters")]
        [RegularExpression("^[A-Za-z]+$", ErrorMessage = "Last name must contain only letters")]
        public string LastName { get; set; } = string.Empty;

        [MinLength(8, ErrorMessage = "Password must be at least 8 characters")]
        [RegularExpression(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).*$",
            ErrorMessage = "Password must contain uppercase, lowercase, number and special character"
        )]
        public string? Password { get; set; }

        public string? CustomerRole { get; set; }

        public string? AuthProvider { get; set; }

        public bool? ProfileCompleted { get; set; }
    }
}
