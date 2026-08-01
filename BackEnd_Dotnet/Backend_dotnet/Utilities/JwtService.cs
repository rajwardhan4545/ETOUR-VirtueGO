using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;

namespace Backend_dotnet.Utilities
{
    public class JwtService
    {
        // Same secret key as Java backend for token compatibility
        private readonly string _secretKey = "MySecretKeyForJwt123456789012345";
        private readonly int _tokenExpiryHours = 24;
        private readonly int _resetTokenExpiryMinutes = 15;

        private SymmetricSecurityKey GetSecurityKey()
        {
            return new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_secretKey));
        }

        /// <summary>
        /// Generates a JWT token for authenticated users
        /// </summary>
        public string GenerateToken(string email, string role)
        {
            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, email),
                new Claim("role", role),
                new Claim(JwtRegisteredClaimNames.Iat, DateTimeOffset.UtcNow.ToUnixTimeSeconds().ToString(), ClaimValueTypes.Integer64)
            };

            var credentials = new SigningCredentials(GetSecurityKey(), SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                claims: claims,
                expires: DateTime.UtcNow.AddHours(_tokenExpiryHours),
                signingCredentials: credentials
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        /// <summary>
        /// Generates a password reset token (15 min expiry)
        /// </summary>
        public string GenerateResetToken(string email)
        {
            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, email),
                new Claim("type", "RESET_PASSWORD"),
                new Claim(JwtRegisteredClaimNames.Iat, DateTimeOffset.UtcNow.ToUnixTimeSeconds().ToString(), ClaimValueTypes.Integer64)
            };

            var credentials = new SigningCredentials(GetSecurityKey(), SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                claims: claims,
                expires: DateTime.UtcNow.AddMinutes(_resetTokenExpiryMinutes),
                signingCredentials: credentials
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        /// <summary>
        /// Validates a token and returns the claims principal
        /// </summary>
        public ClaimsPrincipal? ValidateToken(string token)
        {
            try
            {
                var tokenHandler = new JwtSecurityTokenHandler();
                var validationParameters = new TokenValidationParameters
                {
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = GetSecurityKey(),
                    ValidateIssuer = false,
                    ValidateAudience = false,
                    ValidateLifetime = true,
                    ClockSkew = TimeSpan.Zero
                };

                var principal = tokenHandler.ValidateToken(token, validationParameters, out _);
                return principal;
            }
            catch
            {
                return null;
            }
        }

        /// <summary>
        /// Extracts email (subject) from token
        /// </summary>
        public string? ExtractEmail(string token)
        {
            var principal = ValidateToken(token);
            return principal?.FindFirst(JwtRegisteredClaimNames.Sub)?.Value 
                   ?? principal?.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        }

        /// <summary>
        /// Extracts role from token
        /// </summary>
        public string? ExtractRole(string token)
        {
            var principal = ValidateToken(token);
            return principal?.FindFirst("role")?.Value;
        }

        /// <summary>
        /// Validates that a token is a valid password reset token
        /// </summary>
        public bool IsResetTokenValid(string token)
        {
            var principal = ValidateToken(token);
            if (principal == null) return false;

            var typeClaim = principal.FindFirst("type")?.Value;
            return typeClaim == "RESET_PASSWORD";
        }

        /// <summary>
        /// Checks if token is valid (not expired, properly signed)
        /// </summary>
        public bool IsTokenValid(string token)
        {
            return ValidateToken(token) != null;
        }
    }
}
