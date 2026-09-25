package com.example.weblongmanloc.repository;

import com.example.weblongmanloc.entity.KhachHang;
import com.example.weblongmanloc.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {

    boolean existsBySdt(String sdt);

    boolean existsByEmail(String email);

    Optional<KhachHang> findBySdt(String sdt);

    Optional<KhachHang> findByEmail(String email);

    Optional<KhachHang> findByTaiKhoan(TaiKhoan taiKhoan);

    @Query(value = "SELECT COUNT(*) FROM KhachHang", nativeQuery = true)
    long countAllKhachHang();
}
