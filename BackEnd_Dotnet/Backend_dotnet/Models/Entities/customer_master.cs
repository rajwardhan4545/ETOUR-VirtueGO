using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

public partial class customer_master
{
    [Key]
    public int customer_id { get; set; }

    [StringLength(255)]
    public string? email { get; set; }

    [StringLength(20)]
    public string? phone { get; set; }

    [Column(TypeName = "tinytext")]
    public string? address { get; set; }

    [StringLength(100)]
    public string first_name { get; set; } = null!;

    [StringLength(100)]
    public string last_name { get; set; } = null!;

    [StringLength(255)]
    public string? password { get; set; }

    [Column(TypeName = "enum('CUSTOMER','ADMIN')")]
    public string customer_role { get; set; } = null!;

    [Column(TypeName = "enum('LOCAL','GOOGLE','FACEBOOK')")]
    public string auth_provider { get; set; } = null!;

    public bool profile_completed { get; set; }

    [InverseProperty("customer")]
    public virtual ICollection<booking_header> booking_header { get; set; } = new List<booking_header>();
}
