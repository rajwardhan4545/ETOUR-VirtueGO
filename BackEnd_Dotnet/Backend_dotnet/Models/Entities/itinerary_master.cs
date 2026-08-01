﻿using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("category_id", "day_no", Name = "category_id", IsUnique = true)]
public partial class itinerary_master
{
    [Key]
    public int itinerary_id { get; set; }

    public int category_id { get; set; }

    public int day_no { get; set; }

    [Column(TypeName = "tinytext")]
    public string itinerary_detail { get; set; } = null!;

    [StringLength(255)]
    public string? day_wise_image { get; set; }

    [ForeignKey("category_id")]
    [InverseProperty("itinerary_master")]
    public virtual category_master category { get; set; } = null!;
}