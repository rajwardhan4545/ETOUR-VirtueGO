using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;

namespace Backend_dotnet.Services.Implementations
{
    public class TourService : ITourService
    {
        private readonly ITourRepository _tourRepository;
        private readonly ICategoryService _categoryService;

        public TourService(
            ITourRepository tourRepository,
            ICategoryService categoryService)
        {
            _tourRepository = tourRepository;
            _categoryService = categoryService;
        }

        //  /api/tours (Home page)
        public async Task<List<TourDto>> GetHomePageToursAsync()
        {
            var categoryIds = await _categoryService.GetHomeCategoryIdsAsync();

            if (categoryIds == null || !categoryIds.Any())
                return new List<TourDto>();

            return await FetchToursAsync(categoryIds.ToList());
        }

        //  /api/tours/{subcat}
        public async Task<List<TourDto>> GetToursBySubCategoryAsync(string subCategoryCode)
        {
            var categoryIds =
                await _categoryService.GetCategoryIdsBySubCatAsync(subCategoryCode);

            if (categoryIds == null || !categoryIds.Any())
                return new List<TourDto>();

            return await FetchToursAsync(categoryIds.ToList());
        }

        //  COMMON METHOD (same logic as Java fetchTours)
        private async Task<List<TourDto>> FetchToursAsync(List<int> categoryIds)
        {
            var tours = await _tourRepository.GetByCategoryIdsAsync(categoryIds);

            return tours
                .Select(ConvertToDto)
                .GroupBy(t => t.CategoryId)      // deduplicate by CategoryId
                .Select(g => g.First())
                .ToList();
        }

        public async Task<List<TourDto>> GetAllToursAsync()
        {
            var tours = await _tourRepository.GetAllAsync();
            return tours.Select(ConvertToDto).ToList();
        }

        public async Task<List<TourDto>> GetToursByIdsAsync(List<int> tourIds)
        {
            var tours = await _tourRepository.GetAllAsync();
            return tours
                .Where(t => tourIds.Contains(t.tour_id))
                .Select(ConvertToDto)
                .ToList();
        }

        public async Task<List<TourDto>> GetToursByCategoryIdAsync(int categoryId)
        {
            var tours = await _tourRepository.GetByCategoryIdAsync(categoryId);
            return tours.Select(ConvertToDto).ToList();
        }

        public async Task<TourDto> GetTourByIdAsync(int id)
        {
            var tour = await _tourRepository.GetByIdAsync(id);
            if (tour == null)
                throw new Exception($"Tour not found with id: {id}");

            return ConvertToDto(tour);
        }

        public async Task<int> GetTourIdByCategoryAndDepartureAsync(
            int categoryId,
            int departureId)
        {
            var tour = await _tourRepository
                .GetByCategoryIdAndDepartureIdAsync(categoryId, departureId);

            if (tour == null)
                throw new Exception(
                    $"Tour not found for categoryId={categoryId} and departureId={departureId}"
                );

            return tour.tour_id;
        }

        // 🔹 convertToDTO (Java → C#)
        private TourDto ConvertToDto(tour_master tour)
        {
            return new TourDto
            {
                
                Id = tour.tour_id,

                CategoryId = tour.category?.category_id ?? 0,
                CategoryName = tour.category?.category_name ?? "",
                CategoryCode = tour.category?.cat_code ?? "",
                SubCategoryCode = tour.category?.subcat_code ?? "",
                ImagePath = tour.category?.image_path,
                JumpFlag = tour.category?.jump_flag ?? false,

                DepartureId = tour.departure?.departure_id ?? 0,

                Itineraries = tour.category?.itinerary_master?
                    .Select(i => new ItineraryDto
                    {
                        ItineraryId = i.itinerary_id,
                        DayNo = i.day_no,
                        ItineraryDetail = i.itinerary_detail,
                        DayWiseImage = i.day_wise_image
                    }).ToList() ?? new(),

                Costs = tour.category?.cost_master?
                    .Select(c => new CostDto
                    {
                        CostId = c.cost_id,
                        SinglePersonCost = c.single_person_cost,
                        ExtraPersonCost = c.extra_person_cost,
                        ChildWithBedCost = c.child_with_bed_cost,
                        ChildWithoutBedCost = c.child_without_bed_cost,
                        ValidFrom = c.valid_from,
                        ValidTo = c.valid_to
                    }).ToList() ?? new(),

                Departures = tour.category?.departure_master?
                    .Select(d => new DepartureDto
                    {
                        DepartureId = d.departure_id,
                        DepartDate = d.depart_date,
                        EndDate = d.end_date,
                        NoOfDays = d.no_of_days
                    }).ToList() ?? new(),

                Guides = tour.tour_guide?
                    .Select(g => new TourGuideDto
                    {
                        Id = g.tour_guide_id,
                        Name = g.name,
                        Email = g.email,
                        Phone = g.phone
                    }).ToList() ?? new()
            };
        }

        public async Task<List<TourDto>> GetToursByCategoryIdsAsync(List<int> categoryIds)
        {
            if (categoryIds == null || !categoryIds.Any())
                return new List<TourDto>();

            return await FetchToursAsync(categoryIds);
        }

    }
}
