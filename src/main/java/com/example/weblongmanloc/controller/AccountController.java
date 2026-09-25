package com.example.weblongmanloc.controller;

import com.example.weblongmanloc.model.LoginModel;
import com.example.weblongmanloc.model.RegisterModel;
import com.example.weblongmanloc.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/Login")
    public String login(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            return "redirect:/";
        }
        if (!model.containsAttribute("loginModel")) {
            model.addAttribute("loginModel", new LoginModel());
        }
        return "account/login";
    }

    @GetMapping("/Register")
    public String register(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            return "redirect:/";
        }
        if (!model.containsAttribute("registerModel")) {
            model.addAttribute("registerModel", new RegisterModel());
        }
        return "account/register";
    }

    @PostMapping("/Register")
    public String processRegister(@Valid @ModelAttribute("registerModel") RegisterModel registerModel,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (registerModel.getPassword() != null && !registerModel.getPassword().equals(registerModel.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerModel", "Mật khẩu xác nhận không khớp");
        }

        if (bindingResult.hasErrors()) {
            return "account/register";
        }

        try {
            accountService.registerCustomer(registerModel);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/Account/Login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "account/register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra trong quá trình đăng ký: " + e.getMessage());
            return "account/register";
        }
    }
}
