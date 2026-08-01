using Backend_dotnet.DTOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Services.Interfaces
{
    public interface IDepartureService
    {
        Task<IEnumerable<DepartureDto>> GetAllAsync();
        Task<DepartureDto> GetByIdAsync(int id);
        Task<IEnumerable<DepartureDto>> GetByCategoryIdAsync(int categoryId);
        Task<DepartureDto> CreateAsync(DepartureDto dto);
        Task<DepartureDto> UpdateAsync(int id, DepartureDto dto);
        Task<bool> DeleteAsync(int id);
    }
}
