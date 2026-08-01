using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("tour_id", Name = "FK8a07y9n674kk4ottk66elg91b")]
public partial class tour_guide
{
    [Key]
    public int tour_guide_id { get; set; }

    [StringLength(100)]
    public string name { get; set; } = null!;

    public int tour_id { get; set; }

    [StringLength(255)]
    public string email { get; set; } = null!;

    [StringLength(20)]
    public string? phone { get; set; }

    [ForeignKey("tour_id")]
    [InverseProperty("tour_guide")]
    public virtual tour_master tour { get; set; } = null!;
}
