
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Repositories.Interfaces
{
    public interface ISearchRepository
    {
        Task<IEnumerable<int>> GetCategoryIdsByNameAsync(string query);
        Task<IEnumerable<int>> GetCategoryIdsByDateRangeAsync(DateOnly fromDate, DateOnly toDate);
        Task<IEnumerable<int>> GetCategoryIdsByMaxCostAsync(decimal maxCost);
    }
}
