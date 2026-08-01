using Microsoft.AspNetCore.Mvc;

namespace Backend_dotnet.Controllers
{
    [ApiController]
    [Route("actuator")]
    public class HealthController : ControllerBase
    {
        [HttpGet("health")]
        [Produces("application/vnd.spring-boot.actuator.v3+json", "application/json")]
        public IActionResult GetHealth()
        {
            return Ok(new { status = "UP" });
        }
    }
}
