using Backend_dotnet.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/search")]
    public class SearchController:ControllerBase
    {
        private readonly ISearchService _searchService;
        private readonly ITourService _tourService;

        public SearchController(
            ISearchService searchService,
            ITourService tourService)
        {
            _searchService = searchService;
            _tourService = tourService;
        }

        // GET: api/search/name/{query}
        [HttpGet("name/{query}")]
        public async Task<IActionResult> SearchByName(string query)
        {
            var ids = await _searchService.GetCategoryIdsByNameAsync(query);
            var tours = await _tourService.GetToursByCategoryIdsAsync(ids.ToList());
            return Ok(tours);
        }

        // GET: api/search/date?from=yyyy-mm-dd&to=yyyy-mm-dd
        [HttpGet("date")]
        public async Task<IActionResult> SearchByDateRange(
            [FromQuery] string from,
            [FromQuery] string to)
        {
            if (!DateOnly.TryParse(from, out DateOnly fromDate) ||
                !DateOnly.TryParse(to, out DateOnly toDate))
            {
                return BadRequest("Invalid date format. Use yyyy-MM-dd");
            }

            var ids =
                await _searchService.GetCategoryIdsByDateRangeAsync(
                    fromDate,
                    toDate);

            var tours =
                await _tourService.GetToursByCategoryIdsAsync(
                    ids.ToList());

            return Ok(tours);
        }

        // GET: api/search/cost/{maxCost}
        [HttpGet("cost/{maxCost}")]
        public async Task<IActionResult> SearchByMaxCost(decimal maxCost)
        {
            var ids =
                await _searchService.GetCategoryIdsByMaxCostAsync(maxCost);

            var tours =
                await _tourService.GetToursByCategoryIdsAsync(
                    ids.ToList());

            return Ok(tours);
        }
    }
}
