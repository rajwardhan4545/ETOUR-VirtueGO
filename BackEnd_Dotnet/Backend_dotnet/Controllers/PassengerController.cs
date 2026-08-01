using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using Backend_dotnet.DTOs;
using Backend_dotnet.Services.Interfaces;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("api/passenger")]
    public class PassengerController : ControllerBase
    {
        private readonly IPassengerService _passengerService;

        // Constructor Injection
        public PassengerController(IPassengerService passengerService)
        {
            _passengerService = passengerService;
        }

        // POST: api/passengers/add
        [HttpPost("add")]
        public IActionResult AddPassenger([FromBody] PassengerDto passengerDto)
        {
            var result = _passengerService.AddPassenger(passengerDto);

            if (!result.Success)
            {
                return BadRequest(new
                {
                    message = result.Message
                });
            }

            return Ok(result.Data);
        }

        // GET: api/passengers/booking/{bookingId}
        [HttpGet("booking/{bookingId}")]
        public ActionResult<List<PassengerDto>> GetPassengersByBooking(int bookingId)
        {
            var passengers = _passengerService.GetPassengersByBookingId(bookingId);
            return Ok(passengers);
        }

        // GET: api/passengers/{id}
        [HttpGet("{id}")]
        public ActionResult<PassengerDto> GetPassengerById(int id)
        {
            var passenger = _passengerService.GetPassengerById(id);
            if (passenger == null)
                return NotFound();

            return Ok(passenger);
        }
    }
}
