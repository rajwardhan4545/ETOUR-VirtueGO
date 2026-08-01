using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("category_id", Name = "fk_tour_category")]
[Index("departure_id", Name = "fk_tour_departure")]
public partial class tour_master
{
    [Key]
    public int tour_id { get; set; }

    public int category_id { get; set; }

    public int departure_id { get; set; }

    [InverseProperty("tour")]
    public virtual ICollection<booking_header> booking_header { get; set; } = new List<booking_header>();

    [ForeignKey("category_id")]
    [InverseProperty("tour_master")]
    public virtual category_master category { get; set; } = null!;

    [ForeignKey("departure_id")]
    [InverseProperty("tour_master")]
    public virtual departure_master departure { get; set; } = null!;

    [InverseProperty("tour")]
    public virtual ICollection<tour_guide> tour_guide { get; set; } = new List<tour_guide>();
}
