package com.example.htttdl.controller.Admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.htttdl.DTO.Customer.Admin.PhieuChuyenGiaoDTO;
import com.example.htttdl.FirebaseCrud.Notification;
import com.example.htttdl.config.AminBean;
import com.example.htttdl.modal.DiemNhanHang;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.ThoiDiemTrangThai;
import com.example.htttdl.modal.TrangThai;
import com.example.htttdl.repository.OrderRepository;
import com.example.htttdl.repository.PhieuChuyenGiaoRepository;
import com.example.htttdl.repository.ThoiDiemTrangThaiRepository;
import com.example.htttdl.response.ObjectRespone;
import com.example.htttdl.service.ExportPDF;

import jakarta.transaction.Transactional;

@RestController

public class PhieuChuyenGiao {

        @Autowired
        PhieuChuyenGiaoRepository phieuChuyenGiaoRepo;

        @Autowired
        ThoiDiemTrangThaiRepository thoiDiemTrangThaiRepo;

        @Autowired
        OrderRepository orderRepo;

        @Autowired
        AminBean a;

        @Transactional
        @PostMapping("/admin/phieuchuyengiao/xacnhap")
        public ResponseEntity<Object> xacNhanPhieu(@RequestBody List<Integer> ids) {
                List<com.example.htttdl.modal.PhieuChuyenGiao> phieuChuyenGiaos = phieuChuyenGiaoRepo
                                .findByDiemNhanHangInList(ids, a.getBuuCucId());
                if (phieuChuyenGiaos.size() == ids.size()
                                && phieuChuyenGiaoRepo.countCanNhapPhieuChuyenGiao(ids) == 0) {
                        List<DonHang> orders = new ArrayList<>();
                        List<ThoiDiemTrangThai> thoiDiemTrangThais = new ArrayList<>();
                        phieuChuyenGiaos.forEach(v -> {
                                v.setTrangThai(2);
                                Notification.pushNotifycationBuuCuc(v.getOrders().get(0).getDiemNhanHang().getId(),
                                                "PHIEUCHUYENGIAO",
                                                "Phiếu chuyển giao có id " + v.getId() + " đã được nhập kho",
                                                v.getId() + "", "2025-03-04");
                                v.getOrders().forEach(vv -> {
                                        vv.setTrangThai(new TrangThai(5));
                                        vv.setDiemNhanHang(new DiemNhanHang(a.getBuuCucId()));
                                        orders.add(vv);
                                        thoiDiemTrangThais.add(
                                                        new ThoiDiemTrangThai(a.getNhanVien(), vv, new TrangThai(5),
                                                                        new DiemNhanHang(a.getBuuCucId())));
                                        vv.setLatSend(55.4f);
                                        vv.setLongSend(4.5f);
                                        vv.setDiaChiNguoiGui("dcjdoijcudhcuhdchuduh");
                                });
                        });
                        orderRepo.saveAll(orders);
                        phieuChuyenGiaoRepo.saveAll(phieuChuyenGiaos);
                        thoiDiemTrangThaiRepo.saveAll(thoiDiemTrangThais);
                        return new ResponseEntity<>(
                                        new ObjectRespone("Danh sách phiếu chuyển giao có vẻ không hợp lệ", null),
                                        HttpStatus.OK);
                }
                return new ResponseEntity<>(
                                new ObjectRespone("Danh sách phiếu chuyển giao có vẻ không hợpdcidihcbdbh lệ", null),
                                HttpStatus.BAD_REQUEST);
        }

        @GetMapping("/admin/phieuchuyengiao/findbyid")
        public ResponseEntity<Object> getPhieuChuyenGiaoById(
                        @RequestParam(name = "phieuChuyenGiaoId", defaultValue = "-1") Integer phieuChuyenGiaoId) {
                System.out.println("Điêm nhận hàng " + a.getBuuCucId());
                System.out.println("Phieeuc chuyển giao id lf: " + phieuChuyenGiaoId);
                com.example.htttdl.modal.PhieuChuyenGiao phieuChuyenGiao = phieuChuyenGiaoRepo
                                .findByDiemNhanHangId(phieuChuyenGiaoId, a.getBuuCucId()).orElse(null);
                System.out.println(phieuChuyenGiao != null);
                if (phieuChuyenGiao != null && phieuChuyenGiao.getTrangThai() == 1) {
                        return new ResponseEntity<>(new ObjectRespone("success", phieuChuyenGiao),
                                        HttpStatus.OK);
                }
                return new ResponseEntity<>(
                                new ObjectRespone("Không tìm thấy phiếu chuyển giao đến bưu cục của bạn", null),
                                HttpStatus.BAD_REQUEST);
        }

        @PostMapping("admin/phieuchuyengiao/dangchuyen/getall")
        public ResponseEntity<Object> findAllPhieuChuyenGiaoDangChuyen(@RequestBody List<Integer> ids) {
                List<DonHang> orders = new ArrayList<>();
                if (ids != null && ids.size() > 0) {
                        orders = phieuChuyenGiaoRepo.findAllPhieuChuyenGiaoOfBuuCucDangChuyen(ids, a.getBuuCucId());

                } else {
                        orders = phieuChuyenGiaoRepo.findAllPhieuChuyenGiaoOfBuuCucDangChuyen(a.getBuuCucId());
                }
                List<PhieuChuyenGiaoDTO> phieuChuyenGiaos = orders.stream()
                                .filter(dh -> dh.getPhieuChuyenGiao() != null) // Lọc đơn có phiếu chuyển giao
                                .collect(Collectors.groupingBy(DonHang::getPhieuChuyenGiao)) // Nhóm theo
                                                                                             // phieuChuyenGiao
                                .entrySet().stream()
                                .map(entry -> new PhieuChuyenGiaoDTO(entry.getValue().get(0).getPhieuChuyenGiao(),
                                                entry.getValue()))
                                .collect(Collectors.toList());
                return new ResponseEntity<>(
                                new ObjectRespone("success", phieuChuyenGiaos),
                                HttpStatus.OK);
        }

        @PostMapping("admin/phieuchuyengiao/getall")
        public ResponseEntity<Object> findAllPhieuChuyenGiao(@RequestBody List<Integer> ids) {
                List<DonHang> orders = new ArrayList<>();
                if (ids != null && ids.size() > 0) {
                        orders = phieuChuyenGiaoRepo.findAllPhieuChuyenGiaoOfBuuCuc(ids, a.getBuuCucId());

                } else {
                        orders = phieuChuyenGiaoRepo.findAllPhieuChuyenGiaoOfBuuCuc(a.getBuuCucId());
                }
                List<PhieuChuyenGiaoDTO> phieuChuyenGiaos = orders.stream()
                                .filter(dh -> dh.getPhieuChuyenGiao() != null)
                                .collect(Collectors.groupingBy(DonHang::getPhieuChuyenGiao))
                                .entrySet().stream()
                                .map(entry -> new PhieuChuyenGiaoDTO(entry.getValue().get(0).getPhieuChuyenGiao(),
                                                entry.getValue()))
                                .collect(Collectors.toList());
                return new ResponseEntity<>(
                                new ObjectRespone("success", phieuChuyenGiaos),
                                HttpStatus.OK);
        }

        @Transactional
        @PostMapping("/admin/phieuchuyengiao/chuyentiepdi")
        public ResponseEntity<Object> chuyenTiepDi(
                        @RequestParam(name = "phieuChuyenGiaoId", defaultValue = "-1") Integer phieuChuyenGiaoId) {
                List<DonHang> orders = phieuChuyenGiaoRepo.getCanChuyenTiepDi(phieuChuyenGiaoId, a.getBuuCucId());

                if (orders != null && orders.size() > 0) {
                        System.out.println("Hello bạn hiền ");
                        TrangThai trangThai = new TrangThai(8);
                        List<ThoiDiemTrangThai> thoiDiemTrangThai = new ArrayList<>();
                        orders.forEach(v -> {
                                v.setTrangThai(trangThai);
                                ThoiDiemTrangThai t = new ThoiDiemTrangThai(a.getNhanVien(), v, trangThai,
                                                new DiemNhanHang(a.getBuuCucId()));
                                thoiDiemTrangThai.add(t);
                        });
                        phieuChuyenGiaoRepo.updateTrangThaiById(phieuChuyenGiaoId, 1);

                        orderRepo.saveAll(orders);
                        thoiDiemTrangThaiRepo.saveAll(thoiDiemTrangThai);
                        return new ResponseEntity<>(new ObjectRespone("Phiếu Chuyển giao yêu cầu khoongg hợp lệ", null),
                                        HttpStatus.OK);
                }
                return new ResponseEntity<>(new ObjectRespone("Phiếu Chuyển giao yêu cầu khoongg hợp lệ", orders),
                                HttpStatus.BAD_REQUEST);
        }

        @PostMapping("/admin/phieuchuyengiao/exportpdf")
        public ResponseEntity<Object> exportPDFPhieuChuyenGiaos(@RequestBody List<Integer> phieuChuyenGiaos)
                        throws Exception {
                List<com.example.htttdl.modal.PhieuChuyenGiao> phieuChuyenGiao = phieuChuyenGiaoRepo
                                .findPhieuChuyenGiaoOfDiemNhanHangInList(phieuChuyenGiaos, 1);
                if (phieuChuyenGiao.size() == phieuChuyenGiaos.size()) {
                        byte[] pdfData = ExportPDF.createPdfPhieuChuyenGiao("null", phieuChuyenGiao);
                        return ResponseEntity.ok()
                                        .contentType(MediaType.APPLICATION_PDF)
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"shipping_label.pdf\"")
                                        .body(pdfData);
                }
                return new ResponseEntity<>(new ObjectRespone("Danh sách phiếu chuyển giao không hợp lệ", null),
                                HttpStatus.BAD_REQUEST);
        }

}
