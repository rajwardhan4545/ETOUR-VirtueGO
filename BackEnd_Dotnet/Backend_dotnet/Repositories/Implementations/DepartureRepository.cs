using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Backend_dotnet.Repositories.Implementations
{
    public class DepartureRepository : GenericRepository<departure_master>, IDepartureRepository
    {
        public DepartureRepository(AppDbContext context) : base(context)
        {
        }

        public async Task<IEnumerable<departure_master>> GetByCategoryIdAsync(int categoryId)
        {
            return await _context.departure_master
                .Where(d => d.category_id == categoryId)
                .OrderBy(d => d.depart_date)
                .ToListAsync();
        }
    }
}
