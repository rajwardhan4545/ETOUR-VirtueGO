using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Repositories.Implementations
{
    public class TourRepository
        : GenericRepository<tour_master>, ITourRepository
    {
        

        public TourRepository(AppDbContext context) : base(context)
        {
    
        }

        public async Task<IEnumerable<tour_master>> GetByCategoryIdAsync(int categoryId)
        {
            return await _context.tour_master
                .Include(t => t.category)
                    .ThenInclude(c => c.itinerary_master)
                .Include(t => t.category)
                    .ThenInclude(c => c.cost_master)
                .Include(t => t.category)
                    .ThenInclude(c => c.departure_master)
                .Include(t => t.departure)
                .Include(t => t.tour_guide)
                .Where(t => t.category_id == categoryId)
                .ToListAsync();
        }

        public async Task<IEnumerable<tour_master>> GetByCategoryIdsAsync(IEnumerable<int> categoryIds)
        {
            return await _context.tour_master
                .Include(t => t.category)
                    .ThenInclude(c => c.itinerary_master)
                .Include(t => t.category)
                    .ThenInclude(c => c.cost_master)
                .Include(t => t.category)
                    .ThenInclude(c => c.departure_master)
                .Include(t => t.departure)
                .Include(t => t.tour_guide)
                .Where(t => categoryIds.Contains(t.category_id))
                .ToListAsync();
        }

       
        public async Task<tour_master?> GetByCategoryIdAndDepartureIdAsync(
            int categoryId,
            int departureId)
        {
            return await _context.tour_master
                .FirstOrDefaultAsync(t =>
                    t.category_id == categoryId &&
                    t.departure_id == departureId);
        }
    }
}
