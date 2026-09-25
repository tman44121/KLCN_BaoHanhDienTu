package com.example.weblongmanloc.service;

import com.example.weblongmanloc.dto.WarrantyHistoryDto;
import com.example.weblongmanloc.model.WarrantyRequestModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class WarrantyService {

    @PersistenceContext
    private EntityManager entityManager;

    // ─────────────────────────────────────────────────────────────────────────
    // Lấy danh sách nhóm thiết bị
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Map<String, String>> getNhomThietBi() {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            String sql = "SELECT MaNhom, TenNhom FROM NhomThietBi WHERE HoatDong = 1 ORDER BY ThuTu, TenNhom";
            List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
            for (Object[] r : rows) {
                Map<String, String> m = new HashMap<>();
                m.put("ma", r[0] != null ? r[0].toString() : "");
                m.put("ten", r[1] != null ? r[1].toString() : "");
                list.add(m);
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lấy danh sách loại thiết bị theo nhóm
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Map<String, String>> getLoaiThietBiByNhom(String maNhom) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            String sql = "SELECT MaLoai, TenLoai, LoaiDinhDanh FROM LoaiThietBi " +
                         "WHERE MaNhom = :maNhom AND HoatDong = 1 ORDER BY TenLoai";
            List<Object[]> rows = entityManager.createNativeQuery(sql)
                                               .setParameter("maNhom", maNhom)
                                               .getResultList();
            for (Object[] r : rows) {
                Map<String, String> m = new HashMap<>();
                m.put("ma", r[0] != null ? r[0].toString() : "");
                m.put("ten", r[1] != null ? r[1].toString() : "");
                m.put("loaiDinhDanh", r[2] != null ? r[2].toString() : "SERIAL");
                list.add(m);
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lấy danh sách trạm dịch vụ
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Map<String, String>> getTramDichVu() {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            String sql = "SELECT MaTram, TenTram, DiaChi FROM TramDichVu WHERE HoatDong = 1 ORDER BY TenTram";
            List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
            for (Object[] r : rows) {
                Map<String, String> m = new HashMap<>();
                m.put("ma", r[0] != null ? r[0].toString() : "");
                m.put("ten", r[1] != null ? r[1].toString() : "");
                m.put("diaChi", r[2] != null ? r[2].toString() : "");
                list.add(m);
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lưu yêu cầu bảo hành vào DB (bảng YeuCauBaoHanh)
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public String submitWarrantyRequest(WarrantyRequestModel model) {
        // Sinh mã YC tự động
        String maYC = generateMaYeuCau();

        String insertSql = "INSERT INTO YeuCauBaoHanh " +
            "(MaYeuCau, HoTenKhach, SDTKhach, EmailKhach, DiaChiKhach, " +
            " MaNhom, MaLoai, HangModel, LoaiDinhDanh, SoSerial_IMEI, " +
            " MoTaLoi, MaTramMongMuon, ThoiGianMongMuonTu, ThoiGianMongMuonDen, " +
            " TrangThai, NgayTao) VALUES " +
            "(:maYC, :hoTen, :sdt, :email, :diaChi, " +
            " :maNhom, :maLoai, :hangModel, :loaiDinhDanh, :soSerial, " +
            " :moTaLoi, :maTram, :thoiGianTu, :thoiGianDen, " +
            " 'PENDING_INTAKE', :ngayTao)";

        LocalDateTime thoiGianTu = null;
        LocalDateTime thoiGianDen = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        try {
            if (model.getThoiGianMongMuonTu() != null && !model.getThoiGianMongMuonTu().isEmpty()) {
                thoiGianTu = LocalDateTime.parse(model.getThoiGianMongMuonTu(), dtf);
            }
            if (model.getThoiGianMongMuonDen() != null && !model.getThoiGianMongMuonDen().isEmpty()) {
                thoiGianDen = LocalDateTime.parse(model.getThoiGianMongMuonDen(), dtf);
            }
        } catch (Exception ignored) {}

        entityManager.createNativeQuery(insertSql)
            .setParameter("maYC", maYC)
            .setParameter("hoTen", model.getHoTenKhach().trim())
            .setParameter("sdt", model.getSdtKhach().trim())
            .setParameter("email", model.getEmailKhach() != null ? model.getEmailKhach().trim() : null)
            .setParameter("diaChi", model.getDiaChiKhach() != null ? model.getDiaChiKhach().trim() : null)
            .setParameter("maNhom", model.getMaNhom())
            .setParameter("maLoai", model.getMaLoai())
            .setParameter("hangModel", model.getHangModel().trim())
            .setParameter("loaiDinhDanh", model.getLoaiDinhDanh())
            .setParameter("soSerial", model.getSoSerialImei().trim().toUpperCase())
            .setParameter("moTaLoi", model.getMoTaLoi().trim())
            .setParameter("maTram", model.getMaTramMongMuon())
            .setParameter("thoiGianTu", thoiGianTu)
            .setParameter("thoiGianDen", thoiGianDen)
            .setParameter("ngayTao", LocalDateTime.now())
            .executeUpdate();

        return maYC;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lịch sử bảo hành đầy đủ (PhieuTiepNhan) của một khách
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<WarrantyHistoryDto> getWarrantyHistory(String username) {
        List<WarrantyHistoryDto> list = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) return list;

        try {
            // Tìm MaKH
            String findKhSql = "SELECT k.MaKH FROM KhachHang k " +
                               "LEFT JOIN TaiKhoan t ON k.MaTaiKhoan = t.MaTaiKhoan " +
                               "WHERE LOWER(t.TenDangNhap) = LOWER(:user) OR k.SDT = :user " +
                               "   OR LOWER(k.Email) = LOWER(:user) LIMIT 1";
            List<Object> maKhRes = entityManager.createNativeQuery(findKhSql)
                                                .setParameter("user", username.trim())
                                                .getResultList();
            if (maKhRes.isEmpty()) return list;
            String maKH = maKhRes.get(0).toString();

            // Lấy toàn bộ phiếu của khách (không giới hạn 5)
            String sql = "SELECT p.MaPhieuTN, " +
                         "COALESCE(sp.TenSP, t.MaThietBi, 'Thiết bị điện tử') AS TenThietBi, " +
                         "COALESCE(t.SoSerial_IMEI, 'N/A') AS SoSerial, " +
                         "COALESCE(tr.TenTram, 'LML Service') AS TenTram, " +
                         "DATE_FORMAT(p.NgayTiepNhan, '%d/%m/%Y') AS NgayTiepNhanStr, " +
                         "COALESCE(DATE_FORMAT(p.NgayHenTra, '%d/%m/%Y'), 'Đang cập nhật') AS NgayHenTraStr, " +
                         "p.MoTaLoiKhachBao, p.TrangThaiXuLy, " +
                         "p.LoaiYeuCau, " +
                         "COALESCE(CAST(p.ChiPhiDuKien AS CHAR), '0') AS ChiPhiDuKien " +
                         "FROM PhieuTiepNhan p " +
                         "LEFT JOIN ThietBi t ON p.MaThietBi = t.MaThietBi " +
                         "LEFT JOIN SanPham sp ON t.MaSP = sp.MaSP " +
                         "LEFT JOIN TramDichVu tr ON p.MaTram = tr.MaTram " +
                         "WHERE p.MaKH = :maKH " +
                         "ORDER BY p.NgayTiepNhan DESC";

            List<Object[]> rows = entityManager.createNativeQuery(sql)
                                               .setParameter("maKH", maKH)
                                               .getResultList();

            for (Object[] r : rows) {
                String maPhieu = str(r[0]);
                String tenTb = str(r[1]);
                String serial = str(r[2]);
                String tram = str(r[3]);
                String ngayTN = str(r[4]);
                String ngayHT = str(r[5]);
                String moTa = str(r[6]);
                String status = str(r[7]);
                String loaiYC = str(r[8]);
                String chiPhi = str(r[9]);

                list.add(new WarrantyHistoryDto(
                    maPhieu, tenTb, serial, tram, ngayTN, ngayHT, moTa,
                    status, mapBadgeText(status), mapBadgeClass(status),
                    mapLoaiYeuCau(loaiYC), formatCurrency(chiPhi)
                ));
            }
        } catch (Exception ignored) {}

        // Nếu chưa có phiếu nào, trả về danh sách rỗng (không dùng mock data)
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lịch sử yêu cầu bảo hành trực tuyến (YeuCauBaoHanh)
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Map<String, String>> getOnlineRequests(String username) {
        List<Map<String, String>> list = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) return list;
        try {
            String sql = "SELECT y.MaYeuCau, y.HangModel, y.SoSerial_IMEI, " +
                         "tr.TenTram, DATE_FORMAT(y.NgayTao,'%d/%m/%Y %H:%i') AS NgayTao, " +
                         "y.TrangThai " +
                         "FROM YeuCauBaoHanh y " +
                         "LEFT JOIN TramDichVu tr ON y.MaTramMongMuon = tr.MaTram " +
                         "WHERE y.SDTKhach = :sdt " +
                         "ORDER BY y.NgayTao DESC LIMIT 20";

            // Lấy SDT từ username
            String sdtSql = "SELECT COALESCE(k.SDT, '') FROM KhachHang k " +
                            "LEFT JOIN TaiKhoan t ON k.MaTaiKhoan = t.MaTaiKhoan " +
                            "WHERE LOWER(t.TenDangNhap) = LOWER(:user) LIMIT 1";
            List<Object> sdtRes = entityManager.createNativeQuery(sdtSql)
                                               .setParameter("user", username.trim())
                                               .getResultList();
            String sdt = sdtRes.isEmpty() ? username : sdtRes.get(0).toString();

            List<Object[]> rows = entityManager.createNativeQuery(sql)
                                               .setParameter("sdt", sdt)
                                               .getResultList();
            for (Object[] r : rows) {
                Map<String, String> m = new HashMap<>();
                m.put("maYeuCau", str(r[0]));
                m.put("hangModel", str(r[1]));
                m.put("soSerial", str(r[2]));
                m.put("tenTram", str(r[3]));
                m.put("ngayTao", str(r[4]));
                m.put("trangThai", str(r[5]));
                m.put("badgeText", mapOnlineBadge(str(r[5])));
                m.put("badgeClass", mapOnlineBadgeClass(str(r[5])));
                list.add(m);
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private String generateMaYeuCau() {
        try {
            String countSql = "SELECT COUNT(*) FROM YeuCauBaoHanh WHERE DATE(NgayTao) = CURDATE()";
            Object count = entityManager.createNativeQuery(countSql).getSingleResult();
            long seq = count instanceof Number ? ((Number) count).longValue() + 1 : 1;
            String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            return String.format("YC-%s-%05d", today, seq);
        } catch (Exception e) {
            return "YC-" + System.currentTimeMillis();
        }
    }

    private String mapBadgeText(String status) {
        if (status == null) return "Đang xử lý";
        switch (status.toUpperCase()) {
            case "RECEIVED": return "Mới tiếp nhận";
            case "INSPECTING": return "Đang kiểm tra";
            case "DIAGNOSED": return "Đã chẩn đoán";
            case "REPAIRING": case "PROCESSING": return "Đang sửa chữa";
            case "AWAITING_PARTS": case "WAITING_PARTS": return "Chờ linh kiện";
            case "AWAITING_QUOTE_APPROVAL": return "Chờ duyệt báo giá";
            case "AWAITING_CUSTOMER_CONFIRMATION": return "Chờ xác nhận";
            case "COMPLETED": return "Sửa xong – Chờ nhận";
            case "DELIVERED": return "Đã nhận máy";
            case "CANCELLED": return "Đã huỷ";
            case "RETURNED_UNREPAIRED": return "Trả máy chưa sửa";
            default: return "Đang xử lý";
        }
    }

    private String mapBadgeClass(String status) {
        if (status == null) return "badge-processing";
        switch (status.toUpperCase()) {
            case "RECEIVED": return "badge-received";
            case "INSPECTING": case "DIAGNOSED": return "badge-inspecting";
            case "REPAIRING": case "PROCESSING": return "badge-processing";
            case "AWAITING_PARTS": case "WAITING_PARTS": return "badge-waiting";
            case "AWAITING_QUOTE_APPROVAL": case "AWAITING_CUSTOMER_CONFIRMATION": return "badge-waiting";
            case "COMPLETED": return "badge-completed";
            case "DELIVERED": return "badge-delivered";
            case "CANCELLED": case "RETURNED_UNREPAIRED": return "badge-cancelled";
            default: return "badge-processing";
        }
    }

    private String mapLoaiYeuCau(String loai) {
        if (loai == null) return "Bảo hành";
        switch (loai.toUpperCase()) {
            case "WARRANTY": return "Bảo hành";
            case "REPAIR": return "Sửa chữa";
            case "MAINTENANCE": return "Bảo trì";
            default: return loai;
        }
    }

    private String formatCurrency(String value) {
        if (value == null || value.equals("0") || value.isEmpty()) return "Miễn phí";
        try {
            long v = (long) Double.parseDouble(value);
            if (v == 0) return "Miễn phí";
            return String.format("%,d đ", v).replace(",", ".");
        } catch (Exception e) {
            return value;
        }
    }

    private String mapOnlineBadge(String status) {
        if (status == null) return "Đang chờ";
        switch (status.toUpperCase()) {
            case "PENDING_INTAKE": return "Đang chờ tiếp nhận";
            case "CONVERTED": return "Đã tạo phiếu";
            case "CANCELLED": return "Đã huỷ";
            default: return status;
        }
    }

    private String mapOnlineBadgeClass(String status) {
        if (status == null) return "badge-waiting";
        switch (status.toUpperCase()) {
            case "PENDING_INTAKE": return "badge-waiting";
            case "CONVERTED": return "badge-completed";
            case "CANCELLED": return "badge-cancelled";
            default: return "badge-processing";
        }
    }

    private String str(Object o) {
        return o != null ? o.toString() : null;
    }
}
