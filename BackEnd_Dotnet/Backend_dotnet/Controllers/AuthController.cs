using Backend_dotnet.DTOs;
using Backend_dotnet.Services.Implementations;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/auth")]
    public class AuthController : ControllerBase
    {
        private readonly AuthService _authService;
        private readonly ILogger<AuthController> _logger;
        private readonly IConfiguration _configuration;

        public AuthController(AuthService authService, ILogger<AuthController> logger, IConfiguration configuration)
        {
            _authService = authService;
            _logger = logger;
            _configuration = configuration;
        }

        /// <summary>
        /// Register a new user
        /// </summary>
        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterRequestDto dto)
        {
            try
            {
                var result = await _authService.RegisterAsync(dto);
                return StatusCode(201, result);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        /// <summary>
        /// Login with email and password
        /// </summary>
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequestDto dto)
        {
            try
            {
                var token = await _authService.LoginAsync(dto);

                if (token == null)
                    return Unauthorized(new { message = "Invalid email or password" });

                return Ok(new { token });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        /// <summary>
        /// Request password reset email
        /// </summary>
        [HttpPost("forgot-password")]
        public async Task<IActionResult> ForgotPassword([FromBody] ForgotPasswordDto dto)
        {
            try
            {
                await _authService.SendResetTokenAsync(dto.Email);
                return Ok(new { message = "Password reset link has been sent to your email" });
            }
            catch (KeyNotFoundException)
            {
                // Don't reveal if email exists or not for security
                return Ok(new { message = "If the email exists, a password reset link will be sent" });
            }
        }

        /// <summary>
        /// Reset password using token
        /// </summary>
        [HttpPost("reset-password")]
        public async Task<IActionResult> ResetPassword([FromBody] ResetPasswordDto dto)
        {
            try
            {
                await _authService.ResetPasswordAsync(dto);
                return Ok(new { message = "Password has been reset successfully" });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { message = ex.Message });
            }
        }

        /// <summary>
        /// Redirect to Google OAuth (placeholder - full OAuth requires additional setup)
        /// </summary>
        /// <summary>
        /// Serve a simple HTML page to test Google Login in the browser
        /// </summary>
        [HttpGet("google-login")]
        public IActionResult GoogleLoginTestPage()
        {
            var clientId = _configuration["Google:ClientId"];
            var html = $@"
<!DOCTYPE html>
<html>
<head>
    <title>Google Login Test</title>
    <script src=""https://accounts.google.com/gsi/client"" async defer></script>
</head>
<body style=""font-family: sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh;"">
    <h1>Test Google Login</h1>
    <div id=""buttonDiv""></div>
    <div id=""result"" style=""margin-top: 20px; white-space: pre-wrap; background: #f0f0f0; padding: 10px; border-radius: 5px; max-width: 800px;""></div>

    <script>
        function handleCredentialResponse(response) {{
            document.getElementById('result').innerText = 'Verifying token...';
            
            fetch('/api/auth/google-login', {{
                method: 'POST',
                headers: {{ 'Content-Type': 'application/json' }},
                body: JSON.stringify({{ idToken: response.credential }})
            }})
            .then(res => res.json())
            .then(data => {{
                document.getElementById('result').innerText = 'Success! Backend Response:\n' + JSON.stringify(data, null, 2);
                console.log(data);
            }})
            .catch(err => {{
                 document.getElementById('result').innerText = 'Error: ' + err;
            }});
        }}

        window.onload = function () {{
            google.accounts.id.initialize({{
                client_id: ""{clientId}"",
                callback: handleCredentialResponse
            }});
            google.accounts.id.renderButton(
                document.getElementById(""buttonDiv""),
                {{ theme: ""outline"", size: ""large"" }}
            );
            google.accounts.id.prompt();
        }}
    </script>
</body>
</html>";

            return Content(html, "text/html");
        }

        /// <summary>
        /// Google OAuth login/register
        /// </summary>
        [HttpPost("google-login")]
        public async Task<IActionResult> GoogleLogin([FromBody] GoogleLoginRequestDto dto)
        {
            try
            {
                var token = await _authService.HandleGoogleLoginAsync(dto.IdToken);
                return Ok(new { token });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Google login failed");
                return StatusCode(500, new { message = "An error occurred during Google login" });
            }
        }
    }
}
