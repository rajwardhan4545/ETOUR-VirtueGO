
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Services.Interfaces
{
    public interface ISearchService
    {
        Task<IEnumerable<int>> GetCategoryIdsByNameAsync(string query);
        Task<IEnumerable<int>> GetCategoryIdsByDateRangeAsync(DateOnly fromDate, DateOnly toDate);
        Task<IEnumerable<int>> GetCategoryIdsByMaxCostAsync(decimal maxCost);
    }
}
