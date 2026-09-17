using System.ComponentModel.DataAnnotations;

namespace LongManLoc.Models
{
    public class RegisterModel
    {
        [Required(ErrorMessage = "Vui lÃ²ng nháº­p há» vÃ  tÃªn")]
        [Display(Name = "Há» vÃ  TÃªn")]
        public string FullName { get; set; } = string.Empty;

        [Required(ErrorMessage = "Vui lÃ²ng nháº­p sá»‘ Ä‘iá»‡n thoáº¡i")]
        [Display(Name = "Sá»‘ Ä‘iá»‡n thoáº¡i")]
        public string Phone { get; set; } = string.Empty;

        [Required(ErrorMessage = "Vui lÃ²ng nháº­p email")]
        [EmailAddress(ErrorMessage = "Email khÃ´ng há»£p lá»‡")]
        [Display(Name = "Email")]
        public string Email { get; set; } = string.Empty;

        [Required(ErrorMessage = "Vui lÃ²ng nháº­p máº­t kháº©u")]
        [MinLength(6, ErrorMessage = "Máº­t kháº©u pháº£i cÃ³ Ã­t nháº¥t 6 kÃ½ tá»±")]
        [DataType(DataType.Password)]
        [Display(Name = "Máº­t kháº©u")]
        public string Password { get; set; } = string.Empty;

        [Required(ErrorMessage = "Vui lÃ²ng xÃ¡c nháº­n máº­t kháº©u")]
        [DataType(DataType.Password)]
        [Compare("Password", ErrorMessage = "Máº­t kháº©u xÃ¡c nháº­n khÃ´ng khá»›p")]
        [Display(Name = "XÃ¡c nháº­n máº­t kháº©u")]
        public string ConfirmPassword { get; set; } = string.Empty;
    }
}
