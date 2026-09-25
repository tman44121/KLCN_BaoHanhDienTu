package com.example.weblongmanloc.dto;

public class ProfileDto {
    private String maKH;
    private String hoTen;
    private String sdt;
    private String email;
    private String diaChi;
    private String tenDangNhap;
    private String trangThai;
    private String ngayTao;

    public ProfileDto() {}

    public ProfileDto(String maKH, String hoTen, String sdt, String email, String diaChi,
                      String tenDangNhap, String trangThai, String ngayTao) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
        this.diaChi = diaChi;
        this.tenDangNhap = tenDangNhap;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getNgayTao() { return ngayTao; }
    public void setNgayTao(String ngayTao) { this.ngayTao = ngayTao; }
}
