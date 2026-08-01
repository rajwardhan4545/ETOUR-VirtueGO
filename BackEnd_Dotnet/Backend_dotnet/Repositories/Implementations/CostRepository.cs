using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Backend_dotnet.Repositories.Implementations
{
    public class CostRepository : GenericRepository<cost_master>, ICostRepository
    {
        public CostRepository(AppDbContext context) : base(context)
        {
        }

        public async Task<IEnumerable<cost_master>> GetByCategoryIdAsync(int categoryId)
        {
            return await _context.cost_master
                .Where(c => c.category_id == categoryId)
                .ToListAsync();
        }
    }
}
