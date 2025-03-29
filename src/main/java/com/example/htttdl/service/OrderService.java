package com.example.htttdl.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.hibernate.boot.beanvalidation.IntegrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.htttdl.DTO.Customer.Admin.DiemNhanHangDTO;
import com.example.htttdl.modal.DiemNhanHang;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.HinhThucVanChuyen;
import com.example.htttdl.modal.KhachHang;
import com.example.htttdl.modal.LoaiHang;
import com.example.htttdl.modal.PhieuChuyenGiao;
import com.example.htttdl.modal.ThoiDiemTrangThai;
import com.example.htttdl.modal.TrangThai;
import com.example.htttdl.modal.Xa;
import com.example.htttdl.repository.DiemNhanHangRepository;
import com.example.htttdl.repository.DonGiaRepository;
import com.example.htttdl.repository.HinhThucVanChuyenRepository;
import com.example.htttdl.repository.LoaiHangRepository;
import com.example.htttdl.repository.OrderRepository;
import com.example.htttdl.repository.PhieuChuyenGiaoRepository;
import com.example.htttdl.repository.ThoiDiemTrangThaiRepository;
import com.example.htttdl.repository.XaRepository;
import com.example.htttdl.repository.Specify.OrderSpecify;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;

@Service
public class OrderService {
    @Autowired
    DiemNhanHangRepository diemNhanHangRepo;

    @Autowired
    DonGiaRepository donGiaRepo;

    @Autowired
    XaRepository xaRepo;

    @Autowired
    HinhThucVanChuyenRepository hinhThucVanChuyenRepo;

    @Autowired
    LoaiHangRepository loaiHangRepo;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    PhieuChuyenGiaoRepository phieuChuyenGiaoRepo;

    @Autowired
    ThoiDiemTrangThaiRepository thoiDiemTrangThaiRepo;

    public List<DiemNhanHang> getDiemNhanHangNear(Double lat, Double longti) {
        return diemNhanHangRepo.findTop2Nearest(lat, longti);
    }

    public List<DonHang> getDonHangInlistAndOfBuuCucAndChuaGiao(Integer diemNhanHangId, List<Integer> orderIds) {
        return orderRepo.findAll();
    }

    public void getBaseAddressLayNhan(DonHang order) {
        // Xa xa = xaRepo.findById(order.getXa().getId()).orElse(null);
        Float space = -1.0f;

        List<DiemNhanHang> diemNhanHangs = getDiemNhanHangNear((double) order.getLatSend(),
                (double) order.getLongSend());
        if (diemNhanHangs.size() > 0) {
            DiemNhanHang diemNhanHang = diemNhanHangs.get(0);
            order.setDiemNhanHang(diemNhanHang);
            space += (float) diemNhanHang.haversineDistance(order.getViDo(), order.getKinhDo());
            System.out.println("ĐIỂM NHAN HANG DAU TIEN LA " + diemNhanHang.getId());
            order.setKhoangCachDuTinh(space);
        } else {
            throw new IntegrationException("Xã không hợp lệ", null);
        }
    }

    public void getBaseAddressBuuCuc(DonHang order) {
        Float space = -1.0f;
        Xa xa = xaRepo.findById(order.getXa().getId()).orElse(null);
        if (xa != null) {
            DiemNhanHang diemNhanHang = diemNhanHangRepo.findById(order.getDiemNhanHang().getId()).orElse(null);
            if (diemNhanHang != null) {
                space = (float) diemNhanHang.haversineDistance(xa.getViDo(), xa.getKinhDo());
                space += (float) diemNhanHang.haversineDistance(order.getViDo(), order.getKinhDo());
                System.out.println("Tọa độ lần lược là: KINH ĐỘ: " + space);
                order.setKhoangCachDuTinh(space);
            } else {
                throw new IntegrationException(null, null);
            }
            System.out.println("Tọa độ lần lược là: KINH ĐỘ: " + space);
        } else {
            throw new IntegrationException(null, null);
        }
    }

    public void checkHinhThucVanChuyen(DonHang donHang) {
        HinhThucVanChuyen hinhThucVanChuyen = hinhThucVanChuyenRepo.findById(donHang.getHinhThucVanChuyen().getId())
                .orElse(null);
        LoaiHang loaiHang = loaiHangRepo.findById(donHang.getLoaiHang().getId()).orElse(null);
        if (hinhThucVanChuyen == null || loaiHang == null) {
            throw new IntegrationException("null");
        }
    }

    public void caculateFee(DonHang order) {
        Double a = donGiaRepo.getPrice(order.getLoaiHang().getId(), order.getHinhThucVanChuyen().getId(),
                order.getTrongLuong(), order.getKhoangCachDuTinh());
        System.out.println("LAY FEE LA: " + a);
        order.setFee(a);
    }

    public void saveOrder(DonHang order, Integer status) {
        orderRepo.saveAndFlush(order);
        ThoiDiemTrangThai thoiDiemTrangThai = new ThoiDiemTrangThai();
        thoiDiemTrangThai.setDonHang(order);
        thoiDiemTrangThai.setNhanvien(null);
        thoiDiemTrangThai.setTrangThai(new TrangThai(status));
        order.setTrangThai(new TrangThai(status));
        thoiDiemTrangThai.setThoiDiem(LocalDateTime.now());
        thoiDiemTrangThaiRepo.save(thoiDiemTrangThai);
    }

    public Page<DonHang> getAll(Integer id, Integer trangThaiId, String tenNguoiNhan, KhachHang k, String sortBy,
            Integer page, Integer size) {
        Pageable pageable;
        Sort sort = null;
        if (sortBy == null) {
            pageable = PageRequest.of(page, size);
        } else {

            switch (sortBy.toLowerCase()) {
                case "fee":
                    sort = Sort.by(Sort.Direction.DESC, "fee");
                    break;
                case "khoangcach":
                    sort = Sort.by(Sort.Direction.DESC, "KhoangCachDuTinh");
                    break;
                default:
                    pageable = PageRequest.of(page, size);
            }
            pageable = PageRequest.of(page, size, sort);

        }
        Specification<DonHang> spec = OrderSpecify.filterByCriteria(id, new TrangThai(trangThaiId), tenNguoiNhan,
                k);
        return orderRepo.findAll(spec, pageable);
    }

    public Page<DonHang> getAllAdmin(Integer id, Integer trangThaiId, String tenNguoiNhan, KhachHang k, String sortBy,
            Integer page, Integer size, Integer diemNhanHangId) {
        Pageable pageable;
        Sort sort = null;
        if (sortBy == null) {
            pageable = PageRequest.of(page, size);
        } else {

            switch (sortBy.toLowerCase()) {
                case "fee":
                    sort = Sort.by(Sort.Direction.DESC, "fee");
                    break;
                case "khoangcach":
                    sort = Sort.by(Sort.Direction.DESC, "KhoangCachDuTinh");
                    break;
                default:
                    pageable = PageRequest.of(page, size);
            }
            pageable = PageRequest.of(page, size, sort);

        }
        Specification<DonHang> spec = OrderSpecify.filterByCriteriaAdmin(id, new TrangThai(trangThaiId), tenNguoiNhan,
                k, diemNhanHangId, null);
        return orderRepo.findAll(spec, pageable);
    }

    public Boolean cancelOrder(Integer orderId, TrangThai trangThai, Integer customerId) {
        return orderRepo.cancelOrder(orderId, trangThai, customerId) > 0;
    }

    public Boolean cancelOrderOfBuuCuc(Integer orderId, TrangThai trangThai, Integer buuCucId) {
        return orderRepo.cancelOrderOfBuuCuc(orderId, trangThai, buuCucId) > 0;
    }

    public void saveOrderThoiDiemTrangThai(ThoiDiemTrangThai thoiDiemTrangThai) {
        thoiDiemTrangThaiRepo.save(thoiDiemTrangThai);
    }

    public DonHang getInfo(Integer orderId, Integer customerId) {
        return orderRepo.getOrderInfo(orderId, customerId).orElse(null);
    }

    public Boolean checkOrderCanUpdate(Integer orderId, Integer customerId) {
        return orderRepo.getOrderIsUpdate(orderId, customerId).orElse(null) != null;

    }

    public List<DonHang> getAllOrderOfBuuCuc(Integer buuCucId) {
        return orderRepo.getAllOrderOfBuuCuc(buuCucId);
    }

    public List<DiemNhanHangDTO> getAllDiemNhanHangHopLy(Integer buuCucId) {
        return orderRepo.findDiemNhanHangGan(buuCucId);
    }

    public DonHang getOrderById(Integer orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    public List<DonHang> getOrderInList(List<Integer> orderIds, Integer buuCucId) {
        return orderRepo.getOrderInListAndCanNext(orderIds, buuCucId);
    }

    public void transfomAuto(List<DonHang> orders, List<PhieuChuyenGiao> phieuChuyenGiao) {
        phieuChuyenGiaoRepo.saveAll(phieuChuyenGiao);
        orderRepo.saveAll(orders);
    }

    public void chuyenTiepSingle(PhieuChuyenGiao phieuChuyenGiao, DonHang order) {
        phieuChuyenGiaoRepo.save(phieuChuyenGiao);
        orderRepo.save(order);
    }

}