using Backend_dotnet.DTOs.Common;
using Backend_dotnet.DTOs;
using System.Collections.Generic;

namespace Backend_dotnet.Services.Interfaces
{
    public interface IPassengerService
    {
        ServiceResult<PassengerDto> AddPassenger(PassengerDto passengerDto);
        PassengerDto GetPassengerById(int id);
        List<PassengerDto> GetPassengersByBookingId(int bookingId);
    }
}