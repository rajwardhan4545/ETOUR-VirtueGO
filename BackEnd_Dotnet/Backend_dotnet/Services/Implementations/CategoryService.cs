using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;
using AutoMapper;

namespace Backend_dotnet.Services.Implementations
{
    public class CategoryService : ICategoryService
    {
        private readonly ICategoryRepository _categoryRepository;
        private readonly IMapper _mapper;

        public CategoryService(ICategoryRepository categoryRepository, IMapper mapper)
        {
            _categoryRepository = categoryRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<int>> GetCategoryIdsBySubCatAsync(string subcatCode)
        {
            return await _categoryRepository.GetCategoryIdsBySubCatAsync(subcatCode);
        }

        public async Task<IEnumerable<int>> GetHomeCategoryIdsAsync()
        {
            var categories = await _categoryRepository.GetCategoryIdsBySubCatAsync("^");

            return categories;
        }
    }
}