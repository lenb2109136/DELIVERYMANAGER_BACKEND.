package com.example.htttdl.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.hibernate.boot.beanvalidation.IntegrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.htttdl.DTO.Customer.Admin.DiemNhanHangDTO;
import com.example.htttdl.DTO.Customer.Admin.PhanCongDTO;
import com.example.htttdl.FirebaseCrud.Notification;
import com.example.htttdl.config.AminBean;
import com.example.htttdl.config.ShipperBean;
import com.example.htttdl.modal.DiemNhanHang;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.HinhThucVanChuyen;
import com.example.htttdl.modal.KhachHang;
import com.example.htttdl.modal.LoaiHang;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.modal.PhanCong;
import com.example.htttdl.modal.PhieuChuyenGiao;
import com.example.htttdl.modal.ThoiDiemTrangThai;
import com.example.htttdl.modal.TrangThai;
import com.example.htttdl.modal.Xa;
import com.example.htttdl.repository.DiemNhanHangRepository;
import com.example.htttdl.repository.DonGiaRepository;
import com.example.htttdl.repository.HinhThucVanChuyenRepository;
import com.example.htttdl.repository.LoaiHangRepository;
import com.example.htttdl.repository.OrderRepository;
import com.example.htttdl.repository.PhanCongRepository;
import com.example.htttdl.repository.PhieuChuyenGiaoRepository;
import com.example.htttdl.repository.ThoiDiemTrangThaiRepository;
import com.example.htttdl.repository.TrangThaiRepository;
import com.example.htttdl.repository.XaRepository;
import com.example.htttdl.repository.Specify.OrderSpecify;
import com.example.htttdl.response.ObjectRespone;
import com.example.htttdl.response.response;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

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
    
    @Autowired
    PhanCongRepository phanCongRepository;
    
    @Autowired
    private TrangThaiRepository trangThaiRepository;
    
    @Autowired
    private PhanCongService phanCongService;
    
    @Autowired 
    private ThoiDiemTrangThaiRepository thoiDiemTrangThaiRepository;

    public List<DiemNhanHang> getDiemNhanHangNear(Double lat, Double longti) {
        return diemNhanHangRepo.findTop2Nearest(lat, longti);
    }

    public List<DonHang> getDonHangInlistAndOfBuuCucAndChuaGiao(Integer diemNhanHangId, List<Integer> orderIds) {
        return orderRepo.findAll();
    }

    public void getBaseAddressLayNhan(DonHang order) {
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
    
    
    // đưa về cuối -shipper
    public List<DonHang> getAllOrderChoNhan(int status, ShipperBean shipperBean) {
    	 List<DonHang> orders = new ArrayList<>();
         orders = orderRepo.getAllOrderOfShipperSend(shipperBean.getNhanVien().getId(), status);
         return orders;
    }
    
    public Map<Object, Object> loTrinh(ToaDoDTO t) throws Exception {
    	ToaDo tt = new ToaDo(0, 0);
        Map<Object, Object> map = tt.TSPKhoangCach(t);
        return map;
    }
    public void setLayThanhCongsend(int idd , ShipperBean shipperBean) {
    	NHANVIEN nv = shipperBean.getNhanVien();
        PhanCong b = phanCongRepository.getPhanCongsend(idd, nv.getId());
        b.setTrangThai(2);
        if (b == null) {
            throw new EntityNotFoundException("Bạn không có quyền trên đơn hàng này");
        }
        DonHang d = orderRepo.findById(b.getOrder().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
        TrangThai tt = trangThaiRepository.findById(9)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
        ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, b.getOrder(), tt);
        b.setTrangThai(2);
        phanCongRepository.save(b);
        d.setTrangThai(tt);
        thoiDiemTrangThaiRepository.save(t);
        orderRepo.save(d);
    }
    public void setLayThanhCong(int idd, ShipperBean shipperBean) {
    	 NHANVIEN nv = shipperBean.getNhanVien();
         PhanCong b = phanCongRepository.getPhanCong(idd, nv.getId());
         b.setTrangThai(2);
         if (b == null) {
             throw new EntityNotFoundException("Bạn không có quyền trên đơn hàng này");
         }
         DonHang d = orderRepo.findById(b.getOrder().getId())
                 .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
         TrangThai tt = trangThaiRepository.findById(4)
                 .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
         ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, b.getOrder(), tt);
         b.setTrangThai(2);
         phanCongRepository.save(b);
         d.setTrangThai(tt);
         thoiDiemTrangThaiRepository.save(t);
         orderRepo.save(d);
    }
    public void laythanhcong(int idDonHang,ShipperBean shipperBean) {
    	 DonHang d = orderRepo.findById(idDonHang)
                 .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
         NHANVIEN nv = shipperBean.getNhanVien();
         TrangThai tt = trangThaiRepository.findById(2)
                 .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
         ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, d, tt);
         thoiDiemTrangThaiRepository.save(t);
         d.setTrangThai(tt);
         orderRepo.save(d);
    }
    
    public void ChuyenDangGiaosend(int idDonHang, ShipperBean shipperBean) {
    	  DonHang d = orderRepo.findById(idDonHang)
                  .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
          NHANVIEN nv = shipperBean.getNhanVien();
          TrangThai tt = trangThaiRepository.findById(12)
                  .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
          ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, d, tt);
          thoiDiemTrangThaiRepository.save(t);
          d.setTrangThai(tt);
          orderRepo.save(d);
    }
    
    public void ChuyenDangGiao(int idDonHang, ShipperBean shipperBean) {
    	 DonHang d = orderRepo.findById(idDonHang)
                 .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
         NHANVIEN nv = shipperBean.getNhanVien();
         TrangThai tt = trangThaiRepository.findById(2)
                 .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
         ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, d, tt);
         thoiDiemTrangThaiRepository.save(t);
         d.setTrangThai(tt);
         orderRepo.save(d);
    }
    
    public DonHang getAllOrderChoLayById(int id, ShipperBean shipperBean) {
    	return orderRepo.getOrderByIdOfShipper(shipperBean.getNhanVien().getId(), id, 4);
    }
    public List<DonHang> getAllOrderChoLay(ShipperBean shipperBean, int status) {
    	return orderRepo.getAllOrderOfShipper(shipperBean.getNhanVien().getId(), status);
    }
    
    public void layHang(int id , ShipperBean shipperBean) throws Exception {
    	DonHang order = orderRepo.getOrderByIdOfShipper(shipperBean.getNhanVien().getId(), id);
        if (order != null) {
            if (order.getTrangThai().getId() == 1 || order.getTrangThai().getId() == 2) {
                ThoiDiemTrangThai thoiDiemTrangThai = new ThoiDiemTrangThai();
                thoiDiemTrangThai.setDonHang(order);
                thoiDiemTrangThai.setThoiDiem(LocalDateTime.now());
                thoiDiemTrangThai.setDiemNhanHang(order.getDiemNhanHang());
                thoiDiemTrangThai.setNhanvien(shipperBean.getNhanVien());
                thoiDiemTrangThai.setTrangThai(new TrangThai(4));
                order.setTrangThai(new TrangThai(4));
                thoiDiemTrangThaiRepo.save(thoiDiemTrangThai);
                orderRepo.save(order);
                Notification.pushNotifycationBuuCuc(
                        shipperBean.getNhanVien().getDiemNhanHang().getId(), "ORDER", "Đơn hàng số " + order.getId()
                                + " đã được shipper " + shipperBean.getNhanVien().getTen() + " lấy thành công",
                        "2025-02-03", order.getId());
               
            }
            throw new Exception("Trang thái của đơn hàng không phù hợp");
        }
        throw new Exception("Không tìm thấy đơn hàng hiện tại");
    }
    
    
    public List<DonHang> getThanhCongTrongNgay(NHANVIEN nv){
    	return orderRepo.getAllOrderOfShipperByStateSuccesssend(nv.getId(), LocalDate.now());
    }
    
    
    // PHẦN ADMIN
    @Transactional
    public void phanCong(List<PhanCongDTO> phanCongs,AminBean a) throws Exception {
    	 List<Integer> orderIds = new ArrayList<>();
         List<Integer> shipperIds = new ArrayList<>();
         if (phanCongs.size() > 0) {
             phanCongs.forEach(v -> {
                 shipperIds.add(v.getNhanVien().getId());
                 v.getOrders().forEach(vv -> {
                     orderIds.add(vv.getId());
                 });
             });
             List<ThoiDiemTrangThai> thoiDiemTrangThoi = new ArrayList<>();
             List<NHANVIEN> listNhanVien = phanCongService.checkShiper(shipperIds, a.getBuuCucId());
             TrangThai t = new TrangThai(6);
             List<PhanCong> phanCongss = new ArrayList<>();
             List<Integer> donHang = new ArrayList<Integer>();
             System.out.println(listNhanVien != null && listNhanVien.size() == shipperIds.size());
             if (listNhanVien != null && listNhanVien.size() == shipperIds.size()
                     && orderRepo.getAllOrderOfBuuCucCanShiping(a.getBuuCucId(), orderIds).size() == orderIds.size()) {
                 phanCongs.forEach(v -> {
                     v.getOrders().forEach(vv -> {
                         phanCongss.add(new PhanCong(v.getNhanVien(), vv, 1, 0));
                         vv.setTrangThai(t);
                         donHang.add(vv.getId());
                         thoiDiemTrangThoi.add(new ThoiDiemTrangThai(v.getNhanVien(), vv, t));
                     });
                 });

                 phanCongRepository.saveAll(phanCongss);
                 thoiDiemTrangThaiRepo.saveAll(thoiDiemTrangThoi);
                 orderRepo.updateStatusOrderById(6, orderIds);
                 orderRepo.updateOrderShipping(orderIds);
                 
             }
             throw new Exception("Danh sách shiper hoặc trạng thái đơn hàng không hợp lệ");
         }
         throw new Exception(" ");
    }
    @Transactional
    public void cancelListOrder(List<Integer> orderIds,AminBean a) {
    	 if (orderRepo.checkOrderOfBuuCuc(orderIds, a.getBuuCucId()) == orderIds.size()) {
             orderRepo.cancelListOrder(orderIds);
             orderRepo.mergePhanCongWhenCancel(orderIds);
             TrangThai trangThai = new TrangThai(11);
             DiemNhanHang diemNhanHang = new DiemNhanHang(a.getBuuCucId());
             List<ThoiDiemTrangThai> danhSachTTTT = orderIds.stream()
                     .map(id -> new ThoiDiemTrangThai(a.getNhanVien(), new DonHang(id), trangThai, diemNhanHang))
                     .collect(Collectors.toList());
             thoiDiemTrangThaiRepo.saveAll(danhSachTTTT);
             
         }
    }
    @Transactional
    public void phanCongLayHang( List<PhanCongDTO> phanCongs,AminBean a) throws Exception {
    	List<Integer> orderIds = new ArrayList<>();
        List<Integer> shipperIds = new ArrayList<>();
        if (phanCongs.size() > 0) {
            phanCongs.forEach(v -> {
                shipperIds.add(v.getNhanVien().getId());
                v.getOrders().forEach(vv -> {
                    orderIds.add(vv.getId());
                });
            });
            List<NHANVIEN> listNhanVien = phanCongService.checkShiper(shipperIds, a.getBuuCucId());
            List<PhanCong> phanCongss = new ArrayList<>();
            List<DonHang> donHang = new ArrayList<>();
            if (listNhanVien != null && listNhanVien.size() == shipperIds.size()
                    && orderRepo.getAllOrderOfBuuCucCanLayHang(a.getBuuCucId(), orderIds).size() == orderIds.size()) {
                phanCongs.forEach(v -> {
                    v.getOrders().forEach(vv -> {
                        phanCongss.add(new PhanCong(v.getNhanVien(), vv, 0, 0));
                        donHang.add(vv);
                    });
                });
                phanCongRepository.saveAll(phanCongss);
            }
            throw new Exception("Danh sách shiper hoặc trạng thái đơn hàng không hợp lệ");
        }
        throw new Exception("Danh sách shiper hoặc trạng thái đơn hàng không hợp lệ");
    }
    public Map<String, PhanCongDTO> goiYPhanCongLay(List<Integer> shipperIds,Integer top,AminBean a) throws Exception{
    	List<NHANVIEN> listNhanVien = phanCongService.checkShiper(shipperIds, a.getBuuCucId());
        List<DonHang> orders = new ArrayList<>();
        if (listNhanVien != null && listNhanVien.size() == shipperIds.size()) {
            orders = phanCongService.getOrderTopLayHang(a.getBuuCucId(), top < 1 ? 100000 : top);
            Map<String, PhanCongDTO> data = phanCongService.generateShiperLayHang(orders, listNhanVien);
            return data;
        }
        throw new Exception();
    }
    public Map<String, PhanCongDTO> goiYPhanCong(List<Integer> shipperIds,Integer top,AminBean a) throws Exception{
    	 List<NHANVIEN> listNhanVien = phanCongService.checkShiper(shipperIds, a.getBuuCucId());
         List<DonHang> orders = new ArrayList<>();
         if (listNhanVien != null && listNhanVien.size() == shipperIds.size()) {
             // System.out.println(a.getBuuCucId());
             Integer buuCucId = a.getBuuCucId();
             orders = phanCongService.getOrderTop(buuCucId, top < 1 ? 100000 : top);

             Map<String, PhanCongDTO> data = phanCongService.generateShiper(orders, listNhanVien);
             return data;
         }
         throw new Exception();
    }
    @Transactional
    public void submitPhieuChuyenGiao(List<PhieuChuyenGiao> phieuChuyenGiaos,AminBean a) throws Exception {
    	 if (phieuChuyenGiaos.size() > 0) {
             List<ThoiDiemTrangThai> thoiDiemTrangThais = new ArrayList<>();
             List<DonHang> orders = new ArrayList<>();
             phieuChuyenGiaos.forEach(v -> {
                 if (v.getOrders().stream().filter(vv -> vv.getId() == null).toList().size() > 0) {
                     throw new RuntimeException("Có đơn hàng không hợp lệ");
                 }
                 List<DonHang> orderss = orderRepo.getAllOrderOfBuuCucAndInList(a.getBuuCucId(), v.getOrders());
                 if (orderss.size() == v.getOrders().size()) {
                     v.setOrders(orders);
                 } else {
                     throw new RuntimeException("Danh sách đơn hàng ko hợp lệ");
                 }
                 DiemNhanHang diemNhanHang = diemNhanHangRepo
                         .findById(v.getDiemNhanHang().getId() == null ? -1 : v.getDiemNhanHang().getId()).orElse(null);
                 if (diemNhanHang != null && diemNhanHang.getId() != a.getBuuCucId()) {
                     v.setDiemNhanHang(diemNhanHang);
                     TrangThai trangThai = new TrangThai(7);
                     orderss.forEach(vv -> {
                         vv.setTrangThai(trangThai);
                         thoiDiemTrangThais.add(new ThoiDiemTrangThai(a.getNhanVien(), vv, trangThai));
                         orders.add(vv);
                         vv.setPhieuChuyenGiao(v);
                         Float aa = 3.4f;
                         vv.setLatSend(aa);
                         vv.setLongSend(aa);
                         vv.setDiaChiNguoiGui("dcdocduhcudhcuhdchu");
                     });
                     v.setNgayLap(LocalDateTime.now());
                     v.setId(null);
                     v.setTrangThai(0);
                     v.setNhanvien(a.getNhanVien());
                 } else {
                     throw new RuntimeException("Điểm nhận hàng có vẻ không hợp lệ");
                 }
             });
             phieuChuyenGiaoRepo.saveAll(phieuChuyenGiaos);
             orderRepo.saveAll(orders);
             thoiDiemTrangThaiRepo.saveAll(thoiDiemTrangThais);
             phieuChuyenGiaos.forEach(v -> {
                 Notification.pushNotifycationBuuCuc(v.getDiemNhanHang().getId(), "PHIEUCHUYENGIAO",
                         "Một phiếu chuyển giao vừa được lập đến điểm nhận hàng của bạn", LocalDateTime.now().toString(),
                         v.getId());
             });
             System.out.println("Hello bạn hiền");
         }

        throw  new Exception("Danh sách phiếu chuyển giao rỗng");
    }
    
    public void autoTransfom(AminBean a) {
    	List<DonHang> orders = getAllOrderOfBuuCuc(a.getBuuCucId());
        List<DiemNhanHangDTO> l = getAllDiemNhanHangHopLy(a.getBuuCucId());
        Map<Object, List<DiemNhanHangDTO>> diemNhanHangs = l.stream()
                .collect(Collectors.groupingBy(DiemNhanHangDTO::getOrderId));
        List<PhieuChuyenGiao> phieuChuyenGiaos = new ArrayList<>();
        List<DonHang> donHangs = new ArrayList<>();
        orders.forEach(v -> {
            List<DiemNhanHangDTO> diemNhanHangDTO;
            diemNhanHangDTO = diemNhanHangs.get(v.getId());
            if (diemNhanHangDTO != null) {
                DiemNhanHang diemNhanHang = new DiemNhanHang(
                        Integer.parseInt(diemNhanHangDTO.get(0).getDiemNhanHangId() + ""));
                PhieuChuyenGiao phieuChuyenGiao = new PhieuChuyenGiao();
                phieuChuyenGiao.setDiemNhanHang(diemNhanHang);
                phieuChuyenGiao.setNgayLap(LocalDateTime.now());
                phieuChuyenGiao.setGhiChu("...");
                phieuChuyenGiaos.add(phieuChuyenGiao);
                v.setPhieuChuyenGiao(phieuChuyenGiao);
                donHangs.add(v);
            }
        });
      transfomAuto(orders, phieuChuyenGiaos);
        
    }
    
    public void chuyenTiepDonHang(AminBean a, int orderId, int buuCucId) throws Exception {
    	DonHang order = getOrderById(orderId);
        if (order != null && order.getDiemNhanHang().getId() == a.getBuuCucId()) {
            if (order.getPhieuChuyenGiao() == null) {
                if (order.getDiemNhanHang().getId() != buuCucId) {
                    PhieuChuyenGiao phieuChuyenGiao = new PhieuChuyenGiao();
                    phieuChuyenGiao.setDiemNhanHang(new DiemNhanHang(buuCucId));
                    phieuChuyenGiao.setNgayLap(LocalDateTime.now());
                    phieuChuyenGiao.setNhanvien(a.getNhanVien());
                    order.setPhieuChuyenGiao(phieuChuyenGiao);
                    order.setTrangThai(new TrangThai(7));
                    chuyenTiepSingle(phieuChuyenGiao, order);
                }
                throw new Exception("Điểm chuyển tiếp phải khác bưu cục của bạn");
            }
            throw new Exception("Đơn hàng đã thực hiện chuyển tiếp");
        }
        throw new Exception("Đơn hàng ko tìm thấy hoặc ko phải của bưu cục");
    }
    @Transactional
    public void nextStatusList(List<Integer> orderIds,AminBean a) throws Exception {
    	List<DonHang> orders =getOrderInList(orderIds, a.getBuuCucId());
        if (orders.size() == orderIds.size()) {
            TrangThai trangThai = new TrangThai(5);
            List<ThoiDiemTrangThai> thoiDiemTrangThais = new ArrayList<>();
            DiemNhanHang diemNhanHang = new DiemNhanHang(a.getBuuCucId());
            orders.forEach(v -> {
                thoiDiemTrangThais.add(new ThoiDiemTrangThai(a.getNhanVien(), v, trangThai, diemNhanHang));
                v.setTrangThai(trangThai);
            });
            orderRepo.saveAll(orders);
            thoiDiemTrangThaiRepo.saveAll(thoiDiemTrangThais);
        }
      throw new Exception("Danh sách đơn hàng không hợp lệ hoặc không ở trạng thái có thể thay đổi");
    }
    @Transactional
    public void nextStatus(Integer orderId,AminBean a) throws Exception {
    	DonHang order =getOrderById(orderId);
        if (order.getDiemNhanHang().getId() == a.getBuuCucId()) {
            TrangThai trangThai = new TrangThai(5);
            if (order.getTrangThai().getId() == 4) {
                order.setTrangThai(trangThai);
                ThoiDiemTrangThai thoiDiemTrangThai = new ThoiDiemTrangThai(a.getNhanVien(), order,
                        trangThai, new DiemNhanHang(a.getBuuCucId()));
                orderRepo.save(order);
                thoiDiemTrangThaiRepo.save(thoiDiemTrangThai);
            }
           throw new Exception("Không thể tiếp tuch trạng thái với đơn hàng nay , vui lòng thao tác trên trang");
        }
        throw new Exception("Không tìm thấy đơn hàng này của bưu cục");
    }
    @Transactional
    public void cancelOrder(int orderId,AminBean a) throws Exception {
    	TrangThai trangThai = new TrangThai(11);
        if (cancelOrderOfBuuCuc(orderId, trangThai, a.getBuuCucId())) {
            ThoiDiemTrangThai thoiDiemTrangThai = new ThoiDiemTrangThai();
            thoiDiemTrangThai.setNhanvien(null);
            thoiDiemTrangThai.setThoiDiem(LocalDateTime.now());
            thoiDiemTrangThai.setDonHang(new DonHang(orderId));
            thoiDiemTrangThai.setTrangThai(trangThai);
          saveOrderThoiDiemTrangThai(thoiDiemTrangThai);
        } else {
           throw new Exception("Đơn hàng không hợp lệ");
        }
    }
    
    public Map<String, Object> getOrderPhanCong(String tenNguoiNhan,Integer trangThaiId,Integer id,String sortBy,Integer page,AminBean a) {
    	 Map<String, Object> respone = new HashMap<>();
         List<DonHang> orders =
                 getAllAdmin(id, trangThaiId, tenNguoiNhan, null, null, 0, 100, a.getBuuCucId())
                 .getContent();
         orders.forEach(v -> {
             v.setPhanCongs(v.getPhanCongs().stream()
                     .filter(vv -> vv.getNhanVien().getDiemNhanHang().getId() == a.getBuuCucId()).toList());

         });
         List<DiemNhanHangDTO> l = getAllDiemNhanHangHopLy(a.getBuuCucId());
         respone.put("orders", orders);
         respone.put("diemNhanHang", l.stream()
                 .collect(Collectors.groupingBy(DiemNhanHangDTO::getOrderId)));
         return respone;
    }
}