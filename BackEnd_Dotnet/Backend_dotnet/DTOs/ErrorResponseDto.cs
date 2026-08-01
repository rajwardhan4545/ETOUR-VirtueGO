namespace Backend_dotnet.DTOs.Common
{
    /// <summary>
    /// Error response structure
    /// </summary>
    public class ErrorResponseDto
    {
        public int StatusCode { get; set; }
        public string Message { get; set; }
        public string Details { get; set; }
        public DateTime Timestamp { get; set; } = DateTime.UtcNow;
        public string Path { get; set; }
        public Dictionary<string, string[]> Errors { get; set; }

        public ErrorResponseDto()
        {
            Errors = new Dictionary<string, string[]>();
        }
    }
}