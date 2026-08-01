
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Services.Implementations
{
    public class SearchService : ISearchService
    {
        private readonly ISearchRepository _searchRepository;

        public SearchService(ISearchRepository searchRepository)
        {
            _searchRepository = searchRepository;
        }

        public async Task<IEnumerable<int>> GetCategoryIdsByNameAsync(string query)
        {
            return await _searchRepository.GetCategoryIdsByNameAsync(query);
        }

        public async Task<IEnumerable<int>> GetCategoryIdsByDateRangeAsync(DateOnly fromDate, DateOnly toDate)
        {
            return await _searchRepository.GetCategoryIdsByDateRangeAsync(fromDate, toDate);
        }

        public async Task<IEnumerable<int>> GetCategoryIdsByMaxCostAsync(decimal maxCost)
        {
            return await _searchRepository.GetCategoryIdsByMaxCostAsync(maxCost);
        }
    }
}
