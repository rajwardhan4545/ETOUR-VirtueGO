namespace Backend_dotnet.Utilities.Helpers
{
    /// <summary>
    /// Image upload and processing helper
    /// </summary>
    public class ImageHelper
    {
        private readonly IWebHostEnvironment _environment;
        private readonly ILogger<ImageHelper> _logger;
        private readonly string _uploadPath;
        private readonly long _maxFileSize;
        private readonly string[] _allowedExtensions;

        public ImageHelper(IWebHostEnvironment environment, ILogger<ImageHelper> logger, IConfiguration configuration)
        {
            _environment = environment;
            _logger = logger;
            _uploadPath = configuration["FileUpload:UploadPath"] ?? "wwwroot/uploads";
            _maxFileSize = configuration.GetValue<long>("FileUpload:MaxFileSize", 5242880); // 5MB default
            _allowedExtensions = configuration.GetSection("FileUpload:AllowedExtensions").Get<string[]>()
                ?? new[] { ".jpg", ".jpeg", ".png", ".gif" };
        }

        public async Task<string> UploadImageAsync(IFormFile file, string folder = "images")
        {
            if (file == null || file.Length == 0)
            {
                throw new ArgumentException("File is required");
            }

            // Validate file size
            if (file.Length > _maxFileSize)
            {
                throw new InvalidOperationException($"File size exceeds maximum allowed size of {_maxFileSize / 1024 / 1024}MB");
            }

            // Validate file extension
            var extension = Path.GetExtension(file.FileName).ToLowerInvariant();
            if (!_allowedExtensions.Contains(extension))
            {
                throw new InvalidOperationException($"File type {extension} is not allowed. Allowed types: {string.Join(", ", _allowedExtensions)}");
            }

            try
            {
                // Create folder if not exists
                var uploadFolder = Path.Combine(_environment.ContentRootPath, _uploadPath, folder);
                if (!Directory.Exists(uploadFolder))
                {
                    Directory.CreateDirectory(uploadFolder);
                }

                // Generate unique filename
                var fileName = $"{Guid.NewGuid()}{extension}";
                var filePath = Path.Combine(uploadFolder, fileName);

                // Save file
                using (var stream = new FileStream(filePath, FileMode.Create))
                {
                    await file.CopyToAsync(stream);
                }

                _logger.LogInformation("File uploaded successfully: {FileName}", fileName);

                // Return relative path
                return $"/uploads/{folder}/{fileName}";
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error uploading file");
                throw;
            }
        }

        public bool DeleteImage(string filePath)
        {
            try
            {
                if (string.IsNullOrEmpty(filePath))
                    return false;

                var fullPath = Path.Combine(_environment.ContentRootPath, "wwwroot", filePath.TrimStart('/'));

                if (File.Exists(fullPath))
                {
                    File.Delete(fullPath);
                    _logger.LogInformation("File deleted successfully: {FilePath}", filePath);
                    return true;
                }

                return false;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting file: {FilePath}", filePath);
                return false;
            }
        }

        public bool ValidateImage(IFormFile file)
        {
            if (file == null || file.Length == 0)
                return false;

            if (file.Length > _maxFileSize)
                return false;

            var extension = Path.GetExtension(file.FileName).ToLowerInvariant();
            return _allowedExtensions.Contains(extension);
        }
    }
}