using System.Collections.Generic;
using System.Threading.Tasks;
using Backend_dotnet.DTOs;

namespace Backend_dotnet.Services.Interfaces
{
    public interface ITourService
    {
        Task<List<TourDto>> GetHomePageToursAsync();
        Task<List<TourDto>> GetToursBySubCategoryAsync(string subCategoryCode);
        Task<List<TourDto>> GetAllToursAsync();
        Task<List<TourDto>> GetToursByIdsAsync(List<int> tourIds);
        Task<List<TourDto>> GetToursByCategoryIdAsync(int categoryId);
        Task<TourDto> GetTourByIdAsync(int id);
        Task<List<TourDto>> GetToursByCategoryIdsAsync(List<int> categoryIds);
        Task<int> GetTourIdByCategoryAndDepartureAsync(int categoryId, int departureId);
    }
}
