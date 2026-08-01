using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Backend_dotnet.Repositories.Implementations
{
    public class ItineraryRepository : GenericRepository<itinerary_master>, IItineraryRepository
    {
        public ItineraryRepository(AppDbContext context) : base(context)
        {
        }

        public async Task<IEnumerable<itinerary_master>> GetByCategoryIdAsync(int categoryId)
        {
            return await _context.itinerary_master
                .Where(i => i.category_id == categoryId)
                .OrderBy(i => i.day_no)
                .ToListAsync();
        }
    }
}
