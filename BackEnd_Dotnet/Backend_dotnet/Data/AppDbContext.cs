using System;
using System.Collections.Generic;
using Backend_dotnet.Models.Entities;
using Microsoft.EntityFrameworkCore;
using Pomelo.EntityFrameworkCore.MySql.Scaffolding.Internal;

namespace Backend_dotnet.Data;

public partial class AppDbContext : DbContext
{
    public AppDbContext()
    {
    }

    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
    {
    }

    public virtual DbSet<booking_header> booking_header { get; set; }

    public virtual DbSet<booking_status_master> booking_status_master { get; set; }

    public virtual DbSet<category_master> category_master { get; set; }

    public virtual DbSet<cost_master> cost_master { get; set; }

    public virtual DbSet<customer_master> customer_master { get; set; }

    public virtual DbSet<departure_master> departure_master { get; set; }

    public virtual DbSet<itinerary_master> itinerary_master { get; set; }

    public virtual DbSet<Passenger> passenger { get; set; }

    public virtual DbSet<payment_master> payment_master { get; set; }

    public virtual DbSet<tour_guide> tour_guide { get; set; }

    public virtual DbSet<tour_master> tour_master { get; set; }

    

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder
            .UseCollation("utf8mb4_0900_ai_ci")
            .HasCharSet("utf8mb4");

        modelBuilder.Entity<booking_header>(entity =>
        {
            entity.HasKey(e => e.booking_id).HasName("PRIMARY");

            entity.Property(e => e.status_id).HasDefaultValueSql("'1'");
            entity.Property(e => e.total_amount).HasComputedColumnSql("`tour_amount` + `taxes`", true);

            entity.HasOne(d => d.customer).WithMany(p => p.booking_header)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_booking_customer");

            entity.HasOne(d => d.status).WithMany(p => p.booking_header)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_booking_status");

            entity.HasOne(d => d.tour).WithMany(p => p.booking_header)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_booking_tour");
        });

        modelBuilder.Entity<booking_status_master>(entity =>
        {
            entity.HasKey(e => e.status_id).HasName("PRIMARY");
        });

        modelBuilder.Entity<category_master>(entity =>
        {
            entity.HasKey(e => e.category_id).HasName("PRIMARY");

            entity.Property(e => e.cat_code)
                .IsFixedLength()
                .HasComment("DOM, INT, VSL, CKD");
            entity.Property(e => e.jump_flag)
                .HasDefaultValueSql("'0'")
                .HasComment("Jump to tour page");
            entity.Property(e => e.subcat_code)
                .IsFixedLength()
                .HasComment("EUP, KSH, SEA");
        });

        modelBuilder.Entity<cost_master>(entity =>
        {
            entity.HasKey(e => e.cost_id).HasName("PRIMARY");

            entity.HasOne(d => d.category).WithMany(p => p.cost_master)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_cost_category");
        });

        modelBuilder.Entity<customer_master>(entity =>
        {
            entity.HasKey(e => e.customer_id).HasName("PRIMARY");

            entity.Property(e => e.auth_provider).HasDefaultValueSql("'LOCAL'");
            entity.Property(e => e.customer_role).HasDefaultValueSql("'CUSTOMER'");
        });

        modelBuilder.Entity<departure_master>(entity =>
        {
            entity.HasKey(e => e.departure_id).HasName("PRIMARY");

            entity.HasOne(d => d.category).WithMany(p => p.departure_master)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_departure_category");
        });

        modelBuilder.Entity<itinerary_master>(entity =>
        {
            entity.HasKey(e => e.itinerary_id).HasName("PRIMARY");

            entity.HasOne(d => d.category).WithMany(p => p.itinerary_master).HasConstraintName("fk_itinerary_category");
        });

        modelBuilder.Entity<Passenger>(entity =>
        {
            entity.HasKey(e => e.pax_id).HasName("PRIMARY");

            entity.HasOne(d => d.booking).WithMany(p => p.passenger).HasConstraintName("fk_passenger_booking");
        });

        modelBuilder.Entity<payment_master>(entity =>
        {
            entity.HasKey(e => e.payment_id).HasName("PRIMARY");

            entity.HasOne(d => d.booking).WithMany(p => p.payment_master).HasConstraintName("fk_payment_booking");
        });

        modelBuilder.Entity<tour_guide>(entity =>
        {
            entity.HasKey(e => e.tour_guide_id).HasName("PRIMARY");

            entity.HasOne(d => d.tour).WithMany(p => p.tour_guide)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK8a07y9n674kk4ottk66elg91b");
        });

        modelBuilder.Entity<tour_master>(entity =>
        {
            entity.HasKey(e => e.tour_id).HasName("PRIMARY");

            entity.HasOne(d => d.category).WithMany(p => p.tour_master)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_tour_category");

            entity.HasOne(d => d.departure).WithMany(p => p.tour_master)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_tour_departure");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
