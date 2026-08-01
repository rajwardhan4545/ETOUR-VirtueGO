using Backend_dotnet.Data;
using Backend_dotnet.DTOs;
using Backend_dotnet.Services.Interfaces;
using Backend_dotnet.Utils.Mapper;
using iText.IO.Font.Constants;
using iText.Kernel.Colors;
using iText.Kernel.Font;
using iText.Kernel.Geom;
using iText.Kernel.Pdf;
using iText.Layout;
using iText.Layout.Borders;
using iText.Layout.Element;
using iText.Layout.Properties;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Services.Implementations
{
    public class InvoicePdfService : IInvoicePdfService
    {
        private readonly AppDbContext _context;

        private PdfFont _boldFont;
        private PdfFont _normalFont;

        public InvoicePdfService(AppDbContext context)
        {
            _context = context;
            try 
            {
                _boldFont = PdfFontFactory.CreateFont(StandardFonts.HELVETICA_BOLD);
                _normalFont = PdfFontFactory.CreateFont(StandardFonts.HELVETICA);
            }
            catch
            {
                // Fallback or rethrow with clear message
                // In some environments, StandardFonts might fail if dependencies are missing
                _boldFont = PdfFontFactory.CreateFont(StandardFonts.COURIER_BOLD);
                _normalFont = PdfFontFactory.CreateFont(StandardFonts.COURIER);
            }
        }

        public async Task<byte[]> GenerateInvoiceAsync(int paymentId)
        {
            // =============================
            // 🔵 FETCH PAYMENT + RELATIONS
            // =============================

            var payment = await _context.payment_master
                        .Include(p => p.booking)
                            .ThenInclude(b => b.customer)
                        .Include(p => p.booking)
                            .ThenInclude(b => b.tour)
                                .ThenInclude(t => t.category)
                        .Include(p => p.booking)
                            .ThenInclude(b => b.tour)
                                .ThenInclude(t => t.departure)
                        .Include(p => p.booking)
                            .ThenInclude(b => b.tour)
                                .ThenInclude(t => t.tour_guide)
                        .FirstOrDefaultAsync(p => p.payment_id == paymentId);

            if (payment == null)
                throw new Exception("Payment not found");

            if (payment.booking == null)
                throw new Exception("Booking not found for payment");

            if (!payment.payment_status.Equals(
                    "SUCCESS",
                    StringComparison.OrdinalIgnoreCase))
            {
                throw new Exception("Payment not successful");
            }

            // =============================
            // 🔵 FETCH PASSENGERS
            // =============================

            var passengers = await _context.passenger
                .Where(p => p.booking_id ==
                            payment.booking.booking_id)
                .ToListAsync();

            // =============================
            // 🔵 MAP TO DTO
            // =============================

            InvoiceDto invoice =
                InvoiceMapper.ToInvoiceDto(payment);

            // =============================
            // 🔵 GENERATE PDF
            // =============================

            using var ms = new MemoryStream();

            var writer = new PdfWriter(ms);
            var pdf = new PdfDocument(writer);
            var doc = new Document(pdf, PageSize.A4);

            doc.SetMargins(36, 36, 36, 36);

            // =====================
            // HEADER
            // =====================

            doc.Add(
                new Paragraph("VirtuGo Invoice")
                    .SetFont(_boldFont)
                    .SetFontSize(24)
                    .SetFontColor(ColorConstants.BLUE)
                    .SetTextAlignment(TextAlignment.CENTER)
                    .SetMarginBottom(25)
            );

            // =====================
            // INFO TABLE
            // =====================

            Table info = new Table(2)
                .UseAllAvailableWidth()
                .SetMarginBottom(20);

            info.AddCell(NoBorder(
                "VirtuGo\nSM VITA\nMumbai\n+91-9326923786\nwww.VirtuGo.com"));

            info.AddCell(NoBorder(
                $"Billed To:\n{invoice.CustomerName}\nBooking Date: {invoice.BookingDate}")
                .SetTextAlignment(TextAlignment.RIGHT));

            doc.Add(info);

            // =====================
            // TOUR TABLE
            // =====================

            Table tour = new Table(new float[] { 3, 1, 2, 2 })
                .UseAllAvailableWidth()
                .SetMarginBottom(20);

            Header(tour, "Tour Package");
            Header(tour, "Passengers");
            Header(tour, "Base Price (₹)");
            Header(tour, "Total Price (₹)");

            tour.AddCell(DataCell(invoice.TourName));
            tour.AddCell(Center(invoice.Passengers.ToString()));
            tour.AddCell(Right(invoice.BaseAmount.ToString()));
            tour.AddCell(Right(invoice.TotalAmount!.ToString()));

            doc.Add(tour);

            // =====================
            // PASSENGER TABLE
            // =====================

            doc.Add(new Paragraph("Passenger Details")
                .SetFont(_boldFont)
                .SetMarginTop(10)
                .SetMarginBottom(10));

            Table pax = new Table(4)
                .UseAllAvailableWidth()
                .SetMarginBottom(20);

            Header(pax, "Name");
            Header(pax, "Type");
            Header(pax, "DOB");
            Header(pax, "Amount (₹)");

            foreach (var p in passengers)
            {
                pax.AddCell(DataCell(p.pax_name));
                pax.AddCell(Center(p.pax_type));
                pax.AddCell(Center(p.pax_birthdate.ToString()));
                pax.AddCell(Right(p.pax_amount.ToString()));
            }

            doc.Add(pax);

            // =====================
            // TOUR GUIDE TABLE
            // =====================
            var guides = payment.booking.tour?.tour_guide;
            if (guides != null && guides.Any())
            {
                doc.Add(new Paragraph("Tour Guide Information")
                    .SetFont(_boldFont)
                    .SetMarginTop(10)
                    .SetMarginBottom(10));

                Table guideTable = new Table(3)
                    .UseAllAvailableWidth()
                    .SetMarginBottom(20);

                Header(guideTable, "Name");
                Header(guideTable, "Email");
                Header(guideTable, "Phone");

                foreach (var g in guides)
                {
                    guideTable.AddCell(DataCell(g.name));
                    guideTable.AddCell(DataCell(g.email));
                    guideTable.AddCell(DataCell(g.phone));
                }

                doc.Add(guideTable);
            }

            // =====================
            // TOTALS
            // =====================

            Table total = new Table(2)
                .SetWidth(UnitValue.CreatePercentValue(40))
                .SetHorizontalAlignment(HorizontalAlignment.RIGHT)
                .SetMarginBottom(25);

            NoBorderRow(total, "Subtotal:", invoice.BaseAmount);
            NoBorderRow(total, "Tax:", invoice.TaxAmount);
            NoBorderRow(total, "Total:", invoice.TotalAmount ?? 0, true);

            doc.Add(total);

            // =====================
            // FOOTER
            // =====================

            doc.Add(
                new Paragraph(
                    "Thank you for choosing VirtuGo!\nwww.etourvirtugo.com")
                    .SetFontSize(10)
                    .SetFontColor(ColorConstants.GRAY)
                    .SetTextAlignment(TextAlignment.CENTER)
                    .SetMarginTop(25)
            );

            doc.Close();

            return ms.ToArray();
        }

        // =====================
        // 🔧 HELPERS (WITH PADDING)
        // =====================

        private Cell Header(Table t, string text)
            => new Cell()
                .Add(new Paragraph(text).SetFont(_boldFont))
                .SetPadding(6)
                .SetBackgroundColor(ColorConstants.LIGHT_GRAY)
                .SetTextAlignment(TextAlignment.CENTER);

        private Cell DataCell(string text)
            => new Cell()
                .Add(new Paragraph(text).SetFont(_normalFont))
                .SetPadding(6);

        private Cell Center(string text)
            => new Cell()
                .Add(new Paragraph(text).SetFont(_normalFont))
                .SetPadding(6)
                .SetTextAlignment(TextAlignment.CENTER);

        private Cell Right(string text)
            => new Cell()
                .Add(new Paragraph(text).SetFont(_normalFont))
                .SetPadding(6)
                .SetTextAlignment(TextAlignment.RIGHT);

        private Cell NoBorder(string text)
            => new Cell()
                .Add(new Paragraph(text).SetFont(_normalFont))
                .SetPadding(5)
                .SetBorder(Border.NO_BORDER);

        private void NoBorderRow(
            Table t,
            string label,
            decimal value,
            bool bold = false)
        {
            t.AddCell(
                new Cell()
                    .Add(new Paragraph(label).SetFont(_normalFont))
                    .SetPadding(4)
                    .SetBorder(Border.NO_BORDER));

            var p = new Paragraph(value.ToString());

            p.SetFont(bold ? _boldFont : _normalFont);

            t.AddCell(
                new Cell()
                    .Add(p)
                    .SetPadding(4)
                    .SetBorder(Border.NO_BORDER)
                    .SetTextAlignment(TextAlignment.RIGHT));
        }
    }
}
