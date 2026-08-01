using Backend_dotnet.DTOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Services.Interfaces
{
    public interface IItineraryService
    {
        Task<IEnumerable<ItineraryDto>> GetAllAsync();
        Task<ItineraryDto> GetByIdAsync(int id);
        Task<IEnumerable<ItineraryDto>> GetByCategoryIdAsync(int categoryId);
        Task<ItineraryDto> CreateAsync(ItineraryDto dto);
        Task<ItineraryDto> UpdateAsync(int id, ItineraryDto dto);
        Task<bool> DeleteAsync(int id);
        Task ImportCsvAsync(IFormFile file);
    }
}
