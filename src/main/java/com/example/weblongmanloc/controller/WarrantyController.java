package com.example.weblongmanloc.controller;

import com.example.weblongmanloc.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Warranty")
public class WarrantyController {

    private final DashboardService dashboardService;

    @Autowired
    public WarrantyController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/Register")
    public String showRegisterPage(Model model, Authentication authentication) {
        String username = (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser"))
                ? authentication.getName()
                : null;

        String userFullName = dashboardService.getUserFullName(username);
        model.addAttribute("userFullName", userFullName);
        return "warranty/register";
    }

    @PostMapping("/Register")
    public String processWarrantyRegister(@RequestParam("serialNumber") String serialNumber,
                                          @RequestParam(value = "productName", required = false) String productName,
                                          @RequestParam(value = "purchaseDate", required = false) String purchaseDate,
                                          @RequestParam(value = "dealerName", required = false) String dealerName,
                                          RedirectAttributes redirectAttributes) {
        // Mock processing or save to DB if needed
        redirectAttributes.addFlashAttribute("successMessage", "Đăng ký bảo hành sản phẩm thành công! Mã Serial: " + serialNumber);
        return "redirect:/Warranty/Register?success=true";
    }
}
