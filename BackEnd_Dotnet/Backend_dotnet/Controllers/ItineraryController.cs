using Backend_dotnet.DTOs;
using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/admin/itineraries")]
    public class ItineraryController : ControllerBase
    {
        private readonly IItineraryService _itineraryService;

        public ItineraryController(IItineraryService itineraryService)
        {
            _itineraryService = itineraryService;
        }

        [HttpPost("upload-csv")]
        public async Task<IActionResult> UploadCsv(IFormFile file)
        {
            if (file == null || file.Length == 0)
                return BadRequest("File is empty");

            await _itineraryService.ImportCsvAsync(file);
            return Ok("CSV uploaded successfully");
        }
    }
}
