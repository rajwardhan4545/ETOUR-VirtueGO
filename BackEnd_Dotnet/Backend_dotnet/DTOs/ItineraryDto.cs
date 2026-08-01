namespace Backend_dotnet.DTOs
{
    public class ItineraryDto
    {
        public int ItineraryId { get; set; }
        public int DayNo { get; set; }
        public string ItineraryDetail { get; set; } = null!;
        public string? DayWiseImage { get; set; }
    }
}
