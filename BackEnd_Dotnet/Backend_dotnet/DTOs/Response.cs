namespace Backend_dotnet.DTOs.Common
{
    /// <summary>
    /// Generic API response wrapper
    /// </summary>
    public class ResponseDto<T>
    {
        public bool Success { get; set; }
        public string Message { get; set; }
        public T Data { get; set; }
        public DateTime Timestamp { get; set; } = DateTime.UtcNow;

        public static ResponseDto<T> SuccessResponse(T data, string message = "Operation successful")
        {
            return new ResponseDto<T>
            {
                Success = true,
                Message = message,
                Data = data
            };
        }

        public static ResponseDto<T> FailureResponse(string message, T data = default)
        {
            return new ResponseDto<T>
            {
                Success = false,
                Message = message,
                Data = data
            };
        }
    }
}