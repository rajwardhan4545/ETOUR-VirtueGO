using Backend_dotnet.Models.Entities;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Repositories.Interfaces
{
    public interface IItineraryRepository : IGenericRepository<itinerary_master>
    {
        Task<IEnumerable<itinerary_master>> GetByCategoryIdAsync(int categoryId);
    }
}
