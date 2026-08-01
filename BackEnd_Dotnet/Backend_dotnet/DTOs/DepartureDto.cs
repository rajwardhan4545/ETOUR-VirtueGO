using System;

namespace Backend_dotnet.DTOs
{
    public class DepartureDto
    {
        public int DepartureId { get; set; }
        public DateOnly DepartDate { get; set; }
        public DateOnly EndDate { get; set; }
        public int NoOfDays { get; set; }
    }
}
