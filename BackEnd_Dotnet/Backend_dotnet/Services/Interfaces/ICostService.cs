using Backend_dotnet.DTOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Services.Interfaces
{
    public interface ICostService
    {
        Task<IEnumerable<CostDto>> GetAllAsync();
        Task<CostDto> GetByIdAsync(int id);
        Task<IEnumerable<CostDto>> GetByCategoryIdAsync(int categoryId);
        Task<CostDto> CreateAsync(CostDto dto);
        Task<CostDto> UpdateAsync(int id, CostDto dto);
        Task<bool> DeleteAsync(int id);
    }
}
