using System.ComponentModel.DataAnnotations;

namespace LongManLoc.Models
{
    public class LoginModel
    {
        [Required(ErrorMessage = "Vui lÃ²ng nháº­p tÃªn Ä‘Äƒng nháº­p")]
        public string Username { get; set; } = string.Empty;

        [Required(ErrorMessage = "Vui lÃ²ng nháº­p máº­t kháº©u")]
        [DataType(DataType.Password)]
        public string Password { get; set; } = string.Empty;
    }
}
