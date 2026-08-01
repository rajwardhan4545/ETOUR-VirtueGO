using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("booking_id", Name = "fk_payment_booking")]
[Index("transaction_ref", Name = "transaction_ref", IsUnique = true)]
public partial class payment_master
{
    [Key]
    public int payment_id { get; set; }

    public int booking_id { get; set; }

    [Column(TypeName = "datetime")]
    public DateTime payment_date { get; set; }

    [Column(TypeName = "tinytext")]
    public string payment_mode { get; set; } = null!;

    [StringLength(100)]
    public string? transaction_ref { get; set; }

    [Precision(10, 2)]
    public decimal payment_amount { get; set; }

    [StringLength(20)]
    public string payment_status { get; set; } = null!;

    [ForeignKey("booking_id")]
    [InverseProperty("payment_master")]
    public virtual booking_header booking { get; set; } = null!;
}
