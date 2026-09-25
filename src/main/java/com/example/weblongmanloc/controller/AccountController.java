package com.example.weblongmanloc.controller;

import com.example.weblongmanloc.dto.ProfileDto;
import com.example.weblongmanloc.model.ChangePasswordModel;
import com.example.weblongmanloc.model.LoginModel;
import com.example.weblongmanloc.model.RegisterModel;
import com.example.weblongmanloc.service.AccountService;
import com.example.weblongmanloc.service.DashboardService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Account")
public class AccountController {

    private final AccountService accountService;
    private final DashboardService dashboardService;

    @Autowired
    public AccountController(AccountService accountService, DashboardService dashboardService) {
        this.accountService = accountService;
        this.dashboardService = dashboardService;
    }

    // ─── Đăng nhập ───────────────────────────────────────────────────────────
    @GetMapping("/Login")
    public String login(Model model, Authentication authentication) {
        if (isAuthenticated(authentication)) return "redirect:/";
        if (!model.containsAttribute("loginModel")) {
            model.addAttribute("loginModel", new LoginModel());
        }
        return "account/login";
    }

    // ─── Đăng ký tài khoản ───────────────────────────────────────────────────
    @GetMapping("/Register")
    public String register(Model model, Authentication authentication) {
        if (isAuthenticated(authentication)) return "redirect:/";
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
        if (bindingResult.hasErrors()) return "account/register";

        try {
            accountService.registerCustomer(registerModel);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/Account/Login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "account/register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra: " + e.getMessage());
            return "account/register";
        }
    }

    // ─── Thông tin cá nhân ───────────────────────────────────────────────────
    @GetMapping("/Profile")
    public String profile(Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) return "redirect:/Account/Login";
        String username = authentication.getName();
        ProfileDto profile = accountService.getProfile(username);
        String userFullName = dashboardService.getUserFullName(username);
        model.addAttribute("profile", profile);
        model.addAttribute("userFullName", userFullName);
        return "account/profile";
    }

    @PostMapping("/Profile")
    public String updateProfile(@RequestParam("hoTen") String hoTen,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "diaChi", required = false) String diaChi,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (!isAuthenticated(authentication)) return "redirect:/Account/Login";
        try {
            accountService.updateProfile(authentication.getName(), hoTen, email, diaChi);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi cập nhật.");
        }
        return "redirect:/Account/Profile";
    }

    // ─── Đổi mật khẩu ────────────────────────────────────────────────────────
    @GetMapping("/ChangePassword")
    public String changePasswordPage(Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) return "redirect:/Account/Login";
        String username = authentication.getName();
        String userFullName = dashboardService.getUserFullName(username);
        model.addAttribute("userFullName", userFullName);
        if (!model.containsAttribute("changePasswordModel")) {
            model.addAttribute("changePasswordModel", new ChangePasswordModel());
        }
        return "account/change-password";
    }

    @PostMapping("/ChangePassword")
    public String processChangePassword(@Valid @ModelAttribute("changePasswordModel") ChangePasswordModel form,
                                        BindingResult bindingResult,
                                        Authentication authentication,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        if (!isAuthenticated(authentication)) return "redirect:/Account/Login";

        if (form.getNewPassword() != null && !form.getNewPassword().equals(form.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "error", "Mật khẩu xác nhận không khớp");
        }
        if (bindingResult.hasErrors()) {
            String username = authentication.getName();
            model.addAttribute("userFullName", dashboardService.getUserFullName(username));
            return "account/change-password";
        }

        try {
            accountService.changePassword(authentication.getName(), form.getCurrentPassword(), form.getNewPassword());
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "redirect:/Account/ChangePassword";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/Account/ChangePassword";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi đổi mật khẩu.");
            return "redirect:/Account/ChangePassword";
        }
    }

    // ─── Helper ──────────────────────────────────────────────────────────────
    private boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
    }
}
