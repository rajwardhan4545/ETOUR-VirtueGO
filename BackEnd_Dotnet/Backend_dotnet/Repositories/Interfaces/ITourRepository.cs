using System.Collections.Generic;
using System.Threading.Tasks;
using Backend_dotnet.Models.Entities;

namespace Backend_dotnet.Repositories.Interfaces
{
    public interface ITourRepository:IGenericRepository<tour_master>
    {

        // Custom methods (MATCHING Java exactly)
        Task<IEnumerable<tour_master>> GetByCategoryIdAsync(int categoryId);

        Task<IEnumerable<tour_master>> GetByCategoryIdsAsync(IEnumerable<int> categoryIds);

        Task<tour_master?> GetByCategoryIdAndDepartureIdAsync(
            int categoryId,
            int departureId
        );
    }
}
