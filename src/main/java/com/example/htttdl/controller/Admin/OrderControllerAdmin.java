package com.example.htttdl.controller.Admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.htttdl.DTO.Customer.Admin.DiemNhanHangDTO;
import com.example.htttdl.DTO.Customer.Admin.PhanCongDTO;
import com.example.htttdl.FirebaseCrud.Notification;
import com.example.htttdl.config.AminBean;
import com.example.htttdl.modal.DiemNhanHang;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.modal.PhanCong;
import com.example.htttdl.modal.PhieuChuyenGiao;
import com.example.htttdl.modal.ThoiDiemTrangThai;
import com.example.htttdl.modal.TrangThai;
import com.example.htttdl.repository.DiemNhanHangRepository;
import com.example.htttdl.repository.OrderRepository;
import com.example.htttdl.repository.PhanCongRepository;
import com.example.htttdl.repository.PhieuChuyenGiaoRepository;
import com.example.htttdl.repository.ThoiDiemTrangThaiRepository;
import com.example.htttdl.response.ObjectRespone;
import com.example.htttdl.service.ExportPDF;
import com.example.htttdl.service.OrderService;
import com.example.htttdl.service.PhanCongService;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin("*")
public class OrderControllerAdmin {

    @Autowired
    OrderService orderService;

    @Autowired
    ThoiDiemTrangThaiRepository thoiDiemTrangThaiRepo;

    @Autowired
    PhanCongService phanCongService;

    @Autowired
    DiemNhanHangRepository diemNhanHangRepo;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    PhieuChuyenGiaoRepository phieuChuyenGiaoRepository;

    @Autowired
    AminBean a;

    // @Transactional
    @PostMapping("admin/order/transfom/groupby/get")
    public ResponseEntity<Object> getTransformGroups(@RequestBody List<Integer> orderIds) throws Exception {
        List<DonHang> orders = orderRepo.getAllOrderOfBuuCucAndInLists(a.getBuuCucId(), orderIds);
        List<DiemNhanHangDTO> l = orderService.getAllDiemNhanHangHopLy(a.getBuuCucId());
        l = this.filterUniqueOrderWithMinDistance(l);
        List<PhieuChuyenGiao> phieuChuyenGiaos = new ArrayList<>();
        Map<Integer, DonHang> ordersMap = orders.stream()
                .collect(Collectors.toMap(DonHang::getId, donHang -> donHang));
        Map<Object, List<DiemNhanHangDTO>> groupedByDiemNhanHang = l.stream()
                .collect(Collectors.groupingBy(DiemNhanHangDTO::getDiemNhanHangId));

        Integer t = -1;

        for (Map.Entry<Object, List<DiemNhanHangDTO>> entry : groupedByDiemNhanHang.entrySet()) {
            Object key = entry.getKey();
            List<DiemNhanHangDTO> value = entry.getValue();
            PhieuChuyenGiao phieuChuyenGiao = new PhieuChuyenGiao();
            phieuChuyenGiao.setId(-1); // Gán ID là -1
            phieuChuyenGiao.setDiemNhanHang(diemNhanHangRepo.findById((int) key).orElse(null));
            phieuChuyenGiao.setGhiChu("...");
            phieuChuyenGiao.setNgayLap(LocalDateTime.now());
            phieuChuyenGiao.setNhanvien(a.getNhanVien());
            phieuChuyenGiaos.add(phieuChuyenGiao);
            phieuChuyenGiao.setOrders(new ArrayList<>());

            for (DiemNhanHangDTO v : value) {
                DonHang d = ordersMap.get(v.getOrderId());
                if (d != null) {
                    d.setTrangThai(new TrangThai(7));
                    t++;
                    phieuChuyenGiao.getOrders().add(d);
                    d.setPhieuChuyenGiao(phieuChuyenGiao);
                }
            }
        }

        if (t == -1) {
            return new ResponseEntity<>(
                    new ObjectRespone("Ko có đơn hàng nào được chuyển tiếp do chưa có điểm chuyển tiếp phù hợp", null),
                    HttpStatus.BAD_REQUEST);
        }
        if (!phieuChuyenGiaos.isEmpty()) {
            System.out.println("Tới đây ");
            return new ResponseEntity<>(new ObjectRespone("success", phieuChuyenGiaos), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Transactional
    @PostMapping("/admin/order/cancel")
    public ResponseEntity<Object> cancelOrder(@RequestParam(name = "orderId", defaultValue = "-1") Integer orderId) {
        TrangThai trangThai = new TrangThai(11);
        if (orderService.cancelOrderOfBuuCuc(orderId, trangThai, a.getBuuCucId())) {
            ThoiDiemTrangThai thoiDiemTrangThai = new ThoiDiemTrangThai();
            thoiDiemTrangThai.setNhanvien(null);
            thoiDiemTrangThai.setThoiDiem(LocalDateTime.now());
            thoiDiemTrangThai.setDonHang(new DonHang(orderId));
            thoiDiemTrangThai.setTrangThai(trangThai);
            orderService.saveOrderThoiDiemTrangThai(thoiDiemTrangThai);
            return new ResponseEntity<>(new ObjectRespone("oHuy don haang thanh cong", null), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ObjectRespone("Don hang ko hopw le", null), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PostMapping("/admin/order/nextstatus")
    public ResponseEntity<Object> nextStatus(@RequestParam(name = "orderId", defaultValue = "-1") Integer orderId) {
        DonHang order = orderService.getOrderById(orderId);
        if (order.getDiemNhanHang().getId() == a.getBuuCucId()) {
            TrangThai trangThai = new TrangThai(5);
            if (order.getTrangThai().getId() == 4) {
                order.setTrangThai(trangThai);
                ThoiDiemTrangThai thoiDiemTrangThai = new ThoiDiemTrangThai(a.getNhanVien(), order,
                        trangThai, new DiemNhanHang(a.getBuuCucId()));
                orderRepo.save(order);
                thoiDiemTrangThaiRepo.save(thoiDiemTrangThai);
                return new ResponseEntity<>(new ObjectRespone("success", null), HttpStatus.OK);
            }
            return new ResponseEntity<>(
                    new ObjectRespone("Không thể tiếp tuch trạng thái với đơn hàng nay , vui lòng thao tác trên trang",
                            null),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ObjectRespone("Không tìm thấy đơn hàng này của bưu cục", null),
                HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @PostMapping("/admin/order/nextstatuslist")
    public ResponseEntity<Object> nextStatusList(@RequestBody List<Integer> orderIds) {
        List<DonHang> orders = orderService.getOrderInList(orderIds, a.getBuuCucId());
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
            return new ResponseEntity<>(
                    new ObjectRespone("Cập nhật trạng thái thành công",
                            null),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new ObjectRespone("Danh sách đơn hàng không hợp lệ hoặc không ở trạng thái có thể thay đổi", null),
                HttpStatus.BAD_REQUEST);
    }

    @GetMapping("admin/phancong/allshiper")
    public ResponseEntity<Object> getAllShipperOfBuuCuc() {
        var shipper = phanCongService.getAllShipper(a.getBuuCucId());

        return new ResponseEntity<>(shipper,
                HttpStatus.OK);
    }

    @GetMapping("/admin/order/getorder")
    public ResponseEntity<Object> getOrderPhanCong(
            @RequestParam(name = "tenNguoiNhan", required = false) String tenNguoiNhan,
            @RequestParam(name = "trangThaiId", defaultValue = "1") Integer trangThaiId,
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {
        Map<String, Object> respone = new HashMap<>();
        List<DonHang> orders = orderService
                .getAllAdmin(id, trangThaiId, tenNguoiNhan, null, null, 0, 100, a.getBuuCucId())
                .getContent();
        orders.forEach(v -> {
            v.setPhanCongs(v.getPhanCongs().stream()
                    .filter(vv -> vv.getNhanVien().getDiemNhanHang().getId() == a.getBuuCucId()).toList());

        });
        List<DiemNhanHangDTO> l = orderService.getAllDiemNhanHangHopLy(a.getBuuCucId());
        respone.put("orders", orders);
        respone.put("diemNhanHang", l.stream()
                .collect(Collectors.groupingBy(DiemNhanHangDTO::getOrderId)));
        return new ResponseEntity<Object>(respone, HttpStatus.OK);
    }

    @PostMapping("/admin/order/chuyentiep")
    public ResponseEntity<Object> chuyenTiepDonHang(
            @RequestParam(name = "orderId", defaultValue = "-1") Integer orderId,
            @RequestParam(name = "buuCucId", defaultValue = "-1") Integer buuCucId) {
        DonHang order = orderService.getOrderById(orderId);
        if (order != null && order.getDiemNhanHang().getId() == a.getBuuCucId()) {
            if (order.getPhieuChuyenGiao() == null) {
                if (order.getDiemNhanHang().getId() != buuCucId) {
                    PhieuChuyenGiao phieuChuyenGiao = new PhieuChuyenGiao();
                    phieuChuyenGiao.setDiemNhanHang(new DiemNhanHang(buuCucId));
                    phieuChuyenGiao.setNgayLap(LocalDateTime.now());
                    phieuChuyenGiao.setNhanvien(a.getNhanVien());
                    order.setPhieuChuyenGiao(phieuChuyenGiao);
                    order.setTrangThai(new TrangThai(7));
                    orderService.chuyenTiepSingle(phieuChuyenGiao, order);
                    return new ResponseEntity<>(new ObjectRespone("Chuyển tiếp thành công", null),
                            HttpStatus.OK);
                }
                return new ResponseEntity<>(new ObjectRespone("Điểm chuyển tiếp phải khác bưu cục của bạn", null),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new ObjectRespone("Đơn hàng đã thực hiện chuyển tiếp", null),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ObjectRespone("Đơn hàng ko tìm thấy hoặc ko phải của bưu cục", null),
                HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @PostMapping("admin/order/transfom")
    public ResponseEntity<Object> autoTransfom() {
        List<DonHang> orders = orderService.getAllOrderOfBuuCuc(a.getBuuCucId());
        List<DiemNhanHangDTO> l = orderService.getAllDiemNhanHangHopLy(a.getBuuCucId());
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
                // phieuChuyenGiao.setNhanvien(a.getNhanVien());
                phieuChuyenGiaos.add(phieuChuyenGiao);
                v.setPhieuChuyenGiao(phieuChuyenGiao);
                donHangs.add(v);
            }
        });
        orderService.transfomAuto(orders, phieuChuyenGiaos);
        return new ResponseEntity<>(new ObjectRespone("Đã làm phiếu chuyển giao các đơn hàng", null), HttpStatus.OK);
    }

    public List<DiemNhanHangDTO> filterUniqueOrderWithMinDistance(List<DiemNhanHangDTO> list) {
        return new ArrayList<>(list.stream()
                .collect(Collectors.toMap(
                        DiemNhanHangDTO::getOrderId, // Nhóm theo orderId
                        dto -> dto, // Giá trị ban đầu
                        (dto1, dto2) -> Double.compare(
                                (Double) dto1.getKhoangCachDuTinhNeChuyenTiep(),
                                (Double) dto2.getKhoangCachDuTinhNeChuyenTiep()) <= 0 ? dto1 : dto2 // Chọn phần tử có
                                                                                                    // khoảng cách nhỏ
                                                                                                    // hơn
                ))
                .values());
    }

    @Transactional
    @PostMapping("admin/order/transfom/groupby")
    public ResponseEntity<Object> autoTransfomGrouByPhieuChuyenGiao() throws Exception {
        List<DonHang> orders = orderService.getAllOrderOfBuuCuc(a.getBuuCucId());
        List<DiemNhanHangDTO> l = orderService.getAllDiemNhanHangHopLy(a.getBuuCucId());
        l = this.filterUniqueOrderWithMinDistance(l);
        List<PhieuChuyenGiao> phieuChuyenGiaos = new ArrayList<>();
        Map<Integer, DonHang> ordersMap = orders.stream()
                .collect(Collectors.toMap(DonHang::getId, donHang -> donHang));
        Map<Object, List<DiemNhanHangDTO>> groupedByDiemNhanHang = l.stream()
                .collect(Collectors.groupingBy(DiemNhanHangDTO::getDiemNhanHangId));

        Integer t = -1;

        for (Map.Entry<Object, List<DiemNhanHangDTO>> entry : groupedByDiemNhanHang.entrySet()) {
            Object key = entry.getKey();
            List<DiemNhanHangDTO> value = entry.getValue();
            PhieuChuyenGiao phieuChuyenGiao = new PhieuChuyenGiao();
            phieuChuyenGiao.setDiemNhanHang(new DiemNhanHang((int) key));
            phieuChuyenGiao.setGhiChu("...");
            phieuChuyenGiao.setNgayLap(LocalDateTime.now());
            phieuChuyenGiao.setNhanvien(a.getNhanVien());
            phieuChuyenGiaos.add(phieuChuyenGiao);
            phieuChuyenGiao.setOrders(new ArrayList<>());

            for (DiemNhanHangDTO v : value) {
                DonHang d = ordersMap.get(v.getOrderId());
                if (d != null) {
                    d.setTrangThai(new TrangThai(7));
                    t++;
                    phieuChuyenGiao.getOrders().add(d);
                    d.setPhieuChuyenGiao(phieuChuyenGiao);
                }
            }
        }

        if (t == -1) {
            System.out.println("XONG ROI");
            return new ResponseEntity<>(
                    new ObjectRespone("Ko có đơn hàng nào được chuyển tiếp do chưa có điểm chuyển tiếp phù hợp", null),
                    HttpStatus.BAD_REQUEST);
        }

        orderService.transfomAuto(orders, phieuChuyenGiaos);
        if (!phieuChuyenGiaos.isEmpty()) {
            byte[] pdfData = ExportPDF.createPdfPhieuChuyenGiao("Xin chào Bạn ơi ", phieuChuyenGiaos);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shipping_label.pdf\"")
                    .body(pdfData);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Transactional
    @PostMapping("admin/order/transfom/groupby/submit")
    public ResponseEntity<Object> submitPhieuChuyenGiao(@RequestBody List<PhieuChuyenGiao> phieuChuyenGiaos)
            throws Exception {
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
                        Float a = 3.4f;
                        vv.setLatSend(a);
                        vv.setLongSend(a);
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
            phieuChuyenGiaoRepository.saveAll(phieuChuyenGiaos);
            orderRepo.saveAll(orders);
            thoiDiemTrangThaiRepo.saveAll(thoiDiemTrangThais);
            phieuChuyenGiaos.forEach(v -> {
                Notification.pushNotifycationBuuCuc(v.getDiemNhanHang().getId(), "PHIEUCHUYENGIAO",
                        "Một phiếu chuyển giao vừa được lập đến điểm nhận hàng của bạn", LocalDateTime.now().toString(),
                        v.getId());
            });
            System.out.println("Hello bạn hiền");
            return new ResponseEntity<>(new ObjectRespone("success", null),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(new ObjectRespone("Danh sách phiếu chuyển giao rỗng", null),
                HttpStatus.BAD_REQUEST);

        // List<DiemNhanHangDTO> l =
        // orderService.getAllDiemNhanHangHopLy(a.getBuuCucId());
        // l = this.filterUniqueOrderWithMinDistance(l);
        // List<PhieuChuyenGiao> phieuChuyenGiaos = new ArrayList<>();
        // Map<Integer, DonHang> ordersMap = orders.stream()
        // .collect(Collectors.toMap(DonHang::getId, donHang -> donHang));
        // Map<Object, List<DiemNhanHangDTO>> groupedByDiemNhanHang = l.stream()
        // .collect(Collectors.groupingBy(DiemNhanHangDTO::getDiemNhanHangId));

        // Integer t = -1;

        // for (Map.Entry<Object, List<DiemNhanHangDTO>> entry :
        // groupedByDiemNhanHang.entrySet()) {
        // Object key = entry.getKey();
        // List<DiemNhanHangDTO> value = entry.getValue();
        // PhieuChuyenGiao phieuChuyenGiao = new PhieuChuyenGiao();
        // phieuChuyenGiao.setDiemNhanHang(new DiemNhanHang((int) key));
        // phieuChuyenGiao.setGhiChu("...");
        // phieuChuyenGiao.setNgayLap(LocalDateTime.now());
        // phieuChuyenGiao.setNhanvien(a.getNhanVien());
        // phieuChuyenGiaos.add(phieuChuyenGiao);
        // phieuChuyenGiao.setOrders(new ArrayList<>());

        // for (DiemNhanHangDTO v : value) {
        // DonHang d = ordersMap.get(v.getOrderId());
        // if (d != null) {
        // d.setTrangThai(new TrangThai(7));
        // t++;
        // phieuChuyenGiao.getOrders().add(d);
        // d.setPhieuChuyenGiao(phieuChuyenGiao);
        // }
        // }
        // }

        // if (t == -1) {
        // System.out.println("XONG ROI");
        // return new ResponseEntity<>(
        // new ObjectRespone("Ko có đơn hàng nào được chuyển tiếp do chưa có điểm chuyển
        // tiếp phù hợp", null),
        // HttpStatus.BAD_REQUEST);
        // }

        // orderService.transfomAuto(orders, phieuChuyenGiaos);
        // if (!phieuChuyenGiaos.isEmpty()) {
        // byte[] pdfData = ExportPDF.createPdfPhieuChuyenGiao("Xin chào Bạn ơi ",
        // phieuChuyenGiaos);
        // return ResponseEntity.ok()
        // .contentType(MediaType.APPLICATION_PDF)
        // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;
        // filename=\"shipping_label.pdf\"")
        // .body(pdfData);
        // } else {
        // return new ResponseEntity<>(HttpStatus.OK);
        // }
    }

    @PostMapping("admin/order/phancong/{top}")
    public ResponseEntity<Object> goiYPhanCong(@RequestBody List<Integer> shipperIds,
            @PathVariable(name = "top") Integer top) {
        List<NHANVIEN> listNhanVien = phanCongService.checkShiper(shipperIds, a.getBuuCucId());
        List<DonHang> orders = new ArrayList<>();
        if (listNhanVien != null && listNhanVien.size() == shipperIds.size()) {
            // System.out.println(a.getBuuCucId());
            Integer buuCucId = a.getBuuCucId();
            orders = phanCongService.getOrderTop(buuCucId, top < 1 ? 100000 : top);

            Map<String, PhanCongDTO> data = phanCongService.generateShiper(orders, listNhanVien);
            return new ResponseEntity<>(new ObjectRespone("success", data), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("admin/order/phanconglay/{top}")
    public ResponseEntity<Object> goiYPhanCongLay(@RequestBody List<Integer> shipperIds,
            @PathVariable(name = "top") Integer top) {
        List<NHANVIEN> listNhanVien = phanCongService.checkShiper(shipperIds, a.getBuuCucId());
        List<DonHang> orders = new ArrayList<>();
        if (listNhanVien != null && listNhanVien.size() == shipperIds.size()) {
            orders = phanCongService.getOrderTopLayHang(a.getBuuCucId(), top < 1 ? 100000 : top);
            Map<String, PhanCongDTO> data = phanCongService.generateShiperLayHang(orders, listNhanVien);
            return new ResponseEntity<>(new ObjectRespone("success", data), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @PostMapping("admin/order/phanconglayhang")
    public ResponseEntity<Object> phanCongLayHang(@RequestBody List<PhanCongDTO> phanCongs) {
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
                phanCongRepo.saveAll(phanCongss);
                // orderRepo.saveAll(donHang);
                return new ResponseEntity<>(new ObjectRespone("Phân công thành công", null), HttpStatus.OK);
            }
            return new ResponseEntity<>(
                    new ObjectRespone("Danh sách shiper hoặc trạng thái đơn hàng không hợp lệ", null),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @PostMapping("/admin/order/cancellist")
    public ResponseEntity<Object> cancelListOrder(@RequestBody List<Integer> orderIds) {
        if (orderRepo.checkOrderOfBuuCuc(orderIds, a.getBuuCucId()) == orderIds.size()) {
            orderRepo.cancelListOrder(orderIds);
            orderRepo.mergePhanCongWhenCancel(orderIds);
            TrangThai trangThai = new TrangThai(11);
            DiemNhanHang diemNhanHang = new DiemNhanHang(a.getBuuCucId());
            List<ThoiDiemTrangThai> danhSachTTTT = orderIds.stream()
                    .map(id -> new ThoiDiemTrangThai(a.getNhanVien(), new DonHang(id), trangThai, diemNhanHang))
                    .collect(Collectors.toList());
            thoiDiemTrangThaiRepo.saveAll(danhSachTTTT);
            return new ResponseEntity<>(new ObjectRespone("Hủy đơn thành công", null), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    PhanCongRepository phanCongRepo;

    @Transactional
    @PostMapping("admin/order/phancong")
    public ResponseEntity<Object> phanCong(@RequestBody List<PhanCongDTO> phanCongs) {
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

                phanCongRepo.saveAll(phanCongss);
                thoiDiemTrangThaiRepo.saveAll(thoiDiemTrangThoi);
                orderRepo.updateStatusOrderById(6, orderIds);
                orderRepo.updateOrderShipping(orderIds);
                return new ResponseEntity<>(new ObjectRespone("Phân công thành công", null), HttpStatus.OK);
            }
            return new ResponseEntity<>(
                    new ObjectRespone("Danh sách shiper hoặc trạng thái đơn hàng không hợp lệ", null),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/admin/order/export-pdf")
    public ResponseEntity<Object> exportPDFOrders(@RequestBody List<Integer> orderIds) throws Exception {
        List<DonHang> p = new ArrayList<>();
        if (orderIds.size() < 0) {

        }
        p = orderRepo.ggetAllOrderInListAndOfBuuCuc(orderIds, a.getBuuCucId());
        byte[] pdfData = ExportPDF.createPdf("Tạo đơn hàng", p);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shipping_label.pdf\"")
                .body(pdfData);
    }

    @GetMapping("/admin/l")
    public String name() {
        return "Hello security";
    }

}
