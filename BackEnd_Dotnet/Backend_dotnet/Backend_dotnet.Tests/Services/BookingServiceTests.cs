using Xunit;
using Moq;
using Backend_dotnet.Services.Implementations;
using Backend_dotnet.Services.Interfaces;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using AutoMapper;
using Microsoft.Extensions.Logging;
using System.Threading.Tasks;
using System.Collections.Generic;
using System;
using System.Linq;

namespace Backend_dotnet.Tests.Services
{
    public class BookingServiceTests
    {
        private readonly Mock<IBookingRepository> _mockBookingRepository;
        private readonly Mock<IMapper> _mockMapper;
        private readonly Mock<ILogger<BookingService>> _mockLogger;
        private readonly BookingService _bookingService;

        public BookingServiceTests()
        {
            _mockBookingRepository = new Mock<IBookingRepository>();
            _mockMapper = new Mock<IMapper>();
            _mockLogger = new Mock<ILogger<BookingService>>();
            _bookingService = new BookingService(
                _mockBookingRepository.Object,
                _mockMapper.Object,
                _mockLogger.Object
            );
        }

        [Fact]
        public async Task GetByIdAsync_BookingExists_ReturnsBooking()
        {
            // Arrange
            int bookingId = 1;
            var booking = new booking_header
            {
                booking_id = bookingId,
                customer_id = 1,
                booking_date = DateOnly.FromDateTime(DateTime.Now),
                tour_amount = 100,
                taxes = 10,
                total_amount = 110,
                status_id = 1
            };
            
            _mockBookingRepository.Setup(repo => repo.GetBookingWithDetailsAsync(bookingId))
                .ReturnsAsync(booking);

            // Act
            var result = await _bookingService.GetByIdAsync(bookingId);

            // Assert
            Assert.NotNull(result);
            Assert.Equal(bookingId, result.BookingId);
        }

        [Fact]
        public async Task GetByIdAsync_BookingDoesNotExist_ThrowsKeyNotFoundException()
        {
            // Arrange
            int bookingId = 1;
            _mockBookingRepository.Setup(repo => repo.GetBookingWithDetailsAsync(bookingId))
                .ReturnsAsync((booking_header)null);

            // Act & Assert
            await Assert.ThrowsAsync<KeyNotFoundException>(() => _bookingService.GetByIdAsync(bookingId));
        }

        [Fact]
        public async Task GetAllAsync_ReturnsAllBookings()
        {
            // Arrange
            var bookings = new List<booking_header>
            {
                new booking_header { booking_id = 1, customer_id = 1 },
                new booking_header { booking_id = 2, customer_id = 2 }
            };

            _mockBookingRepository.Setup(repo => repo.GetAllAsync())
                .ReturnsAsync(bookings);

            // Act
            var result = await _bookingService.GetAllAsync();

            // Assert
            Assert.NotNull(result);
            Assert.Equal(2, result.Count());
        }

        [Fact]
        public async Task GetCustomerBookingsAsync_ReturnsBookingsForCustomer()
        {
            // Arrange
            int customerId = 1;
            var bookings = new List<booking_header>
            {
                new booking_header { booking_id = 1, customer_id = customerId }
            };

            _mockBookingRepository.Setup(repo => repo.GetCustomerBookingsAsync(customerId))
                .ReturnsAsync(bookings);

            // Act
            var result = await _bookingService.GetCustomerBookingsAsync(customerId);

            // Assert
            Assert.NotNull(result);
            Assert.Single(result);
            Assert.Equal(customerId, result.First().CustomerId);
        }

        [Fact]
        public async Task CreateAsync_ValidDto_CreatesBooking()
        {
            // Arrange
            var dto = new BookingCreateDto
            {
                CustomerId = 1,
                TourId = 1,
                NoOfPax = 2,
                TourAmount = 100,
                Taxes = 10
            };

            var createdBooking = new booking_header
            {
                booking_id = 1,
                customer_id = dto.CustomerId,
                tour_id = dto.TourId,
                no_of_pax = dto.NoOfPax
            };

            _mockBookingRepository.Setup(repo => repo.AddAsync(It.IsAny<booking_header>()))
                .ReturnsAsync(createdBooking);
            
            _mockBookingRepository.Setup(repo => repo.GetBookingWithDetailsAsync(createdBooking.booking_id))
                .ReturnsAsync(createdBooking);

            // Act
            var result = await _bookingService.CreateAsync(dto);

            // Assert
            Assert.NotNull(result);
            Assert.Equal(createdBooking.booking_id, result.BookingId);
        }

        [Fact]
        public async Task CreateAsync_InvalidCustomerId_ThrowsArgumentException()
        {
            // Arrange
            var dto = new BookingCreateDto { CustomerId = 0, TourId = 1, NoOfPax = 1 };

            // Act & Assert
            await Assert.ThrowsAsync<ArgumentException>(() => _bookingService.CreateAsync(dto));
        }

        [Fact]
        public async Task GetPaymentStatusAsync_BookingExists_ReturnsStatus()
        {
            // Arrange
            int bookingId = 1;
            int statusId = 1;
            var booking = new booking_header { booking_id = bookingId, status_id = statusId };

            _mockBookingRepository.Setup(repo => repo.GetByIdAsync(bookingId))
                .ReturnsAsync(booking);

            // Act
            var result = await _bookingService.GetPaymentStatusAsync(bookingId);

            // Assert
            Assert.Equal(statusId, result);
        }
    }
}
