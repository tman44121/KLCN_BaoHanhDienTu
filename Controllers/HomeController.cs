using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace LongManLoc.Controllers
{
    [Authorize] // Báº£o vá»‡ toÃ n bá»™ controller: chÆ°a Ä‘Äƒng nháº­p sáº½ redirect vá» /Account/Login
    public class HomeController : Controller
    {
        // GET: /Home/Index
        public IActionResult Index()
        {
            return View();
        }
    }
}
