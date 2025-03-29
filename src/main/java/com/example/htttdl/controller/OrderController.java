package com.example.htttdl.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.htttdl.FirebaseCrud.Notification;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.DonHangBuuCuc;
import com.example.htttdl.modal.DonHangLayNHan;
import com.example.htttdl.modal.Huyen;
import com.example.htttdl.modal.KhachHang;
import com.example.htttdl.modal.ThoiDiemTrangThai;
import com.example.htttdl.modal.TrangThai;
import com.example.htttdl.modal.Xa;
import com.example.htttdl.repository.DonHangBuuCucRepository;
import com.example.htttdl.repository.DonHangLayNhanRepository;
import com.example.htttdl.repository.KhachHangRepository;
import com.example.htttdl.repository.OrderRepository;
import com.example.htttdl.repository.XaRepository;
import com.example.htttdl.response.ObjectRespone;
import com.example.htttdl.service.ExportPDF;
import com.example.htttdl.service.OrderService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
public class OrderController {
    @Autowired
    DonHangBuuCucRepository dh;

    @Autowired
    DonHangLayNhanRepository dhLayNhan;

    @Autowired
    OrderService orderService;
    @Autowired
    KhachHang khachHang;

    @Autowired
    XaRepository xaRepo;

    @Autowired
    OrderRepository orderRepository;

    @PostMapping("/customer/order/export-pdf")
    public ResponseEntity<Object> exportPDFOrders(@RequestBody List<Integer> orderIds) throws Exception {
        List<DonHang> p = new ArrayList<>();
        if (orderIds.size() < 0) {

        }
        p = orderRepository.getAllOrderInListAndOfKhachHang(orderIds, khachHang.getId());
        byte[] pdfData = ExportPDF.createPdf("Tạo đơn hàng", p);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shipping_label.pdf\"")
                .body(pdfData);
    }

    @GetMapping("/customer/orders")
    public ResponseEntity<Object> getAllOrder(
            @RequestParam(name = "tenNguoiNhan", required = false) String tenNguoiNhan,
            @RequestParam(name = "trangThaiId", defaultValue = "1") Integer trangThaiId,
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {
        System.out.println("LAY DON HANG CUA CUSTOMER :" + khachHang.getId());
        return new ResponseEntity<>(
                new ObjectRespone("success",
                        orderService.getAll(id, trangThaiId, tenNguoiNhan, new KhachHang(khachHang.getId()), sortBy,
                                page,
                                1000)),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/getbuucucnear")
    public ResponseEntity<Object> getNear(@RequestParam(name = "lat") Double lat,
            @RequestParam(name = "longti") Double longti) {
        return new ResponseEntity<>(orderService.getDiemNhanHangNear(lat, longti), HttpStatus.ACCEPTED);
    }

    @Transactional
    @PostMapping("/customer/order/add/buucuc")
    public ResponseEntity<Object> addOrder(@RequestBody @Valid DonHangBuuCuc order) throws Exception {
        Xa xa = new Xa();
        xa.setTenXa(order.getDiaChiNguoiGui());
        xa.setHuyen(new Huyen(1));
        xa.setTenXa(order.getDiaChiNguoiGui());
        xa.setViDo(order.getLatSend());
        xa.setKinhDo(order.getLongSend());
        xaRepo.save(xa);
        order.setKhachHang(new KhachHang(khachHang.getId()));
        order.setXa(xa);
        orderService.getBaseAddressBuuCuc(order);
        orderService.checkHinhThucVanChuyen(order);
        orderService.caculateFee(order);
        orderService.saveOrder(order, 3);
        DonHang d = orderService.getOrderById(order.getId());
        System.out.println("TEN XA LA: " + d.getKhachHang().getTen());
        Notification.pushNotifycationBuuCuc(order.getDiemNhanHang().getId(), "ORDER",
                "ĐON HÀNG MỚI: Đơn hàng " + order.getId() + "Từ khác hàng " + d.getKhachHang().getTen(),
                "2025-02-02", d.getId());
        List<DonHang> p = new ArrayList<>();
        p.add(d);
        byte[] pdfData = ExportPDF.createPdf("Tạo đơn hàng", p);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shipping_label.pdf\"")
                .body(pdfData);
    }

    @Autowired
    KhachHangRepository khachHangRepository;

    @PostMapping("/customer/order/add/laynhan")
    public ResponseEntity<Object> addOrder(@RequestBody DonHangLayNHan order) throws Exception {
        order.setKhachHang(new KhachHang(khachHang.getId()));
        Xa xa = new Xa();
        xa.setTenXa(order.getDiaChiNguoiGui());
        xa.setHuyen(new Huyen(1));
        xa.setTenXa(order.getDiaChiNguoiGui());
        xa.setViDo(order.getLatSend());
        xa.setKinhDo(order.getLongSend());
        xaRepo.saveAndFlush(xa);
        order.setXa(xa);
        orderService.getBaseAddressLayNhan(order);
        orderService.checkHinhThucVanChuyen(order);
        orderService.caculateFee(order);
        orderService.saveOrder(order, 1);
        List<DonHang> p = new ArrayList<>();
        p.add(order);
        byte[] pdfData = ExportPDF.createPdf("Tạo đơn hàng", p);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shipping_label.pdf\"")
                .body(pdfData);
        // return new ResponseEntity<>(new ObjectRespone("Thêm đơn hàng thành công",
        // null), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/customer/order/cancel")
    public ResponseEntity<Object> cancelOrder(@RequestParam(name = "orderId", defaultValue = "-1") Integer orderId) {
        TrangThai trangThai = new TrangThai(11);
        if (orderService.cancelOrder(orderId, trangThai, khachHang.getId())) {
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

    @GetMapping("/customer/order/getbyid")
    public ResponseEntity<Object> getOrderInfo(@RequestParam(name = "orderId", defaultValue = "-1") Integer orderId) {
        DonHang donHnag = orderService.getInfo(orderId, khachHang.getId());
        if (donHnag != null) {
            return new ResponseEntity<>(new ObjectRespone("success", donHnag), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ObjectRespone("success", donHnag), HttpStatus.OK);
    }

    @PutMapping("/customer/order/update/buucuc")
    public ResponseEntity<Object> updateOrder(@RequestBody @Valid DonHangBuuCuc order) {
        Boolean a = orderService.checkOrderCanUpdate(order.getId(), khachHang.getId());
        if (a) {
            order.setKhachHang(new KhachHang(1));
            orderService.getBaseAddressBuuCuc(order);
            orderService.checkHinhThucVanChuyen(order);
            orderService.caculateFee(order);
            orderService.saveOrder(order, 3);
            return new ResponseEntity<>(new ObjectRespone("Cập nhật đơn hàng thành công", null), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ObjectRespone("Đơn hàng không hợp lệ", null), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/customer/order/update/laynhan")
    public ResponseEntity<Object> updateOrder(@RequestBody @Valid DonHangLayNHan order) {
        Boolean a = orderService.checkOrderCanUpdate(order.getId(), khachHang.getId());
        if (a) {
            order.setKhachHang(new KhachHang(1));
            orderService.getBaseAddressLayNhan(order);
            orderService.checkHinhThucVanChuyen(order);
            orderService.caculateFee(order);
            orderService.saveOrder(order, 1);
            return new ResponseEntity<>(new ObjectRespone("Cập nhật đơn hàng thành công", null), HttpStatus.OK);

        }
        return new ResponseEntity<>(new ObjectRespone("Đơn hàng không hợp lệ", null), HttpStatus.BAD_REQUEST);
    }

}
