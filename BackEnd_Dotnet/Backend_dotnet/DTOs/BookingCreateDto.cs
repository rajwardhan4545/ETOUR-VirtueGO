using System;

namespace Backend_dotnet.DTOs
{
    public class BookingCreateDto
    {
        public int CustomerId { get; set; }
        public int TourId { get; set; }
        public int NoOfPax { get; set; }
        public decimal TourAmount { get; set; }
        public decimal Taxes { get; set; }
        public int StatusId { get; set; }
    }
}
