using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("booking_id", Name = "fk_passenger_booking")]
public partial class Passenger
{
    [Key]
    public int pax_id { get; set; }

    public int booking_id { get; set; }

    [StringLength(255)]
    public string pax_name { get; set; } = null!;

   [Column(TypeName = "date")]
public DateTime pax_birthdate { get; set; }


    [Column(TypeName = "tinytext")]
    public string pax_type { get; set; } = null!;

    [Precision(10, 2)]
    public decimal pax_amount { get; set; }

    [ForeignKey("booking_id")]
    [InverseProperty("passenger")]
    public virtual booking_header booking { get; set; } = null!;
}
