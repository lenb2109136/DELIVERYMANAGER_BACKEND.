package com.example.htttdl.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.beust.jcommander.Parameter;
import com.example.htttdl.DTO.Customer.Admin.DiemNhanHangDTO;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.modal.PhanCong;
import com.example.htttdl.modal.TrangThai;

public interface OrderRepository extends JpaRepository<DonHang, Integer>, JpaSpecificationExecutor<DonHang> {

        // ==================SHIPPER=================
        @Query("SELECT pc.order FROM   PhanCong pc  WHERE pc.nhanVien.id=:nhanVienId AND pc.loaiPhanCong=0 AND pc.trangThai=0 AND pc.order.trangThai.id=:trangThaiId")
        public List<DonHang> getAllOrderOfShipper(@Param("nhanVienId") Integer nhanVienId,
                        @Param("trangThaiId") Integer trangThaiId);

        @Query("SELECT pc.order FROM  PhanCong pc  WHERE pc.nhanVien.id=:nhanVienId AND pc.order.trangThai.id<:trangThaiId and pc.order.id=:orderId")
        public DonHang getOrderByIdOfShipper(@Param("nhanVienId") Integer nhanVienId,
                        @Param("orderId") Integer orderId, @Param("trangThaiId") Integer trangThaiId);

        @Query("SELECT pc.order FROM  PhanCong pc  WHERE pc.nhanVien.id=:nhanVienId and pc.order.id=:orderId")
        public DonHang getOrderByIdOfShipper(@Param("nhanVienId") Integer nhanVienId,
                        @Param("orderId") Integer orderId);

        // =========================================================================================

        @Modifying
        @Query("update DonHang p set p.trangThai.id=6 where p.id in :ids")
        public void updateOrderShipping(@Param("ids") List<Integer> orderIds);

        @Query("select p.id from DonHang p where p.id in :ids and p.diemNhanHang.id=:diemNhanHangId  and p.trangThai.id=5")
        public List<Integer> getAllOrderOfBuuCucCanShiping(@Param("diemNhanHangId") Integer diemNhanHangId,
                        @Param("ids") List<Integer> ids);

        @Query("select p from NHANVIEN p where p.loainhanvien.id=2 and p.diemNhanHang.id=:diemNhanHangId")
        public List<NHANVIEN> getAllShiperOfBuuCuc(@Param("diemNhanHangId") Integer diemNhanHangId);

        @Modifying
        @Query("UPDATE DonHang d set d.trangThai=:trangThai where d.id=:orderId and d.khachHang.id =:customerId and (d.trangThai.id=1 or d.trangThai.id=3) ")
        public Integer cancelOrder(@Param("orderId") Integer orderId, @Param("trangThai") TrangThai trangThai,
                        @Param("customerId") Integer customerId);

        @Modifying
        @Query("UPDATE DonHang d set d.trangThai=:trangThai where d.id=:orderId and d.diemNhanHang.id =:diemNhanHangId and (d.trangThai.id=3 or d.trangThai.id=1) ")
        public Integer cancelOrderOfBuuCuc(@Param("orderId") Integer orderId, @Param("trangThai") TrangThai trangThai,
                        @Param("diemNhanHangId") Integer buuCucId);

        @Query("SELECT d FROM DonHang d WHERE d.id = :orderId AND d.khachHang.id = :customerId")
        Optional<DonHang> getOrderInfo(@Param("orderId") Integer orderId, @Param("customerId") Integer customerId);

        @Query("SELECT p.id from DonHang p where p.id=:orderId and p.khachHang.id=:customerId")
        public Optional<Integer> getOrderIsUpdate(@Param("orderId") Integer orderId,
                        @Param("customerId") Integer customerId);

        @Query("select p from DonHang p where p.diemNhanHang.id=:buucucId and p.trangThai.id=5")
        public List<DonHang> getAllOrderOfBuuCuc(@Param("buucucId") Integer buuCucId);

        @Query("select p from DonHang p where p in:ids and  p.diemNhanHang.id=:buucucId and p.trangThai.id=5")
        public List<DonHang> getAllOrderOfBuuCucAndInList(@Param("buucucId") Integer buuCucId,
                        @Param("ids") List<DonHang> ids);

        @Query("select p from DonHang p where p in:ids and  p.diemNhanHang.id=:buucucId and p.trangThai.id=5")
        public List<DonHang> getAllOrderOfBuuCucAndInLists(@Param("buucucId") Integer buuCucId,
                        @Param("ids") List<Integer> ids);

        // Object orderId,Object diemNhanHangId, Object diChiChiTiet, Object
        // khoangCachDuTinh,
        // Object khoangCachNeuChuyenTiep
        @Query(value = """
                            SELECT
                                ttdh.DH_ID AS orderId,
                                dhn.DNH_ID AS diemNhanHangId,
                                dhn.DNH_DIACHICHITIET AS diaChiChiTiet,
                                ttdh.DH_KHOANGCACHDUTINH AS khoangCachDuTinh,
                                (ST_Distance_Sphere(
                                    POINT(dhn.DNH_KINHDO, dhn.DNH_VIDO),
                                    POINT(ttdh.DNH_KINHDO, ttdh.DNH_VIDO)
                                ) / 1000 ) AS khoangCachDuTinhNeChuyenTiep
                            FROM diemnhanhang dhn
                            CROSS JOIN (
                                SELECT
                                    d.DH_ID,
                                    d.DH_KHOANGCACHDUTINH,
                                    d.DH_KINHDO,
                                    d.DH_VIDO,
                                    dh.DNH_KINHDO,
                                    dh.DNH_VIDO,
                                    dh.DNH_DIACHICHITIET,
                                    dh.DNH_ID,
                                    d.PCG_ID
                                FROM donhang d
                                JOIN diemnhanhang dh ON dh.DNH_ID = d.DNH_ID
                            ) ttdh
                            WHERE ttdh.PCG_ID IS NULL
                                AND (ST_Distance_Sphere(
                                    POINT(dhn.DNH_KINHDO, dhn.DNH_VIDO),
                                    POINT(ttdh.DH_KINHDO, ttdh.DH_VIDO)
                                ) / 1000 ) < ttdh.DH_KHOANGCACHDUTINH
                                AND dhn.DNH_ID != ttdh.DNH_ID
                            ORDER BY ttdh.DH_ID ASC
                        """, nativeQuery = true)
        List<DiemNhanHangDTO> findDiemNhanHangGan(@Param("buuCucId") Integer buuCucId);

        @Query("SELECT p FROM DonHang p WHERE   p.diemNhanHang.id = :diemNhanHangId AND p.trangThai.id=5 ORDER BY p.id")
        List<DonHang> getTopOrderByBuuCuc(@Param("diemNhanHangId") Integer diemNhanHangId, Pageable pageable);

        @Query("""
                            SELECT p FROM DonHang p
                            WHERE p.phieuChuyenGiao IS NULL
                            AND p.diemNhanHang.id = :diemNhanHangId
                            AND p.trangThai.id = 1
                            AND NOT EXISTS (
                                SELECT pc FROM PhanCong pc WHERE pc.order = p AND pc.trangThai <> 1
                            )
                            ORDER BY p.id
                        """)
        List<DonHang> getTopOrderLayHangByBuuCuc(@Param("diemNhanHangId") Integer diemNhanHangId, Pageable pageable);

        @Query("select p.id from DonHang p where p.id in :ids and p.diemNhanHang.id=:diemNhanHangId and p.phieuChuyenGiao is NULL and p.trangThai.id=1 AND NOT EXISTS (\r\n"
                        + //
                        "                    SELECT pc FROM PhanCong pc WHERE pc.order = p AND pc.trangThai <> 1\r\n" + //
                        "                )")
        public List<Integer> getAllOrderOfBuuCucCanLayHang(@Param("diemNhanHangId") Integer diemNhanHangId,
                        @Param("ids") List<Integer> ids);

        @Query("SELECT p from NHANVIEN p where p.id in :ids and p.diemNhanHang.id=:diemNhanHangId and p.loainhanvien.id=2")
        List<NHANVIEN> getAllShiperInListAndOfBuuCuc(@Param("ids") List<Integer> ids,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("SELECT p from DonHang p where p.id in :ids and p.diemNhanHang.id=:diemNhanHangId ")
        List<DonHang> ggetAllOrderInListAndOfBuuCuc(@Param("ids") List<Integer> ids,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("SELECT p from DonHang p where p.id in :ids and p.khachHang.id=:khachHangId ")
        List<DonHang> getAllOrderInListAndOfKhachHang(@Param("ids") List<Integer> ids,
                        @Param("khachHangId") Integer khachHangId);

        @Query("SELECT p from DonHang p where p.phieuChuyenGiao is NULL and p.diemNhanHang.id=:diemNhanHangId")
        List<DonHang> getAll(@Param("diemNhanHangId") Integer diemNhanHangId);

        @Query("select count(p.id) from DonHang p where p.id in :ids and p.diemNhanHang.id=:diemNhanHangId")
        public Integer checkOrderOfBuuCuc(@Param("ids") List<Integer> ids,
                        @Param("diemNhanHangId") Integer diemNhanHangId);

        @Modifying
        @Query("update DonHang p set p.trangThai.id=11 where p.id in:ids and ( p.trangThai.id=3 or p.trangThai.id=1)")
        public void cancelListOrder(@Param("ids") List<Integer> ids);

        @Modifying
        @Query("UPDATE PhanCong p SET p.trangThai = 3 WHERE p.order.id IN :ids AND p.trangThai = 0")
        void mergePhanCongWhenCancel(@Param("ids") List<Integer> ids);

        @Query("select p from DonHang p where p.id in :ids and p.diemNhanHang.id=:diemNhanHangId and p.trangThai.id=4")
        public List<DonHang> getOrderInListAndCanNext(@Param("ids") List<Integer> orderids,
                        @Param("diemNhanHangId") Integer buuCucId);

        @Modifying
        @Query("update DonHang p set p.trangThai.id=:trangThaiId where p.id in:orderIds")
        void updateStatusOrderById(@Param("trangThaiId") Integer trangThaiId,
                        @Param("orderIds") List<Integer> orderIds);

        ///////////////////////////////////////////////////////////////////
        @Query(value = "SELECT p.order " +
                        "FROM ThoiDiemTrangThai t " +
                        "JOIN PhanCong p ON p.order.id = t.donHang.id " +
                        "WHERE p.nhanVien.id = :nhanVienId " +
                        "AND p.trangThai = 2 " +
                        "AND t.trangThai.id = 4 AND p.loaiPhanCong=1 " +
                        "AND FUNCTION('DATE', t.thoiDiem) = :ngay")
        List<DonHang> getAllOrderOfShipperByStateSuccess(@Param("nhanVienId") Integer nhanVienId,
                        @Param("ngay") LocalDate ngay);

        @Query(value = "SELECT p.order " +
                        "FROM ThoiDiemTrangThai t " +
                        "JOIN PhanCong p ON p.order.id = t.donHang.id " +
                        "WHERE p.nhanVien.id = :nhanVienId " +
                        "AND p.trangThai = 2 " +
                        "AND t.trangThai.id = 9 AND p.loaiPhanCong=1 " +
                        "AND FUNCTION('DATE', t.thoiDiem) = :ngay")
        List<DonHang> getAllOrderOfShipperByStateSuccesssend(@Param("nhanVienId") Integer nhanVienId,
                        @Param("ngay") LocalDate ngay);

        @Query("SELECT pc.order FROM   PhanCong pc  WHERE pc.nhanVien.id=:nhanVienId AND pc.loaiPhanCong=1 AND pc.trangThai=0 AND pc.order.trangThai.id=:trangThaiId")
        public List<DonHang> getAllOrderOfShipperSend(@Param("nhanVienId") Integer nhanVienId,
                        @Param("trangThaiId") Integer trangThaiId);

}
