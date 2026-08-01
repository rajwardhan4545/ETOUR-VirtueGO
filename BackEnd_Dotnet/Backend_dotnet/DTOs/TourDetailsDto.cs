using System.Collections.Generic;

namespace Backend_dotnet.DTOs
{
    public class TourDetailsDto
    {
        public int CategoryId { get; set; }
        public string CategoryName { get; set; } = null!;
        public string CatCode { get; set; } = null!;
        public string? SubCatCode { get; set; }
        public string? ImagePath { get; set; }
        
        public List<ItineraryDto> Itineraries { get; set; } = new List<ItineraryDto>();
        public List<CostDto> Costs { get; set; } = new List<CostDto>(); 
        public List<ScheduledTourDto> ScheduledTours { get; set; } = new List<ScheduledTourDto>();
        public List<DepartureDto> Departures { get; set; } = new List<DepartureDto>();
    }
}
