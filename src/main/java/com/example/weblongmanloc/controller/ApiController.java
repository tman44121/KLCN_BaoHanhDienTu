package com.example.weblongmanloc.controller;

import com.example.weblongmanloc.service.WarrantyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final WarrantyService warrantyService;

    @Autowired
    public ApiController(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    /**
     * API: Lấy danh sách loại thiết bị theo nhóm (dùng cho AJAX trên form đăng ký bảo hành)
     * GET /api/loai-thiet-bi?maNhom=PHONE
     */
    @GetMapping("/loai-thiet-bi")
    public List<Map<String, String>> getLoaiThietBi(@RequestParam("maNhom") String maNhom) {
        return warrantyService.getLoaiThietBiByNhom(maNhom);
    }
}
