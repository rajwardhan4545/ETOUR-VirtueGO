using System.Text;

namespace Backend_dotnet.Middleware
{
    /// <summary>
    /// Detailed request/response body logging (use only in development)
    /// </summary>
    public class RequestResponseLoggingMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<RequestResponseLoggingMiddleware> _logger;

        public RequestResponseLoggingMiddleware(RequestDelegate next, ILogger<RequestResponseLoggingMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext context)
        {
            // Log request body
            context.Request.EnableBuffering();
            var requestBody = await ReadRequestBodyAsync(context.Request);

            if (!string.IsNullOrEmpty(requestBody))
            {
                _logger.LogDebug("Request Body: {RequestBody}", requestBody);
            }

            // Capture response
            var originalBodyStream = context.Response.Body;
            using var responseBody = new MemoryStream();
            context.Response.Body = responseBody;

            await _next(context);

            // Log response body
            var responseBodyText = await ReadResponseBodyAsync(context.Response);
            if (!string.IsNullOrEmpty(responseBodyText))
            {
                _logger.LogDebug("Response Body: {ResponseBody}", responseBodyText);
            }

            await responseBody.CopyToAsync(originalBodyStream);
        }

        private static async Task<string> ReadRequestBodyAsync(HttpRequest request)
        {
            request.Body.Position = 0;
            using var reader = new StreamReader(request.Body, Encoding.UTF8, leaveOpen: true);
            var body = await reader.ReadToEndAsync();
            request.Body.Position = 0;
            return body;
        }

        private static async Task<string> ReadResponseBodyAsync(HttpResponse response)
        {
            response.Body.Seek(0, SeekOrigin.Begin);
            var text = await new StreamReader(response.Body).ReadToEndAsync();
            response.Body.Seek(0, SeekOrigin.Begin);
            return text;
        }
    }
}