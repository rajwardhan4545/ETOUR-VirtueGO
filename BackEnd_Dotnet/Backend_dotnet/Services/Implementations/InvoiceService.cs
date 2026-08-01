using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;
using QuestPDF.Fluent;
using QuestPDF.Helpers;
using QuestPDF.Infrastructure;

namespace Backend_dotnet.Services.Implementations
{
    public class InvoiceService : IInvoiceService
    {
        private readonly IPaymentRepository _paymentRepository;
        private readonly IPassengerService _passengerService;
        private readonly ILogger<InvoiceService> _logger;

        public InvoiceService(
            IPaymentRepository paymentRepository,
            IPassengerService passengerService,
            ILogger<InvoiceService> logger)
        {
            _paymentRepository = paymentRepository;
            _passengerService = passengerService;
            _logger = logger;

            // QuestPDF Community License
            QuestPDF.Settings.License = LicenseType.Community;
        }

        public int? GetPaymentIdByBookingId(int bookingId)
        {
            var payment = _paymentRepository.FindByBookingIdAndStatus(bookingId, "SUCCESS");
            return payment?.payment_id;
        }

        public async Task<byte[]> GenerateInvoiceAsync(int paymentId)
        {
            var payments = _paymentRepository.FindAllByBookingId(paymentId);
            var payment = payments.FirstOrDefault(p => p.payment_id == paymentId);

            if (payment == null)
            {
                // Try finding by payment ID directly
                payment = _paymentRepository.FindByBookingIdAndStatus(paymentId, "SUCCESS");
            }

            if (payment == null)
                throw new Exception("Payment not found");

            if (payment.payment_status != "SUCCESS")
                throw new Exception("Payment not successful");

            var booking = payment.booking;
            if (booking == null)
                throw new Exception("Booking not found for payment");

            var passengers = _passengerService.GetPassengersByBookingId(booking.booking_id);

            _logger.LogInformation("Generating invoice for payment {PaymentId}, booking {BookingId}", 
                paymentId, booking.booking_id);

            return GeneratePdf(payment, booking, passengers);
        }

        private byte[] GeneratePdf(payment_master payment, booking_header booking, List<PassengerDto> passengers)
        {
            var customerName = $"{booking.customer?.first_name} {booking.customer?.last_name}";
            var tourName = booking.tour?.category?.category_name ?? "Tour Package";
            var guides = booking.tour?.tour_guide?.ToList() ?? new List<tour_guide>();

            var document = Document.Create(container =>
            {
                container.Page(page =>
                {
                    page.Size(PageSizes.A4);
                    page.Margin(36);
                    page.DefaultTextStyle(x => x.FontSize(11));

                    page.Header().Element(ComposeHeader);
                    page.Content().Element(content => ComposeContent(content, payment, booking, passengers, customerName, tourName, guides));
                    page.Footer().Element(ComposeFooter);
                });
            });

            return document.GeneratePdf();
        }

        private void ComposeHeader(IContainer container)
        {
            container.Column(column =>
            {
                column.Item().Text("VirtuGo Invoice")
                    .FontSize(24)
                    .Bold()
                    .FontColor(Colors.Blue.Medium)
                    .AlignCenter();

                column.Item().PaddingBottom(20);
            });
        }

        private void ComposeContent(IContainer container, payment_master payment, booking_header booking, 
            List<PassengerDto> passengers, string customerName, string tourName, List<tour_guide> guides)
        {
            container.Column(column =>
            {
                // Company & Billing Info
                column.Item().Row(row =>
                {
                    row.RelativeItem().Column(col =>
                    {
                        col.Item().Text("VirtuGo").Bold();
                        col.Item().Text("SM VITA");
                        col.Item().Text("Mumbai, India");
                        col.Item().Text("📞 +91-9326923786");
                        col.Item().Text("🌐 www.VirtuGo.com");
                    });

                    row.RelativeItem().AlignRight().Column(col =>
                    {
                        col.Item().Text("Billed To:").Bold();
                        col.Item().Text(customerName);
                        col.Item().Text($"Booking Date: {booking.booking_date:yyyy-MM-dd}");
                        col.Item().Text($"Booking ID: #{booking.booking_id}");
                    });
                });

                column.Item().PaddingVertical(15);

                // Invoice Summary Table
                column.Item().Table(table =>
                {
                    table.ColumnsDefinition(columns =>
                    {
                        columns.RelativeColumn(3);
                        columns.RelativeColumn(1);
                        columns.RelativeColumn(2);
                        columns.RelativeColumn(2);
                    });

                    table.Header(header =>
                    {
                        header.Cell().Background(Colors.Grey.Lighten2).Padding(5).Text("Tour Package").Bold();
                        header.Cell().Background(Colors.Grey.Lighten2).Padding(5).AlignCenter().Text("Passengers").Bold();
                        header.Cell().Background(Colors.Grey.Lighten2).Padding(5).AlignRight().Text("Base Price (₹)").Bold();
                        header.Cell().Background(Colors.Grey.Lighten2).Padding(5).AlignRight().Text("Total Price (₹)").Bold();
                    });

                    table.Cell().BorderBottom(1).Padding(5).Text(tourName);
                    table.Cell().BorderBottom(1).Padding(5).AlignCenter().Text(booking.no_of_pax.ToString());
                    table.Cell().BorderBottom(1).Padding(5).AlignRight().Text($"₹{booking.tour_amount:N2}");
                    table.Cell().BorderBottom(1).Padding(5).AlignRight().Text($"₹{booking.total_amount:N2}");
                });

                column.Item().PaddingVertical(15);

                // Passenger Details
                column.Item().Text("Passenger Details").Bold().FontSize(14);
                column.Item().PaddingVertical(8);

                column.Item().Table(table =>
                {
                    table.ColumnsDefinition(columns =>
                    {
                        columns.RelativeColumn(3);
                        columns.RelativeColumn(2);
                        columns.RelativeColumn(2);
                        columns.RelativeColumn(2);
                    });

                    table.Header(header =>
                    {
                        header.Cell().Background(Colors.Grey.Lighten2).Padding(5).Text("Name").Bold();
                        header.Cell().Background(Colors.Grey.Lighten2).Padding(5).AlignCenter().Text("Type").Bold();
                        header.Cell().Background(Colors.Grey.Lighten2).Padding(5).AlignCenter().Text("DOB").Bold();
                        header.Cell().Background(Colors.Grey.Lighten2).Padding(5).AlignRight().Text("Amount (₹)").Bold();
                    });

                    foreach (var p in passengers)
                    {
                        table.Cell().BorderBottom(1).Padding(5).Text(p.PaxName);
                        table.Cell().BorderBottom(1).Padding(5).AlignCenter().Text(p.PaxType);
                        table.Cell().BorderBottom(1).Padding(5).AlignCenter().Text($"{p.PaxBirthdate:yyyy-MM-dd}");
                        table.Cell().BorderBottom(1).Padding(5).AlignRight().Text($"₹{p.PaxAmount:N2}");
                    }
                });

                // Tour Guide Information
                if (guides.Any())
                {
                    column.Item().PaddingVertical(15);
                    column.Item().Text("Tour Guide Information").Bold().FontSize(14);
                    column.Item().PaddingVertical(8);

                    column.Item().Table(table =>
                    {
                        table.ColumnsDefinition(columns =>
                        {
                            columns.RelativeColumn();
                            columns.RelativeColumn();
                            columns.RelativeColumn();
                        });

                        table.Header(header =>
                        {
                            header.Cell().Background(Colors.Grey.Lighten2).Padding(5).Text("Name").Bold();
                            header.Cell().Background(Colors.Grey.Lighten2).Padding(5).Text("Email").Bold();
                            header.Cell().Background(Colors.Grey.Lighten2).Padding(5).Text("Phone").Bold();
                        });

                        foreach (var g in guides)
                        {
                            table.Cell().BorderBottom(1).Padding(5).Text(g.name ?? "N/A");
                            table.Cell().BorderBottom(1).Padding(5).Text(g.email ?? "N/A");
                            table.Cell().BorderBottom(1).Padding(5).Text(g.phone ?? "N/A");
                        }
                    });
                }

                // Payment Summary
                column.Item().PaddingVertical(15);
                column.Item().AlignRight().Width(200).Table(table =>
                {
                    table.ColumnsDefinition(columns =>
                    {
                        columns.RelativeColumn();
                        columns.RelativeColumn();
                    });

                    table.Cell().Padding(5).Text("Subtotal:");
                    table.Cell().Padding(5).AlignRight().Text($"₹{booking.tour_amount:N2}");

                    table.Cell().Padding(5).Text("Tax (10%):");
                    table.Cell().Padding(5).AlignRight().Text($"₹{booking.taxes:N2}");

                    table.Cell().Padding(5).Text("Total Amount:").Bold();
                    table.Cell().Padding(5).AlignRight().Text($"₹{booking.total_amount:N2}").Bold().FontColor(Colors.Red.Medium);
                });
            });
        }

        private void ComposeFooter(IContainer container)
        {
            container.Column(column =>
            {
                column.Item().PaddingTop(30).AlignCenter().Text(text =>
                {
                    text.Span("Thank you for choosing VirtuGO!").Italic().FontColor(Colors.Grey.Medium);
                });
                column.Item().AlignCenter().Text(text =>
                {
                    text.Span("Your gateway to amazing experiences.").Italic().FontColor(Colors.Grey.Medium);
                });
                column.Item().AlignCenter().Text("www.virtugo.com").FontColor(Colors.Grey.Medium);
            });
        }
    }
}
