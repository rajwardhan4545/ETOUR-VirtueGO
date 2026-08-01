using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Backend_dotnet.Models.Entities;

[Index("status_name", Name = "status_name", IsUnique = true)]
public partial class booking_status_master
{
    [Key]
    public int status_id { get; set; }

    [StringLength(50)]
    public string status_name { get; set; } = null!;

    [InverseProperty("status")]
    public virtual ICollection<booking_header> booking_header { get; set; } = new List<booking_header>();
}
