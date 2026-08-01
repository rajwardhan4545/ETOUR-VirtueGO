using Backend_dotnet.Data;
using Backend_dotnet.DTOs;
using Backend_dotnet.DTOs.Common;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Services.Interfaces;
using System.Collections.Generic;
using System.Linq;


namespace Backend_dotnet.Services.Implementations
{
    public class PassengerService : IPassengerService
    {
        private readonly AppDbContext _context;

        public PassengerService(AppDbContext context)
        {
            _context = context;
        }

        public ServiceResult<PassengerDto> AddPassenger(PassengerDto passengerDto)
        {
            // 1️⃣ Validate booking exists
            bool bookingExists = _context.booking_header
                .Any(b => b.booking_id == passengerDto.BookingId);

            if (!bookingExists)
            {
               return ServiceResult<PassengerDto>
                    .Fail($"Invalid BookingId {passengerDto.BookingId}. Booking does not exist.");
            }

            // 2️⃣ Create passenger entity
            var passenger = new Passenger
            {
                booking_id = passengerDto.BookingId,
                pax_name = passengerDto.PaxName,
                pax_birthdate = passengerDto.PaxBirthdate,
                pax_type = passengerDto.PaxType,
                pax_amount = passengerDto.PaxAmount
            };

            // 3️⃣ Save to DB
            _context.passenger.Add(passenger);
            _context.SaveChanges();

            // 4️⃣ Return DTO
            passengerDto.Id = passenger.pax_id;
            return ServiceResult<PassengerDto>.Ok(passengerDto);
        }




        public PassengerDto GetPassengerById(int id)
        {
            var passenger = _context.passenger.FirstOrDefault(p => p.pax_id == id);
            if (passenger == null) return null;

            return new PassengerDto
            {
                Id = passenger.pax_id,
                BookingId = passenger.booking_id,
                PaxName = passenger.pax_name,
                PaxBirthdate = passenger.pax_birthdate,
                PaxType = passenger.pax_type,
                PaxAmount = passenger.pax_amount
            };
        }

        public List<PassengerDto> GetPassengersByBookingId(int bookingId)
        {
            return _context.passenger
                .Where(p => p.booking_id == bookingId)
                .Select(p => new PassengerDto
                {
                    Id = p.pax_id,
                    BookingId = p.booking_id,
                    PaxName = p.pax_name,
                    PaxBirthdate = p.pax_birthdate,
                    PaxType = p.pax_type,
                    PaxAmount = p.pax_amount
                }).ToList();
        }
    }
}
