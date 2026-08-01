using System;

namespace Backend_dotnet.DTOs
{
    public class CostDto
    {
        public int CostId { get; set; }
        public int CategoryId { get; set; }
        public decimal SinglePersonCost { get; set; }
        public decimal ExtraPersonCost { get; set; }
        public decimal ChildWithBedCost { get; set; }
        public decimal ChildWithoutBedCost { get; set; }
        public DateOnly ValidFrom { get; set; }
        public DateOnly ValidTo { get; set; }
    }
}
