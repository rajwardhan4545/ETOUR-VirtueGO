using Xunit;
using Moq;
using Backend_dotnet.Controllers;
using Backend_dotnet.Services.Interfaces;
using Backend_dotnet.DTOs;
using Microsoft.Extensions.Logging;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;

namespace Backend_dotnet.Tests.Controllers
{
    public class BookingControllerTests
    {
        private readonly Mock<IBookingService> _mockBookingService;
        private readonly Mock<ILogger<BookingController>> _mockLogger;
        private readonly BookingController _bookingController;

        public BookingControllerTests()
        {
            _mockBookingService = new Mock<IBookingService>();
            _mockLogger = new Mock<ILogger<BookingController>>();
            _bookingController = new BookingController(_mockBookingService.Object, _mockLogger.Object);
        }

        [Fact]
        public async Task GetAll_ReturnsOkWithBookings()
        {
            // Arrange
            var bookings = new List<BookingResponseDto>
            {
                new BookingResponseDto { BookingId = 1 },
                new BookingResponseDto { BookingId = 2 }
            };

            _mockBookingService.Setup(s => s.GetAllAsync())
                .ReturnsAsync(bookings);

            // Act
            var result = await _bookingController.GetAll();

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnedBookings = Assert.IsAssignableFrom<IEnumerable<BookingResponseDto>>(okResult.Value);
            Assert.Equal(2, returnedBookings.Count());
        }

        [Fact]
        public async Task GetById_BookingExists_ReturnsOkWithBooking()
        {
            // Arrange
            int bookingId = 1;
            var booking = new BookingResponseDto { BookingId = bookingId };

            _mockBookingService.Setup(s => s.GetByIdAsync(bookingId))
                .ReturnsAsync(booking);

            // Act
            var result = await _bookingController.GetById(bookingId);

            // Assert
            var actionResult = Assert.IsType<ActionResult<BookingResponseDto>>(result);
            var okResult = Assert.IsType<OkObjectResult>(actionResult.Result);
            var returnedBooking = Assert.IsType<BookingResponseDto>(okResult.Value);
            Assert.Equal(bookingId, returnedBooking.BookingId);
        }

        [Fact]
        public async Task GetCustomerBookings_ReturnsOkWithBookings()
        {
            // Arrange
            int customerId = 1;
            var bookings = new List<BookingResponseDto>
            {
                new BookingResponseDto { CustomerId = customerId }
            };

            _mockBookingService.Setup(s => s.GetCustomerBookingsAsync(customerId))
                .ReturnsAsync(bookings);

            // Act
            var result = await _bookingController.GetCustomerBookings(customerId);

            // Assert
            var actionResult = Assert.IsType<ActionResult<IEnumerable<BookingResponseDto>>>(result);
            var okResult = Assert.IsType<OkObjectResult>(actionResult.Result);
            var returnedBookings = Assert.IsAssignableFrom<IEnumerable<BookingResponseDto>>(okResult.Value);
            Assert.Single(returnedBookings);
        }

        [Fact]
        public async Task Create_ValidDto_ReturnsCreatedAtAction()
        {
            // Arrange
            var dto = new BookingCreateDto { CustomerId = 1, TourId = 1 };
            var createdBooking = new BookingResponseDto { BookingId = 1, CustomerId = 1 };

            _mockBookingService.Setup(s => s.CreateAsync(dto))
                .ReturnsAsync(createdBooking);

            // Act
            var result = await _bookingController.Create(dto);

            // Assert
            var actionResult = Assert.IsType<ActionResult<BookingResponseDto>>(result);
            var createdResult = Assert.IsType<CreatedAtActionResult>(actionResult.Result);
            var returnedBooking = Assert.IsType<BookingResponseDto>(createdResult.Value);
            Assert.Equal(createdBooking.BookingId, returnedBooking.BookingId);
        }

        [Fact]
        public async Task GetPaymentStatus_ReturnsOkWithStatus()
        {
            // Arrange
            int bookingId = 1;
            int status = 1;

            _mockBookingService.Setup(s => s.GetPaymentStatusAsync(bookingId))
                .ReturnsAsync(status);

            // Act
            var result = await _bookingController.GetPaymentStatus(bookingId);

            // Assert
            var actionResult = Assert.IsType<ActionResult<int>>(result);
            var okResult = Assert.IsType<OkObjectResult>(actionResult.Result);
            Assert.Equal(status, okResult.Value);
        }
    }
}
