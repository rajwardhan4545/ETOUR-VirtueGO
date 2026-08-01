using Backend_dotnet.Models.Entities;

namespace Backend_dotnet.Repositories.Interfaces
{
    public interface ICategoryRepository : IGenericRepository<category_master>
    {
        // Custom methods specific to Category
       
        Task<IEnumerable<int>> GetCategoryIdsBySubCatAsync(string subcatCode);
        
    }
}