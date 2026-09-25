package com.example.weblongmanloc.service;

import com.example.weblongmanloc.entity.KhachHang;
import com.example.weblongmanloc.entity.TaiKhoan;
import com.example.weblongmanloc.entity.VaiTro;
import com.example.weblongmanloc.model.RegisterModel;
import com.example.weblongmanloc.repository.KhachHangRepository;
import com.example.weblongmanloc.repository.TaiKhoanRepository;
import com.example.weblongmanloc.repository.VaiTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class AccountService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(TaiKhoanRepository taiKhoanRepository,
                          KhachHangRepository khachHangRepository,
                          VaiTroRepository vaiTroRepository,
                          PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isPhoneExists(String phone) {
        String cleanPhone = normalizePhone(phone);
        return khachHangRepository.existsBySdt(cleanPhone) || taiKhoanRepository.existsByTenDangNhap(cleanPhone);
    }

    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return khachHangRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Transactional
    public KhachHang registerCustomer(RegisterModel registerModel) {
        String cleanPhone = normalizePhone(registerModel.getPhone());
        String email = registerModel.getEmail() != null ? registerModel.getEmail().trim().toLowerCase() : null;

        if (isPhoneExists(cleanPhone)) {
            throw new IllegalArgumentException("Số điện thoại này đã được đăng ký tài khoản.");
        }

        if (email != null && !email.isEmpty() && isEmailExists(email)) {
            throw new IllegalArgumentException("Email này đã được sử dụng.");
        }

        // 1. Tạo TaiKhoan
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(cleanPhone);
        taiKhoan.setMatKhauHash(passwordEncoder.encode(registerModel.getPassword()));
        taiKhoan.setLoaiChuThe("CUSTOMER");
        taiKhoan.setTrangThai("ACTIVE");
        taiKhoan.setNgayTao(LocalDateTime.now());

        // Gán vai trò CUSTOMER
        VaiTro customerRole = vaiTroRepository.findByMaVaiTro("CUSTOMER")
            .orElseGet(() -> vaiTroRepository.save(new VaiTro("CUSTOMER", "Khách hàng", "/")));
        
        Set<VaiTro> roles = new HashSet<>();
        roles.add(customerRole);
        taiKhoan.setVaiTros(roles);

        TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

        // 2. Tạo mã KhachHang mới (Format KH-XXXXXX)
        String maKH = generateMaKH();

        // 3. Tạo KhachHang
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKH(maKH);
        khachHang.setHoTen(registerModel.getFullName().trim());
        khachHang.setSdt(cleanPhone);
        khachHang.setEmail(email);
        khachHang.setTaiKhoan(savedTaiKhoan);
        khachHang.setTrangThai("ACTIVE");
        khachHang.setNgayTao(LocalDateTime.now());

        return khachHangRepository.save(khachHang);
    }

    private String generateMaKH() {
        long count = khachHangRepository.countAllKhachHang();
        long nextId = count + 1;
        String candidate;
        do {
            candidate = String.format("KH-%06d", nextId);
            nextId++;
        } while (khachHangRepository.existsById(candidate));
        return candidate;
    }

    private String normalizePhone(String phone) {
        if (phone == null) return "";
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("84") && cleaned.length() == 11) {
            cleaned = "0" + cleaned.substring(2);
        }
        return cleaned;
    }
}
