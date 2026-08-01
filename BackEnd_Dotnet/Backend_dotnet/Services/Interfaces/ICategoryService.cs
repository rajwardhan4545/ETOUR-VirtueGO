using Backend_dotnet.Models.Entities;

namespace Backend_dotnet.Services.Interfaces
{
    public interface ICategoryService
    {
        Task<IEnumerable<int>> GetCategoryIdsBySubCatAsync(string subcatCode);

        Task<IEnumerable<int>> GetHomeCategoryIdsAsync();

        
    }
}