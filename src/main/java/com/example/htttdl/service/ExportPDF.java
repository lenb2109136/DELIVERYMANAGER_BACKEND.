package com.example.htttdl.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.PhieuChuyenGiao;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
public class ExportPDF {

    private static GenderService genderService = new GenderService();

    private static Font customFont;
    private static Font customBoldFont;

    static {
        try {
            // Đăng ký font tùy chỉnh từ resources/Fonts/a.ttf
            String fontPath = new ClassPathResource("Fonts/Roboto-ExtraLight.ttf").getFile().getAbsolutePath();
            FontFactory.register(fontPath, "customFont");

            // Tạo font sử dụng
            customFont = FontFactory.getFont("customFont", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
            customBoldFont = FontFactory.getFont("customFont", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] createPdf(String message, List<DonHang> orders) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            String imagePath = "src/main/resources/images/logo.png";
            Image image = Image.getInstance(imagePath);
            image.scaleToFit(200, 200);

            boolean firstOrder = true; // Biến kiểm tra đơn hàng đầu tiên

            for (DonHang o : orders) {
                if (!firstOrder) { // Nếu không phải đơn hàng đầu tiên thì tạo trang mới
                    document.newPage();
                }
                firstOrder = false; // Đánh dấu là đã qua đơn hàng đầu tiên

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);

                PdfPCell textCell = new PdfPCell();
                textCell.setBorder(PdfPCell.NO_BORDER);
                textCell.addElement(
                        new Paragraph("Từ Bưu cục: " + o.getDiemNhanHang().getDiachichitiet(), customBoldFont));
                textCell.addElement(new Paragraph("Tên người nhận: " + o.getTenNguoiNhan(), customBoldFont));
                textCell.addElement(new Paragraph("Số điện thoại: " + o.getSDTnguoiNhan(), customBoldFont));
                textCell.addElement(new Paragraph("Địa chỉ chi tiết: " + o.getDiaChiChiTiet(), customBoldFont));

                byte[] barcodeBytes = genderService.generateQRCodeAsBytes(o.getId());
                Image barcodeImage = Image.getInstance(barcodeBytes);
                barcodeImage.scaleToFit(150, 50);
                textCell.addElement(barcodeImage);

                table.addCell(textCell);

                PdfPCell imageCell = new PdfPCell();
                imageCell.setBorder(PdfPCell.NO_BORDER);
                imageCell.addElement(image);
                table.addCell(imageCell);

                document.add(table);
                document.add(new Paragraph(" "));

                PdfPTable detailTable = new PdfPTable(2);
                detailTable.setWidthPercentage(100);
                detailTable.addCell(new PdfPCell(new Phrase("Thông tin sản phẩm", customBoldFont)));
                detailTable.addCell(new PdfPCell(new Phrase("Chi tiết", customBoldFont)));

                detailTable.addCell(new Phrase("Hình thức vận chuyển", customFont));
                detailTable.addCell(new Phrase(o.getHinhThucVanChuyen().getTenHinhThuc(), customFont));

                detailTable.addCell(new Phrase("Loại hàng", customFont));
                detailTable.addCell(new Phrase(o.getLoaiHang().getTen(), customFont));

                detailTable.addCell(new Phrase("Trọng lượng", customFont));
                detailTable.addCell(new Phrase(o.getHinhThucVanChuyen().getTenHinhThuc(), customFont));

                document.add(detailTable);
                document.add(new Paragraph("Địa chỉ giao hàng:", customBoldFont));
                document.add(new Paragraph(o.getDiaChiChiTiet(), customFont));

                document.add(new Paragraph("Phí dịch : " + o.getFee(), customBoldFont));
                document.add(new Paragraph("Tổng cộng: " + " (Đã bao gồm voucher và giảm giá)", customBoldFont));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    public static byte[] createPdfPhieuChuyenGiao(String message, List<PhieuChuyenGiao> phieuChuyenGiaos)
            throws Exception {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            String imagePath = new ClassPathResource("images/logo.png").getFile().getAbsolutePath();
            Image image = Image.getInstance(imagePath);
            image.scaleToFit(200, 200);

            for (PhieuChuyenGiao o : phieuChuyenGiaos) {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);

                PdfPCell textCell = new PdfPCell();
                textCell.setBorder(PdfPCell.NO_BORDER);
                textCell.addElement(new Paragraph("Từ Bưu cục: " + o.getDiemNhanHang().getId(), customBoldFont));
                textCell.addElement(
                        new Paragraph("Địa chỉ chi tiết: " + o.getOrders().get(0).getDiemNhanHang().getDiachichitiet(),
                                customBoldFont));
                byte[] barcodeBytes = genderService.generateQRCodeAsBytes(o.getId());
                Image barcodeImage = Image.getInstance(barcodeBytes);
                barcodeImage.scaleToFit(150, 50);
                textCell.addElement(barcodeImage);

                textCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(textCell);

                PdfPCell imageCell = new PdfPCell();
                imageCell.setBorder(PdfPCell.NO_BORDER);
                imageCell.addElement(image);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(imageCell);

                document.add(table);
                document.add(new Paragraph(" "));

                PdfPTable detailTable = new PdfPTable(2);
                detailTable.setWidthPercentage(100);

                PdfPCell cell1 = new PdfPCell(new Phrase("Order Info", customBoldFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell cell2 = new PdfPCell(new Phrase("Detail", customBoldFont));
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                detailTable.addCell(cell1);
                detailTable.addCell(cell2);

                for (DonHang order : o.getOrders()) {
                    detailTable.addCell(
                            new Phrase("Id đơn" + order.getId() + " - " + order.getSDTnguoiNhan(), customFont));
                    detailTable.addCell(new Phrase(order.getDiaChiChiTiet(), customFont));
                }
                document.add(detailTable);

                document.add(
                        new Paragraph("Từ điểm nhận hàng: " + o.getOrders().get(0).getDiemNhanHang().getDiachichitiet(),
                                customBoldFont));
                document.add(new Paragraph("Số điện thoại liên hệ: " + o.getOrders().get(0).getDiemNhanHang().getSdt(),
                        customBoldFont));

                document.add(new Paragraph(
                        "---------------------------------------------------------------------------------------------------------------------"));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        return outputStream.toByteArray();
    }
}
