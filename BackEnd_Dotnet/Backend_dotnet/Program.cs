using Backend_dotnet.Configuration;
using Backend_dotnet.Data;
using Backend_dotnet.Middleware;
using Backend_dotnet.Repositories.Implementations;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services;
using Backend_dotnet.Services.Implementations;
using Backend_dotnet.Services.Interfaces;
using Backend_dotnet.Utilities;
using Backend_dotnet.Utilities.Helpers;
using Backend_dotnet.Utilities.Mappers;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using Serilog;

namespace Backend_dotnet
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // ================= LOGGING =================
            Log.Logger = new LoggerConfiguration()
                .ReadFrom.Configuration(builder.Configuration)
                .WriteTo.Console()
                .WriteTo.File("D://etour-.log", rollingInterval: RollingInterval.Day)
                .CreateLogger();

            builder.Host.UseSerilog();

            // ================= DATABASE =================
            DotNetEnv.Env.Load();

            var dbUser = Environment.GetEnvironmentVariable("DB_USER");
            var dbPassword = Environment.GetEnvironmentVariable("DB_PASS");

            if (string.IsNullOrWhiteSpace(dbUser) || string.IsNullOrWhiteSpace(dbPassword))
            {
                throw new Exception("Database environment variables missing");
            }

            var connectionString =
                $"server=localhost;database=e_tour;user={dbUser};password={dbPassword}";

            builder.Services.AddDbContext<AppDbContext>(options =>
                options.UseMySql(
                    connectionString,
                    ServerVersion.Parse("8.0.43-mysql")
                )
            );

            var mailId = Environment.GetEnvironmentVariable("MAIL_USERNAME");
            var mailToken = Environment.GetEnvironmentVariable("MAIL_PASSWORD");

            if (string.IsNullOrWhiteSpace(mailId) || string.IsNullOrWhiteSpace(mailToken))
            {
                throw new Exception("Email environment variables missing");
            }

            // Inject into appsettings-style configuration
            builder.Configuration["EmailSettings:SenderEmail"] = mailId;
            builder.Configuration["EmailSettings:SenderPassword"] = mailToken;


            // Read your existing env vars
            var clientId = Environment.GetEnvironmentVariable("CLIENT_ID");
            var clientSecret = Environment.GetEnvironmentVariable("CLIENT_KEY");

            if (string.IsNullOrWhiteSpace(clientId) || string.IsNullOrWhiteSpace(clientSecret))
            {
                throw new Exception("CLIENT_ID / CLIENT_key environment variables missing");
            }

            // Inject them into configuration so appsettings.json style works
            builder.Configuration["Google:ClientId"] = clientId;
            builder.Configuration["Google:ClientSecret"] = clientSecret;

            // ================= AUTOMAPPER =================
            builder.Services.AddAutoMapper(typeof(AutoMapperProfile));

            // ================= REPOSITORIES =================
            builder.Services.AddScoped(typeof(IGenericRepository<>), typeof(GenericRepository<>));
            builder.Services.AddScoped<ICategoryRepository, CategoryRepository>();
            builder.Services.AddScoped<IPaymentRepository, PaymentRepository>();
            builder.Services.AddScoped<ICustomerRepository, CustomerRepository>();
            
            builder.Services.AddScoped<IBookingRepository, BookingRepository>();
            builder.Services.AddScoped<IBookingService, BookingService>();

            // Tour Module
            builder.Services.AddScoped<ITourRepository, TourRepository>();
            builder.Services.AddScoped<ITourService, TourService>();

            // Extra Modules
            builder.Services.AddScoped<ICostRepository, CostRepository>();
            builder.Services.AddScoped<IItineraryRepository, ItineraryRepository>();
            builder.Services.AddScoped<IDepartureRepository, DepartureRepository>();
            builder.Services.AddScoped<ICostService, CostService>();
            builder.Services.AddScoped<IItineraryService, ItineraryService>();
            builder.Services.AddScoped<IDepartureService, DepartureService>();

            // ================= SERVICES =================
            builder.Services.AddScoped<ICategoryService, CategoryService>();
            builder.Services.AddScoped<IPaymentService, PaymentService>();
            builder.Services.AddScoped<IPaymentGatewayService, RazorpayService>();

            builder.Services.AddScoped<IPassengerService, PassengerService>();
            builder.Services.AddScoped<IPassengerRepository, PassengerRepository>();

            builder.Services.Configure<EmailSettings>(
            builder.Configuration.GetSection("EmailSettings"));

            // Search Module
            builder.Services.AddScoped<ISearchService, SearchService>();
            builder.Services.AddScoped<ISearchRepository, SearchRepository>();

            // Invoice & Email Module
            builder.Services.AddScoped<IInvoiceService, InvoiceService>();
            builder.Services.AddScoped<IInvoicePdfService, InvoicePdfService>();
            builder.Services.AddScoped<IEmailService, EmailService>();
            builder.Configuration.AddEnvironmentVariables();

            // ================= RAZORPAY CONFIG =================
            builder.Services.Configure<RazorpayOptions>(
                builder.Configuration.GetSection("Razorpay")
            );

            // ================= HELPERS =================
            builder.Services.AddScoped<EmailHelper>();
            builder.Services.AddScoped<ImageHelper>();

            // ================= JWT & AUTH (NATIVE - NO JAVA PROXY) =================
            builder.Services.AddSingleton<JwtService>();
            builder.Services.AddScoped<AuthService>();
            builder.Services.AddScoped<ICustomerService, CustomerService>();
            builder.Services.AddHttpContextAccessor();

            // ================= CORS =================
            builder.Services.AddCors(options =>
            {
                options.AddPolicy("AllowAll", policy =>
                {
                    policy.AllowAnyOrigin()
                          .AllowAnyMethod()
                          .AllowAnyHeader();
                });
            });

            // ================= MVC & SWAGGER =================
            builder.Services.AddControllers()
                .AddJsonOptions(options =>
                {
                    options.JsonSerializerOptions.PropertyNamingPolicy =
                        System.Text.Json.JsonNamingPolicy.CamelCase;
                });

            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen(options =>
            {
                options.SwaggerDoc("v1", new OpenApiInfo
                {
                    Title = "E-Tour API",
                    Version = "v1"
                });
            });

            builder.Services.AddAutoMapper(typeof(Program));

            var app = builder.Build();

            // ================= MIDDLEWARE PIPELINE =================
            // 1. CORS (MUST BE FIRST for error responses to work in browser/swagger)
            app.UseCors("AllowAll");

            // 2. Exception Handling
            app.UseMiddleware<ExceptionHandlingMiddleware>();

            // 3. Logging
            app.UseMiddleware<LoggingMiddleware>();

            // 4. JWT Authentication Middleware
            app.UseMiddleware<JwtMiddleware>();

            // 5. Swagger (Development only)
            if (app.Environment.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            // 6. Static Files
            app.UseStaticFiles(new StaticFileOptions
            {
                OnPrepareResponse = ctx =>
                {
                    // Cache images for 30 days
                    ctx.Context.Response.Headers["Cache-Control"] = "public,max-age=2592000";
                }
            });

            // 7. Map Controllers
            app.MapControllers();

            app.Run();
        }
    }
}