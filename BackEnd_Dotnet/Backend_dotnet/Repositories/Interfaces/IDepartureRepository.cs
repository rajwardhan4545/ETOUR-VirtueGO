using Backend_dotnet.Models.Entities;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Repositories.Interfaces
{
    public interface IDepartureRepository : IGenericRepository<departure_master>
    {
        Task<IEnumerable<departure_master>> GetByCategoryIdAsync(int categoryId);
    }
}
