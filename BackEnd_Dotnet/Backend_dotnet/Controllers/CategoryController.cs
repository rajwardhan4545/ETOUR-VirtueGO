using Backend_dotnet.Models.Entities;
using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CategoryController : ControllerBase
    {
        private readonly ICategoryService _categoryService;
        private readonly ILogger<CategoryController> _logger;

        public CategoryController(ICategoryService categoryService, ILogger<CategoryController> logger)
        {
            _categoryService = categoryService;
            _logger = logger;
        }

        // GET: api/category/ids/subcat/EUP
        [HttpGet("ids/subcat/{subcatCode}")]
        public async Task<IActionResult> GetCategoryIdsBySubCat(string subcatCode)
        {
            try
            {
                var categoryIds = await _categoryService.GetCategoryIdsBySubCatAsync(subcatCode);

                if (!categoryIds.Any())
                {
                    return NotFound(new { message = $"No categories found for subcat_code: {subcatCode}" });
                }

                return Ok(categoryIds);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Error getting category IDs for subcat: {subcatCode}");
                return StatusCode(500, new { message = "An error occurred" });
            }
        }

        

      

        

        
        

        
    }
}