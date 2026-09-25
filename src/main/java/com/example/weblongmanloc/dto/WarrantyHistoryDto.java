package com.example.weblongmanloc.dto;

public class WarrantyHistoryDto {
    private String maPhieu;
    private String tenThietBi;
    private String soSerial;
    private String tenTram;
    private String ngayTiepNhan;
    private String ngayHenTra;
    private String moTaLoi;
    private String trangThai;
    private String badgeText;
    private String badgeClass;
    private String loaiYeuCau;
    private String chiPhiDuKien;

    public WarrantyHistoryDto() {}

    public WarrantyHistoryDto(String maPhieu, String tenThietBi, String soSerial, String tenTram,
                               String ngayTiepNhan, String ngayHenTra, String moTaLoi,
                               String trangThai, String badgeText, String badgeClass,
                               String loaiYeuCau, String chiPhiDuKien) {
        this.maPhieu = maPhieu;
        this.tenThietBi = tenThietBi;
        this.soSerial = soSerial;
        this.tenTram = tenTram;
        this.ngayTiepNhan = ngayTiepNhan;
        this.ngayHenTra = ngayHenTra;
        this.moTaLoi = moTaLoi;
        this.trangThai = trangThai;
        this.badgeText = badgeText;
        this.badgeClass = badgeClass;
        this.loaiYeuCau = loaiYeuCau;
        this.chiPhiDuKien = chiPhiDuKien;
    }

    public String getMaPhieu() { return maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
    public String getSoSerial() { return soSerial; }
    public void setSoSerial(String soSerial) { this.soSerial = soSerial; }
    public String getTenTram() { return tenTram; }
    public void setTenTram(String tenTram) { this.tenTram = tenTram; }
    public String getNgayTiepNhan() { return ngayTiepNhan; }
    public void setNgayTiepNhan(String ngayTiepNhan) { this.ngayTiepNhan = ngayTiepNhan; }
    public String getNgayHenTra() { return ngayHenTra; }
    public void setNgayHenTra(String ngayHenTra) { this.ngayHenTra = ngayHenTra; }
    public String getMoTaLoi() { return moTaLoi; }
    public void setMoTaLoi(String moTaLoi) { this.moTaLoi = moTaLoi; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getBadgeText() { return badgeText; }
    public void setBadgeText(String badgeText) { this.badgeText = badgeText; }
    public String getBadgeClass() { return badgeClass; }
    public void setBadgeClass(String badgeClass) { this.badgeClass = badgeClass; }
    public String getLoaiYeuCau() { return loaiYeuCau; }
    public void setLoaiYeuCau(String loaiYeuCau) { this.loaiYeuCau = loaiYeuCau; }
    public String getChiPhiDuKien() { return chiPhiDuKien; }
    public void setChiPhiDuKien(String chiPhiDuKien) { this.chiPhiDuKien = chiPhiDuKien; }
}
