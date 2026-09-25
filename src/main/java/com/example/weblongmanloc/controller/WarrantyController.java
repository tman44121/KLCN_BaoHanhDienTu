package com.example.weblongmanloc.controller;

import com.example.weblongmanloc.dto.WarrantyHistoryDto;
import com.example.weblongmanloc.model.WarrantyRequestModel;
import com.example.weblongmanloc.service.DashboardService;
import com.example.weblongmanloc.service.WarrantyService;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Warranty")
public class WarrantyController {

    private final DashboardService dashboardService;
    private final WarrantyService warrantyService;

    @Autowired
    public WarrantyController(DashboardService dashboardService, WarrantyService warrantyService) {
        this.dashboardService = dashboardService;
        this.warrantyService = warrantyService;
    }

    // ─── Đăng ký bảo hành trực tuyến ─────────────────────────────────────────
    @GetMapping("/Register")
    public String showRegisterPage(Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) return "redirect:/Account/Login?next=/Warranty/Register";

        String username = authentication.getName();
        String userFullName = dashboardService.getUserFullName(username);
        model.addAttribute("userFullName", userFullName);

        // Tiền điền thông tin từ profile
        var profile = dashboardService.getCustomerProfile(username);
        model.addAttribute("profile", profile);

        // Load danh mục
        model.addAttribute("nhomList", warrantyService.getNhomThietBi());
        model.addAttribute("tramList", warrantyService.getTramDichVu());

        if (!model.containsAttribute("warrantyRequest")) {
            var form = new WarrantyRequestModel();
            if (profile != null) {
                form.setHoTenKhach(profile.getOrDefault("hoTen", ""));
                form.setSdtKhach(profile.getOrDefault("sdt", ""));
                form.setEmailKhach(profile.getOrDefault("email", ""));
            }
            model.addAttribute("warrantyRequest", form);
        }

        return "warranty/register";
    }

    @PostMapping("/Register")
    public String processWarrantyRegister(@Valid @ModelAttribute("warrantyRequest") WarrantyRequestModel form,
                                          BindingResult bindingResult,
                                          Authentication authentication,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        if (!isAuthenticated(authentication)) return "redirect:/Account/Login";

        String username = authentication.getName();
        String userFullName = dashboardService.getUserFullName(username);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userFullName", userFullName);
            model.addAttribute("nhomList", warrantyService.getNhomThietBi());
            model.addAttribute("tramList", warrantyService.getTramDichVu());
            model.addAttribute("profile", dashboardService.getCustomerProfile(username));
            return "warranty/register";
        }

        try {
            String maYC = warrantyService.submitWarrantyRequest(form);
            redirectAttributes.addFlashAttribute("successMessage",
                "Đăng ký bảo hành thành công! Mã yêu cầu của bạn: " + maYC +
                ". Nhân viên sẽ liên hệ xác nhận lịch hẹn trong 24 giờ.");
            return "redirect:/Warranty/Register?success=true";
        } catch (Exception e) {
            model.addAttribute("userFullName", userFullName);
            model.addAttribute("nhomList", warrantyService.getNhomThietBi());
            model.addAttribute("tramList", warrantyService.getTramDichVu());
            model.addAttribute("profile", dashboardService.getCustomerProfile(username));
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra: " + e.getMessage());
            return "warranty/register";
        }
    }

    // ─── Lịch sử bảo hành ────────────────────────────────────────────────────
    @GetMapping("/History")
    public String warrantyHistory(Model model, Authentication authentication) {
        if (!isAuthenticated(authentication)) return "redirect:/Account/Login?next=/Warranty/History";

        String username = authentication.getName();
        String userFullName = dashboardService.getUserFullName(username);
        model.addAttribute("userFullName", userFullName);

        List<WarrantyHistoryDto> history = warrantyService.getWarrantyHistory(username);
        List<Map<String, String>> onlineRequests = warrantyService.getOnlineRequests(username);

        model.addAttribute("history", history);
        model.addAttribute("onlineRequests", onlineRequests);
        model.addAttribute("totalPhieu", history.size());
        model.addAttribute("totalOnline", onlineRequests.size());

        return "warranty/history";
    }

    // ─── Helper ──────────────────────────────────────────────────────────────
    private boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
    }
}
