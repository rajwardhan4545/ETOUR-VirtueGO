using AutoMapper;
using Backend_dotnet.Models.Entities;
//using Backend_dotnet.DTOs.Customer;
//using Backend_dotnet.DTOs.Booking;
//using Backend_dotnet.DTOs.Tour;
// Add other DTOs as needed

namespace Backend_dotnet.Utilities.Mappers
{
    /// <summary>
    /// AutoMapper profile for entity to DTO mappings
    /// </summary>
    public class AutoMapperProfile : Profile
    {
        public AutoMapperProfile()
        {
            //Customer mappings
            //CreateMap<customer_master, CustomerResponseDto>();
            //CreateMap<customer_master, CustomerProfileDto>()
            //    .ForMember(dest => dest.FullName, opt => opt.MapFrom(src => $"{src.first_name} {src.last_name}"));
            //CreateMap<customer_master, CustomerListDto>();

            //Add more mappings as other team members create their DTOs
            //Example:
            // CreateMap<booking_header, BookingResponseDto>();
            //CreateMap<tour_master, TourResponseDto>();
        }
    }
}