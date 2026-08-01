using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Repositories.Implementations
{
    public class CategoryRepository : GenericRepository<category_master>, ICategoryRepository
    {
        public CategoryRepository(AppDbContext context) : base(context)
        {
        }

        // Get categories by subcat_code (e.g., "EUP", "KSH", "SEA")

        // Get only category IDs by subcat_code
        public async Task<IEnumerable<int>> GetCategoryIdsBySubCatAsync(string subcatCode)
        {
            return await _dbSet
                .AsNoTracking()
                .Where(c => c.subcat_code == subcatCode)
                .Select(c => c.category_id)
                .ToListAsync();
        }

        
    }
}