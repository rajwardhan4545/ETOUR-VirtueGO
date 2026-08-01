using Microsoft.AspNetCore.Mvc;
using Backend_dotnet.Services.Interfaces;
using Backend_dotnet.DTOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/tours")]
    public class TourController : ControllerBase
    {
        private readonly ITourService _tourService;

        public TourController(ITourService tourService)
        {
            _tourService = tourService;
        }

        //  HOME PAGE
        // GET: /api/tours
        [HttpGet]
        public async Task<ActionResult<List<TourDto>>> GetHomePageTours()
        {
            var tours = await _tourService.GetHomePageToursAsync();
            return Ok(tours);
        }

        //  GET TOUR ID BY CATEGORY + DEPARTURE
        // GET: /api/tours/tour-id?categoryId=1&departureId=2
        [HttpGet("tour-id")]
        public async Task<ActionResult<int>> GetTourId(
            [FromQuery] int categoryId,
            [FromQuery] int departureId)
        {
            var tourId =
                await _tourService.GetTourIdByCategoryAndDepartureAsync(
                    categoryId,
                    departureId
                );

            return Ok(tourId);
        }

        //  SUBCATEGORY PAGE
        // GET: /api/tours/{subcat}
        [HttpGet("{subcat}")]
        public async Task<ActionResult<List<TourDto>>> GetToursBySubCategory(
            string subcat)
        {
            var tours = await _tourService.GetToursBySubCategoryAsync(subcat);
            return Ok(tours);
        }

        //  DETAILS PAGE
        // GET: /api/tours/details/{catId}
        [HttpGet("details/{catId}")]
        public async Task<ActionResult<List<TourDto>>> GetToursForDetailsPage(
            int catId)
        {
            var tours = await _tourService.GetToursByCategoryIdAsync(catId);
            return Ok(tours);
        }
        //  GET TOURS BY CATEGORY IDs
        // POST: /api/tours/by-category-ids
        [HttpPost("by-category-ids")]
        public async Task<ActionResult<List<TourDto>>> GetToursByCategoryIds(
            [FromBody] List<int> categoryIds)
        {
            if (categoryIds == null || !categoryIds.Any())
                return BadRequest("Category IDs cannot be empty.");

            var tours = await _tourService.GetToursByCategoryIdsAsync(categoryIds);
            return Ok(tours);
        }
    }
}
