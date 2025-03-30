package com.example.htttdl.controller.Shipper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;

import com.example.htttdl.FirebaseCrud.Notification;
import com.example.htttdl.config.ShipperBean;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.modal.PhanCong;
import com.example.htttdl.modal.ThoiDiemTrangThai;
import com.example.htttdl.modal.TrangThai;
import com.example.htttdl.repository.OrderRepository;
import com.example.htttdl.repository.PhanCongRepository;
import com.example.htttdl.repository.ThoiDiemTrangThaiRepository;
import com.example.htttdl.repository.TrangThaiRepository;
import com.example.htttdl.response.ObjectRespone;
import com.example.htttdl.service.OrderService;
import com.example.htttdl.service.ToaDo;
import com.example.htttdl.service.ToaDoDTO;
import com.google.type.Date;

import jakarta.persistence.EntityNotFoundException;

@RestController
public class OrderControllerShipper {

    private ShipperBean shipperBean = new ShipperBean();
    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    private OrderService orderService;
    @PostMapping("/shipper/order/nhanhang/byid")
    public ResponseEntity<Object> layHang(@Param("id") Integer id) throws Exception {
        orderService.layHang(id, shipperBean);
        return new ResponseEntity<>(new ObjectRespone("Nhận thành công đơn hàng", null),HttpStatus.OK);
    }
            

    @GetMapping("/shipper/order/status")
    public ResponseEntity<Object> getAllOrderChoLay(@Param("status") Integer status) {
        List<DonHang> orders = orderService.getAllOrderChoLay(shipperBean, status);
        return new ResponseEntity<>(new ObjectRespone("success", orders), HttpStatus.OK);
    }

    @GetMapping("/shipper/order/layhang/byid")
    public ResponseEntity<Object> getAllOrderChoLayById(@Param("id") Integer id) {
        DonHang order = orderService.getAllOrderChoLayById(id, shipperBean);
        return new ResponseEntity<>(new ObjectRespone("successfully", order), HttpStatus.OK);
    }

    @GetMapping("/shipper/chuyendanggiao")
    public ResponseEntity<Object> ChuyenDangGiao(@RequestParam("id") int idDonHang) {
       orderService.ChuyenDangGiao(idDonHang, shipperBean);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @GetMapping("/shipper/chuyendanggiaosend")
    public ResponseEntity<Object> ChuyenDangGiaosend(@RequestParam("id") int idDonHang) {
      orderService.ChuyenDangGiaosend(idDonHang, shipperBean);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @GetMapping("/shipper/giaoThanhCongTrongNgay")
    public ResponseEntity<Object> getThanhCongTrongNgay() {
        NHANVIEN nv = shipperBean.getNhanVien();
        try {
            orderRepository.getAllOrderOfShipperByStateSuccess(nv.getId(), LocalDate.now());
        } catch (Exception e) {
            System.out.println();
        }
        return new ResponseEntity<Object>(
                new ObjectRespone("successfully",
                        orderRepository.getAllOrderOfShipperByStateSuccess(nv.getId(), LocalDate.now())),
                HttpStatus.OK);
    }

    @GetMapping("/shipper/giaoThanhCongTrongNgaysend")
    public ResponseEntity<Object> thanhcongsend() {
        NHANVIEN nv = shipperBean.getNhanVien();
        try {
            orderRepository.getAllOrderOfShipperByStateSuccesssend(nv.getId(), LocalDate.now());
        } catch (Exception e) {
            System.out.println();
        }
        return new ResponseEntity<Object>(
                new ObjectRespone("successfully",
                        orderRepository.getAllOrderOfShipperByStateSuccesssend(nv.getId(), LocalDate.now())),
                HttpStatus.OK);
    }

    @PostMapping("/shipper/laythanhcong")
    public ResponseEntity<Object> laythanhcong(@RequestParam("id") int idDonHang) {
       orderService.laythanhcong(idDonHang, shipperBean);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @PostMapping("/shipper/setlaythanhcong")
    public ResponseEntity<Object> setLayThanhCong(@RequestParam("idd") int idd) {
       orderService.setLayThanhCong(idd, shipperBean);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @PostMapping("/shipper/setlaythanhcongsend")
    public ResponseEntity<Object> setLayThanhCongsend(@RequestParam("idd") int idd) {
        orderService.setLayThanhCongsend(idd, shipperBean);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @PostMapping("/shipper/lotrinh")
    public ResponseEntity<Object> loTrinh(@RequestBody ToaDoDTO t) throws Exception {
        Map<Object, Object> map=orderService.loTrinh(t);
        return new ResponseEntity<>(new ObjectRespone("successfully", map), HttpStatus.OK);
    }

    @GetMapping("/shipper/ordersend/status")
    public ResponseEntity<Object> getAllOrderChoNhan(@RequestParam("status") Integer status) {
        List<DonHang> orders = orderService.getAllOrderChoNhan(status,shipperBean);
        return new ResponseEntity<>(new ObjectRespone("success", orders), HttpStatus.OK);
    }

}