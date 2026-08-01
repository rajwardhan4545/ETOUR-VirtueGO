using System.Collections.Generic;

namespace Backend_dotnet.DTOs
{
    public class TourDto
    {
        public int? Id { get; set; }
        public bool? JumpFlag { get; set; }

        public int? CategoryId { get; set; }
        public string? CategoryCode { get; set; }
        public string? CategoryName { get; set; }
        public string? SubCategoryCode { get; set; }

        public string? ImagePath { get; set; }

        public int? DepartureId { get; set; }

        public List<ItineraryDto>? Itineraries { get; set; }
        public List<CostDto>? Costs { get; set; }
        public List<DepartureDto>? Departures { get; set; }
        public List<TourGuideDto>? Guides { get; set; }
    }
}
