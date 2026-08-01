using System;
using System.Collections.Generic;

namespace Backend_dotnet.DTOs
{
    public class ScheduledTourDto
    {
        public int TourId { get; set; }
        public int DepartureId { get; set; }
        public DateOnly DepartDate { get; set; }
        public DateOnly EndDate { get; set; }
        public int NoOfDays { get; set; }
        public List<TourGuideDto> Guides { get; set; } = new List<TourGuideDto>();
    }
}
