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
import com.example.htttdl.service.ToaDo;
import com.example.htttdl.service.ToaDoDTO;
import com.google.type.Date;

import jakarta.persistence.EntityNotFoundException;

@RestController
public class OrderControllerShipper {

    @Autowired
    private ThoiDiemTrangThaiRepository thoiDiemTrangThaiRepository;
    @Autowired
    PhanCongRepository phanCongRepository;
    @Autowired
    private TrangThaiRepository trangThaiRepository;

    private ShipperBean shipperBean = new ShipperBean();
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ThoiDiemTrangThaiRepository thoiDiemTrangThaiRepo;

    @PostMapping("/shipper/order/nhanhang/byid")
    public ResponseEntity<Object> layHang(@Param("id") Integer id) {
        DonHang order = orderRepository.getOrderByIdOfShipper(shipperBean.getNhanVien().getId(), id);
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
                orderRepository.save(order);
                Notification.pushNotifycationBuuCuc(
                        shipperBean.getNhanVien().getDiemNhanHang().getId(), "ORDER", "Đơn hàng số " + order.getId()
                                + " đã được shipper " + shipperBean.getNhanVien().getTen() + " lấy thành công",
                        "2025-02-03", order.getId());
                return new ResponseEntity<>(new ObjectRespone("Nhận thành công đơn hàng", null),
                        HttpStatus.OK);
            }
            return new ResponseEntity<>(new ObjectRespone("Trang thái của đơn hàng không phù hợp", null),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ObjectRespone("Không tìm thấy đơn hàng hiện tại", null),
                HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/shipper/order/status")
    public ResponseEntity<Object> getAllOrderChoLay(@Param("status") Integer status) {
        List<DonHang> orders = new ArrayList<>();
        orders = orderRepository.getAllOrderOfShipper(shipperBean.getNhanVien().getId(), status);
        return new ResponseEntity<>(new ObjectRespone("success", orders), HttpStatus.OK);
    }

    @GetMapping("/shipper/order/layhang/byid")
    public ResponseEntity<Object> getAllOrderChoLayById(@Param("id") Integer id) {
        DonHang order = null;
        // System.out.println(shipperBean.getS);
        order = orderRepository.getOrderByIdOfShipper(shipperBean.getNhanVien().getId(), id, 4);
        return new ResponseEntity<>(new ObjectRespone("successfully", order), HttpStatus.OK);
    }

    @GetMapping("/shipper/chuyendanggiao")
    public ResponseEntity<Object> ChuyenDangGiao(@RequestParam("id") int idDonHang) {
        DonHang d = orderRepository.findById(idDonHang)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
        NHANVIEN nv = shipperBean.getNhanVien();
        TrangThai tt = trangThaiRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
        ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, d, tt);
        thoiDiemTrangThaiRepository.save(t);
        d.setTrangThai(tt);
        orderRepository.save(d);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @GetMapping("/shipper/chuyendanggiaosend")
    public ResponseEntity<Object> ChuyenDangGiaosend(@RequestParam("id") int idDonHang) {
        DonHang d = orderRepository.findById(idDonHang)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
        NHANVIEN nv = shipperBean.getNhanVien();
        TrangThai tt = trangThaiRepository.findById(12)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
        ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, d, tt);
        thoiDiemTrangThaiRepository.save(t);
        d.setTrangThai(tt);
        orderRepository.save(d);
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
        DonHang d = orderRepository.findById(idDonHang)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
        NHANVIEN nv = shipperBean.getNhanVien();
        TrangThai tt = trangThaiRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
        ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, d, tt);
        thoiDiemTrangThaiRepository.save(t);
        d.setTrangThai(tt);
        orderRepository.save(d);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @PostMapping("/shipper/setlaythanhcong")
    public ResponseEntity<Object> setLayThanhCong(@RequestParam("idd") int idd) {
        NHANVIEN nv = shipperBean.getNhanVien();
        PhanCong b = phanCongRepository.getPhanCong(idd, nv.getId());
        b.setTrangThai(2);
        if (b == null) {
            throw new EntityNotFoundException("Bạn không có quyền trên đơn hàng này");
        }
        DonHang d = orderRepository.findById(b.getOrder().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
        TrangThai tt = trangThaiRepository.findById(4)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
        ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, b.getOrder(), tt);
        b.setTrangThai(2);
        phanCongRepository.save(b);
        d.setTrangThai(tt);
        thoiDiemTrangThaiRepository.save(t);
        orderRepository.save(d);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @PostMapping("/shipper/setlaythanhcongsend")
    public ResponseEntity<Object> setLayThanhCongsend(@RequestParam("idd") int idd) {
        NHANVIEN nv = shipperBean.getNhanVien();
        PhanCong b = phanCongRepository.getPhanCongsend(idd, nv.getId());
        b.setTrangThai(2);
        if (b == null) {
            throw new EntityNotFoundException("Bạn không có quyền trên đơn hàng này");
        }
        DonHang d = orderRepository.findById(b.getOrder().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
        TrangThai tt = trangThaiRepository.findById(9)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái phù hợp"));
        ThoiDiemTrangThai t = new ThoiDiemTrangThai(nv, b.getOrder(), tt);
        b.setTrangThai(2);
        phanCongRepository.save(b);
        d.setTrangThai(tt);
        thoiDiemTrangThaiRepository.save(t);
        orderRepository.save(d);
        return new ResponseEntity<>(new ObjectRespone("successfully", null), HttpStatus.OK);
    }

    @PostMapping("/shipper/lotrinh")
    public ResponseEntity<Object> loTrinh(@RequestBody ToaDoDTO t) throws Exception {
        ToaDo tt = new ToaDo(0, 0);
        Map<Object, Object> map = tt.TSPKhoangCach(t);
        return new ResponseEntity<>(new ObjectRespone("successfully", map), HttpStatus.OK);
    }

    @GetMapping("/shipper/ordersend/status")
    public ResponseEntity<Object> getAllOrderChoNhan(@RequestParam("status") Integer status) {
        List<DonHang> orders = new ArrayList<>();
        orders = orderRepository.getAllOrderOfShipperSend(shipperBean.getNhanVien().getId(), status);
        return new ResponseEntity<>(new ObjectRespone("success", orders), HttpStatus.OK);
    }

}