
using Backend_dotnet.Data;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Backend_dotnet.Repositories.Implementations
{
    public class SearchRepository : ISearchRepository
    {
        private readonly AppDbContext _context;

        public SearchRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<int>> GetCategoryIdsByNameAsync(string query)
        {
            if (string.IsNullOrWhiteSpace(query))
                return new List<int>();

            return await _context.category_master
                .AsNoTracking()
                .Where(c => (c.category_name.StartsWith(query) || c.cat_code.StartsWith(query)) && c.jump_flag == true)
                .Select(c => c.category_id)
                .ToListAsync();
        }

        public async Task<IEnumerable<int>> GetCategoryIdsByDateRangeAsync(DateOnly fromDate, DateOnly toDate)
        {
            return await _context.departure_master
                .AsNoTracking()
                .Include(d => d.category) // Ensure category is loaded to check flag
                .Where(d => d.depart_date >= fromDate && d.depart_date <= toDate && d.category.jump_flag == true)
                .Select(d => d.category_id)
                .Distinct()
                .ToListAsync();
        }

        public async Task<IEnumerable<int>> GetCategoryIdsByMaxCostAsync(decimal maxCost)
        {
            return await _context.cost_master
                .AsNoTracking()
                .Include(c => c.category) // Ensure category is loaded to check flag
                .Where(c => c.single_person_cost <= maxCost && c.category.jump_flag == true)
                .Select(c => c.category_id)
                .Distinct()
                .ToListAsync();
        }
    }
}
