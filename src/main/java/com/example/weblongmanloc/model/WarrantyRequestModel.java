package com.example.weblongmanloc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class WarrantyRequestModel {

    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(max = 100, message = "Họ tên không quá 100 ký tự")
    private String hoTenKhach;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại không hợp lệ (bắt đầu bằng 0, 10-11 chữ số)")
    private String sdtKhach;

    private String emailKhach;
    private String diaChiKhach;

    @NotBlank(message = "Vui lòng chọn nhóm thiết bị")
    private String maNhom;

    @NotBlank(message = "Vui lòng chọn loại thiết bị")
    private String maLoai;

    @NotBlank(message = "Vui lòng nhập hãng và tên model thiết bị")
    @Size(max = 150, message = "Hãng/model không quá 150 ký tự")
    private String hangModel;

    // IMEI hoặc SERIAL
    private String loaiDinhDanh = "SERIAL";

    @NotBlank(message = "Vui lòng nhập số Serial / IMEI")
    @Size(max = 50, message = "Số Serial không quá 50 ký tự")
    private String soSerialImei;

    @NotBlank(message = "Vui lòng mô tả lỗi hoặc yêu cầu")
    private String moTaLoi;

    @NotBlank(message = "Vui lòng chọn trạm dịch vụ")
    private String maTramMongMuon;

    private String thoiGianMongMuonTu;
    private String thoiGianMongMuonDen;

    public String getHoTenKhach() { return hoTenKhach; }
    public void setHoTenKhach(String hoTenKhach) { this.hoTenKhach = hoTenKhach; }
    public String getSdtKhach() { return sdtKhach; }
    public void setSdtKhach(String sdtKhach) { this.sdtKhach = sdtKhach; }
    public String getEmailKhach() { return emailKhach; }
    public void setEmailKhach(String emailKhach) { this.emailKhach = emailKhach; }
    public String getDiaChiKhach() { return diaChiKhach; }
    public void setDiaChiKhach(String diaChiKhach) { this.diaChiKhach = diaChiKhach; }
    public String getMaNhom() { return maNhom; }
    public void setMaNhom(String maNhom) { this.maNhom = maNhom; }
    public String getMaLoai() { return maLoai; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }
    public String getHangModel() { return hangModel; }
    public void setHangModel(String hangModel) { this.hangModel = hangModel; }
    public String getLoaiDinhDanh() { return loaiDinhDanh; }
    public void setLoaiDinhDanh(String loaiDinhDanh) { this.loaiDinhDanh = loaiDinhDanh; }
    public String getSoSerialImei() { return soSerialImei; }
    public void setSoSerialImei(String soSerialImei) { this.soSerialImei = soSerialImei; }
    public String getMoTaLoi() { return moTaLoi; }
    public void setMoTaLoi(String moTaLoi) { this.moTaLoi = moTaLoi; }
    public String getMaTramMongMuon() { return maTramMongMuon; }
    public void setMaTramMongMuon(String maTramMongMuon) { this.maTramMongMuon = maTramMongMuon; }
    public String getThoiGianMongMuonTu() { return thoiGianMongMuonTu; }
    public void setThoiGianMongMuonTu(String thoiGianMongMuonTu) { this.thoiGianMongMuonTu = thoiGianMongMuonTu; }
    public String getThoiGianMongMuonDen() { return thoiGianMongMuonDen; }
    public void setThoiGianMongMuonDen(String thoiGianMongMuonDen) { this.thoiGianMongMuonDen = thoiGianMongMuonDen; }
}
