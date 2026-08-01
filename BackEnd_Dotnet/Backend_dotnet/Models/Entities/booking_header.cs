using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("customer_id", Name = "fk_booking_customer")]
[Index("status_id", Name = "fk_booking_status")]
[Index("tour_id", Name = "fk_booking_tour")]
public partial class booking_header
{
    [Key]
    public int booking_id { get; set; }

    public DateOnly booking_date { get; set; }

    public int customer_id { get; set; }

    public int tour_id { get; set; }

    public int no_of_pax { get; set; }

    [Precision(10, 2)]
    public decimal tour_amount { get; set; }

    [Precision(10, 2)]
    public decimal taxes { get; set; }

    [Precision(10, 2)]
    public decimal? total_amount { get; set; }

    public int status_id { get; set; }

    [ForeignKey("customer_id")]
    [InverseProperty("booking_header")]
    public virtual customer_master customer { get; set; } = null!;

    [InverseProperty("booking")]
    public virtual ICollection<Passenger> passenger { get; set; } = new List<Passenger>();

    [InverseProperty("booking")]
    public virtual ICollection<payment_master> payment_master { get; set; } = new List<payment_master>();

    [ForeignKey("status_id")]
    [InverseProperty("booking_header")]
    public virtual booking_status_master status { get; set; } = null!;

    [ForeignKey("tour_id")]
    [InverseProperty("booking_header")]
    public virtual tour_master tour { get; set; } = null!;
}
