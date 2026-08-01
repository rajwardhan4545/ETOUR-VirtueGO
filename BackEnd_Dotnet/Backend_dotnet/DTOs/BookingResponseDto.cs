namespace Backend_dotnet.DTOs
{
    /// <summary>
    /// Basic booking response DTO
    /// </summary>
    public class BookingResponseDto
    {
        public int BookingId { get; set; }
        public DateOnly BookingDate { get; set; }
        public int CustomerId { get; set; }
        public string CustomerName { get; set; }
        public int TourId { get; set; }
        public string TourName { get; set; }
        public int NoOfPax { get; set; }
        public decimal TourAmount { get; set; }
        public decimal Taxes { get; set; }
        public decimal TotalAmount { get; set; }
        public int StatusId { get; set; }
        public string StatusName { get; set; }

        public List<TourGuideDto> Guides { get; set; } = new();
    }
}