package com.example.htttdl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.PhieuChuyenGiao;

@Repository
public interface PhieuChuyenGiaoRepository extends JpaRepository<PhieuChuyenGiao, Integer> {

        @Query("select p from PhieuChuyenGiao p where p.diemNhanHang.id=:diemNhanHangId and p.id in:phieuChuyenGiaoIds and p.trangThai=1")
        List<PhieuChuyenGiao> findByDiemNhanHangInList(@Param("phieuChuyenGiaoIds") List<Integer> phieuChuyenGiaoIds,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select p from PhieuChuyenGiao p where  p.id in:phieuChuyenGiaoIds")
        List<PhieuChuyenGiao> findPhieuChuyenGiaoOfDiemNhanHangInList(
                        @Param("phieuChuyenGiaoIds") List<Integer> phieuChuyenGiaoIds,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select p from PhieuChuyenGiao p where p.diemNhanHang.id=:diemNhanHangId and p.id=:phieuChuyenGiaoId")
        Optional<PhieuChuyenGiao> findByDiemNhanHangId(@Param("phieuChuyenGiaoId") Integer phieuChuyenGiaoId,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select p from DonHang p where p.phieuChuyenGiao is not null and p.trangThai.id=7 and p.phieuChuyenGiao.diemNhanHang.id in:ids  and p.diemNhanHang.id=:diemNhanHangId")
        public List<DonHang> findAllPhieuChuyenGiaoOfBuuCuc(@Param("ids") List<Integer> ids,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select p from DonHang p where p.phieuChuyenGiao is not null and p.trangThai.id=8 and p.phieuChuyenGiao.diemNhanHang.id in:ids  and p.diemNhanHang.id=:diemNhanHangId")
        public List<DonHang> findAllPhieuChuyenGiaoOfBuuCucDangChuyen(@Param("ids") List<Integer> ids,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select p from DonHang p where p.phieuChuyenGiao is not null and p.trangThai.id=8  and p.diemNhanHang.id=:diemNhanHangId")
        public List<DonHang> findAllPhieuChuyenGiaoOfBuuCucDangChuyen(
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select p from DonHang p where p.phieuChuyenGiao is not null and p.trangThai.id=7  and p.diemNhanHang.id =:diemNhanHangId ")
        public List<DonHang> findAllPhieuChuyenGiaoOfBuuCuc(
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select p from DonHang p where p.phieuChuyenGiao is not null and p.trangThai.id=7  and p.diemNhanHang.id =:diemNhanHangId and p.phieuChuyenGiao.id=:phieuChuyenGiaoId")
        public List<DonHang> getCanChuyenTiepDi(@Param("phieuChuyenGiaoId") Integer phieuChuyenGiaoId,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select count(p.id) from DonHang p where p.phieuChuyenGiao.id in:phieuChuyenGiaoIds and p.trangThai.id !=8")
        public Integer countCanNhapPhieuChuyenGiao(@Param("phieuChuyenGiaoIds") List<Integer> phieuChuyenGiaoIds);

        @Modifying
        @Query("update PhieuChuyenGiao p set p.trangThai=:trangThai where p.id=:phieuChuyenGiaoId")
        public Integer updateTrangThaiById(@Param("phieuChuyenGiaoId") Integer phieuChuyenGiaoId,
                        @Param("trangThai") Integer trangThai);

}
