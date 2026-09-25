package com.example.weblongmanloc.service;

import com.example.weblongmanloc.dto.ProfileDto;
import com.example.weblongmanloc.entity.KhachHang;
import com.example.weblongmanloc.entity.TaiKhoan;
import com.example.weblongmanloc.entity.VaiTro;
import com.example.weblongmanloc.model.RegisterModel;
import com.example.weblongmanloc.repository.KhachHangRepository;
import com.example.weblongmanloc.repository.TaiKhoanRepository;
import com.example.weblongmanloc.repository.VaiTroRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

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

    // ─────────────────────────────────────────────────────────────────────────
    // Profile: lấy thông tin cá nhân
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ProfileDto getProfile(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        try {
            String sql = "SELECT k.MaKH, k.HoTen, k.SDT, k.Email, k.DiaChi, " +
                         "t.TenDangNhap, k.TrangThai, DATE_FORMAT(k.NgayTao, '%d/%m/%Y') " +
                         "FROM KhachHang k " +
                         "LEFT JOIN TaiKhoan t ON k.MaTaiKhoan = t.MaTaiKhoan " +
                         "WHERE LOWER(t.TenDangNhap) = LOWER(:user) OR k.SDT = :user " +
                         "   OR LOWER(k.Email) = LOWER(:user) LIMIT 1";
            List<Object[]> rows = entityManager.createNativeQuery(sql)
                                               .setParameter("user", username.trim())
                                               .getResultList();
            if (!rows.isEmpty()) {
                Object[] r = rows.get(0);
                return new ProfileDto(
                    str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5]), str(r[6]), str(r[7])
                );
            }
        } catch (Exception ignored) {}
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Profile: cập nhật thông tin cá nhân
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public void updateProfile(String username, String hoTen, String email, String diaChi) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhapIgnoreCase(username);
        if (taiKhoanOpt.isEmpty()) throw new IllegalArgumentException("Không tìm thấy tài khoản.");

        TaiKhoan tk = taiKhoanOpt.get();
        // Tìm KhachHang
        Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(tk);
        if (khOpt.isEmpty()) throw new IllegalArgumentException("Không tìm thấy thông tin khách hàng.");

        KhachHang kh = khOpt.get();
        if (hoTen != null && !hoTen.trim().isEmpty()) kh.setHoTen(hoTen.trim());
        if (email != null && !email.trim().isEmpty()) {
            String newEmail = email.trim().toLowerCase();
            if (!newEmail.equals(kh.getEmail()) && khachHangRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email này đã được sử dụng bởi tài khoản khác.");
            }
            kh.setEmail(newEmail);
        } else {
            kh.setEmail(null);
        }
        kh.setDiaChi(diaChi != null ? diaChi.trim() : null);
        khachHangRepository.save(kh);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Đổi mật khẩu
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhapIgnoreCase(username);
        if (taiKhoanOpt.isEmpty()) throw new IllegalArgumentException("Không tìm thấy tài khoản.");

        TaiKhoan tk = taiKhoanOpt.get();
        if (!passwordEncoder.matches(currentPassword, tk.getMatKhauHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }
        tk.setMatKhauHash(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(tk);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
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

    private String str(Object o) {
        return o != null ? o.toString() : null;
    }
}
