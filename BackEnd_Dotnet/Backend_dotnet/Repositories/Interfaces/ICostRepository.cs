using Backend_dotnet.Models.Entities;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Repositories.Interfaces
{
    public interface ICostRepository : IGenericRepository<cost_master>
    {
        Task<IEnumerable<cost_master>> GetByCategoryIdAsync(int categoryId);
    }
}
