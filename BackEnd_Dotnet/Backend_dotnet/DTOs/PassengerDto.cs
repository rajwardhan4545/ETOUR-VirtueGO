using System;
using System.ComponentModel.DataAnnotations;

namespace Backend_dotnet.DTOs
{
    public class PassengerDto
    {
        public int? Id { get; set; }

        public int BookingId { get; set; }

        [Required]
        [MaxLength(100)]
        public string PaxName { get; set; }

        public DateTime PaxBirthdate { get; set; }

        // Adult, Child, Infant
        [Required]
        public string PaxType { get; set; }

        public decimal PaxAmount { get; set; }
    }
}
