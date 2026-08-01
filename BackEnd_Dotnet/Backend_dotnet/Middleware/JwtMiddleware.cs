using Backend_dotnet.Utilities;

namespace Backend_dotnet.Middleware
{
    public class JwtMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<JwtMiddleware> _logger;

        // Routes that don't require authentication
        private static readonly string[] PublicRoutes = new[]
        {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/google-login",
            "/api/tours",
            "/api/search",
            "/swagger",
            "/health"
        };

        public JwtMiddleware(RequestDelegate next, ILogger<JwtMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext context, JwtService jwtService)
        {
            var path = context.Request.Path.Value?.ToLower() ?? "";

            // Skip authentication for public routes
            if (IsPublicRoute(path))
            {
                await _next(context);
                return;
            }

            var authHeader = context.Request.Headers["Authorization"].FirstOrDefault();

            if (authHeader != null && authHeader.StartsWith("Bearer "))
            {
                var token = authHeader.Substring(7);

                try
                {
                    var email = jwtService.ExtractEmail(token);
                    var role = jwtService.ExtractRole(token);

                    if (email != null && jwtService.IsTokenValid(token))
                    {
                        // Store user info in HttpContext for controllers to access
                        context.Items["UserEmail"] = email;
                        context.Items["UserRole"] = role;
                        
                        _logger.LogDebug("JWT validated for user: {Email}", email);
                    }
                    else
                    {
                        _logger.LogWarning("Invalid JWT token");
                    }
                }
                catch (Exception ex)
                {
                    _logger.LogWarning(ex, "JWT validation failed");
                }
            }

            await _next(context);
        }

        private bool IsPublicRoute(string path)
        {
            foreach (var route in PublicRoutes)
            {
                if (path.StartsWith(route, StringComparison.OrdinalIgnoreCase))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
