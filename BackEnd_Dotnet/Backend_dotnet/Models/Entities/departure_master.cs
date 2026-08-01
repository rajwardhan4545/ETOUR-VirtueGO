using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("category_id", Name = "fk_departure_category")]
public partial class departure_master
{
    [Key]
    public int departure_id { get; set; }

    public int category_id { get; set; }

    public DateOnly depart_date { get; set; }

    public DateOnly end_date { get; set; }

    public int no_of_days { get; set; }

    [ForeignKey("category_id")]
    [InverseProperty("departure_master")]
    public virtual category_master category { get; set; } = null!;

    [InverseProperty("departure")]
    public virtual ICollection<tour_master> tour_master { get; set; } = new List<tour_master>();
}
