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
    AminBean a;

   

    @PostMapping("/admin/order/cancel")
    public ResponseEntity<Object> cancelOrder(@RequestParam(name = "orderId", defaultValue = "-1") Integer orderId) throws Exception {
        orderService.cancelOrder(orderId, a);
            return new ResponseEntity<>(new ObjectRespone("Hủy đơn hàng thành công", null), HttpStatus.OK);
    }

   
    @PostMapping("/admin/order/nextstatus")
    public ResponseEntity<Object> nextStatus(@RequestParam(name = "orderId", defaultValue = "-1") Integer orderId) throws Exception {
       orderService.nextStatus(orderId, a);
            return new ResponseEntity<>(
                    new ObjectRespone("Không thể tiếp tuch trạng thái với đơn hàng nay , vui lòng thao tác trên trang",
                            null),
                    HttpStatus.BAD_REQUEST);
    }

  
    @PostMapping("/admin/order/nextstatuslist")
    public ResponseEntity<Object> nextStatusList(@RequestBody List<Integer> orderIds) throws Exception {
        orderService.nextStatusList(orderIds, a);
            return new ResponseEntity<>(
                    new ObjectRespone("Cập nhật trạng thái thành công",
                            null),
                    HttpStatus.OK);
        
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
       
        return new ResponseEntity<Object>(orderService.getOrderPhanCong(tenNguoiNhan, trangThaiId, id, sortBy, page, a), HttpStatus.OK);
    }

    @PostMapping("/admin/order/chuyentiep")
    public ResponseEntity<Object> chuyenTiepDonHang(
            @RequestParam(name = "orderId", defaultValue = "-1") Integer orderId,
            @RequestParam(name = "buuCucId", defaultValue = "-1") Integer buuCucId) throws Exception {
       orderService.chuyenTiepDonHang(a, orderId, buuCucId);
                    return new ResponseEntity<>(new ObjectRespone("Chuyển tiếp thành công", null),
                            HttpStatus.OK);
                
    }

    @Transactional
    @PostMapping("admin/order/transfom")
    public ResponseEntity<Object> autoTransfom() {
        orderService.autoTransfom(a);
        return new ResponseEntity<>(new ObjectRespone("Đã làm phiếu chuyển giao các đơn hàng", null), HttpStatus.OK);
    }

    public List<DiemNhanHangDTO> filterUniqueOrderWithMinDistance(List<DiemNhanHangDTO> list) {
        return new ArrayList<>(list.stream()
                .collect(Collectors.toMap(
                        DiemNhanHangDTO::getOrderId,
                        dto -> dto, 
                        (dto1, dto2) -> Double.compare(
                                (Double) dto1.getKhoangCachDuTinhNeChuyenTiep(),
                                (Double) dto2.getKhoangCachDuTinhNeChuyenTiep()) <= 0 ? dto1 : dto2 
                ))
                .values());
    }

    

    
    @PostMapping("admin/order/transfom/groupby/submit")
    public ResponseEntity<Object> submitPhieuChuyenGiao(@RequestBody List<PhieuChuyenGiao> phieuChuyenGiaos)
            throws Exception {
        orderService.submitPhieuChuyenGiao(phieuChuyenGiaos, a);
            return new ResponseEntity<>(new ObjectRespone("success", null),
                    HttpStatus.OK);
    }

    @PostMapping("admin/order/phancong/{top}")
    public ResponseEntity<Object> goiYPhanCong(@RequestBody List<Integer> shipperIds,
            @PathVariable(name = "top") Integer top) throws Exception {
            return new ResponseEntity<>(new ObjectRespone("success", orderService.goiYPhanCong(shipperIds, top, a)), HttpStatus.OK);
    }

    @PostMapping("admin/order/phanconglay/{top}")
    public ResponseEntity<Object> goiYPhanCongLay(@RequestBody List<Integer> shipperIds,
            @PathVariable(name = "top") Integer top) throws Exception {
            return new ResponseEntity<>(new ObjectRespone("success", orderService.goiYPhanCongLay(shipperIds, top, a)), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("admin/order/phanconglayhang")
    public ResponseEntity<Object> phanCongLayHang(@RequestBody List<PhanCongDTO> phanCongs) throws Exception {
        orderService.phanCongLayHang(phanCongs, a);
                return new ResponseEntity<>(new ObjectRespone("Phân công thành công", null), HttpStatus.OK);
            
    }

    
    @PostMapping("/admin/order/cancellist")
    public ResponseEntity<Object> cancelListOrder(@RequestBody List<Integer> orderIds) {
        orderService.cancelListOrder(orderIds, a);
            return new ResponseEntity<>(new ObjectRespone("Hủy đơn thành công", null), HttpStatus.OK);
    }

    @Autowired
    PhanCongRepository phanCongRepo;

    
    @PostMapping("admin/order/phancong")
    public ResponseEntity<Object> phanCong(@RequestBody List<PhanCongDTO> phanCongs) throws Exception {
        orderService.phanCong(phanCongs, a);
                return new ResponseEntity<>(new ObjectRespone("Phân công thành công", null), HttpStatus.OK);
            
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

}
