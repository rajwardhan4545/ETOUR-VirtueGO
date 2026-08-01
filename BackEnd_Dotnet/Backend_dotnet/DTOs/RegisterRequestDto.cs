using System.Text.Json.Serialization;

namespace Backend_dotnet.DTOs
{
    public class RegisterRequestDto
    {
        [JsonPropertyName("email")]
        public string Email { get; set; }

        [JsonPropertyName("phone")]
        public string Phone { get; set; }

        [JsonPropertyName("address")]
        public string Address { get; set; }

        [JsonPropertyName("firstName")]
        public string FirstName { get; set; }

        [JsonPropertyName("lastName")]
        public string LastName { get; set; }

        [JsonPropertyName("password")]
        public string Password { get; set; }


        [JsonIgnore]
        [JsonPropertyName("customerRole")]
        public string CustomerRole { get; set; } = "CUSTOMER";

        [JsonIgnore]
        [JsonPropertyName("authProvider")]
        public string AuthProvider { get; set; } = "LOCAL";

        [JsonIgnore]
        [JsonPropertyName("profileCompleted")]
        public bool ProfileCompleted { get; set; } = true;
    }
}
