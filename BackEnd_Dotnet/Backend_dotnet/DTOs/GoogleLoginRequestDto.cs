using System.ComponentModel.DataAnnotations;

namespace Backend_dotnet.DTOs
{
    public class GoogleLoginRequestDto
    {
        [Required]
        public string IdToken { get; set; } = string.Empty;
    }
}
