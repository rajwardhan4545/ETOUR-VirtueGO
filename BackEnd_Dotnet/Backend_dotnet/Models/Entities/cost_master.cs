using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("category_id", Name = "fk_cost_category")]
public partial class cost_master
{
    [Key]
    public int cost_id { get; set; }

    public int category_id { get; set; }

    [Precision(10, 2)]
    public decimal single_person_cost { get; set; }

    [Precision(10, 2)]
    public decimal extra_person_cost { get; set; }

    [Precision(10, 2)]
    public decimal child_with_bed_cost { get; set; }

    [Precision(10, 2)]
    public decimal child_without_bed_cost { get; set; }

    public DateOnly valid_from { get; set; }

    public DateOnly valid_to { get; set; }

    [ForeignKey("category_id")]
    [InverseProperty("cost_master")]
    public virtual category_master category { get; set; } = null!;
}
