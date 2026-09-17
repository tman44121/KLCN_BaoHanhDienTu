using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace LongManLoc.Controllers
{
    [Authorize] // Bảo vệ toàn bộ controller: chưa đăng nhập sẽ redirect về /Account/Login
    public class HomeController : Controller
    {
        // GET: /Home/Index
        public IActionResult Index()
        {
            return View();
        }
    }
}
