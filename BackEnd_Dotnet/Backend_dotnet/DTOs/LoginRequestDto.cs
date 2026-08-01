using System.Text.Json.Serialization;

namespace Backend_dotnet.DTOs
{
    public class LoginRequestDto
    {
        [JsonPropertyName("email")]
        public string Email { get; set; }

        [JsonPropertyName("password")]
        public string Password { get; set; }
    }
}
