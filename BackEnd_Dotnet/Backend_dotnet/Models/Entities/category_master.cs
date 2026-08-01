using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("cat_code", "subcat_code", Name = "cat_code", IsUnique = true)]
public partial class category_master
{
    [Key]
    public int category_id { get; set; }

    /// <summary>
    /// DOM, INT, VSL, CKD
    /// </summary>
    [StringLength(3)]
    public string cat_code { get; set; } = null!;

    /// <summary>
    /// EUP, KSH, SEA
    /// </summary>
    [StringLength(3)]
    public string? subcat_code { get; set; }

    [StringLength(255)]
    public string category_name { get; set; } = null!;

    [StringLength(255)]
    public string? image_path { get; set; }

    /// <summary>
    /// Jump to tour page
    /// </summary>
    public bool? jump_flag { get; set; }

    [InverseProperty("category")]
    public virtual ICollection<cost_master> cost_master { get; set; } = new List<cost_master>();

    [InverseProperty("category")]
    public virtual ICollection<departure_master> departure_master { get; set; } = new List<departure_master>();

    [InverseProperty("category")]
    public virtual ICollection<itinerary_master> itinerary_master { get; set; } = new List<itinerary_master>();

    [InverseProperty("category")]
    public virtual ICollection<tour_master> tour_master { get; set; } = new List<tour_master>();
}
