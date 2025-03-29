// package com.API.service;

// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.util.Iterator;
// import java.util.List;

// import org.springframework.stereotype.Service;

// import com.itextpdf.text.Document;
// import com.itextpdf.text.DocumentException;
// import com.itextpdf.text.Element;
// import com.itextpdf.text.Font;
// import com.itextpdf.text.FontFactory;
// import com.itextpdf.text.Image;
// import com.itextpdf.text.Paragraph;
// import com.itextpdf.text.Phrase;
// import com.itextpdf.text.pdf.PdfPCell;
// import com.itextpdf.text.pdf.PdfPTable;
// import com.itextpdf.text.pdf.PdfWriter;
// @Service
// public class PDFService {
// public byte[] createPdf(String message,List<Order>orders) throws IOException
// {
// Document document = new Document();
// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

// try {
// PdfWriter.getInstance(document, outputStream);
// document.open();

// Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
// Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
// String imagePath = "src/main/resources/images/logo.png";
// Image image = Image.getInstance(imagePath);
// image.scaleToFit(200, 200);

// orders.forEach(v->{

// });
// for (Order o:orders) {
// PdfPTable table = new PdfPTable(2);
// table.setWidthPercentage(100);

// PdfPCell textCell = new PdfPCell();
// textCell.setBorder(PdfPCell.NO_BORDER); // Không có viền
// textCell.addElement(new Paragraph("Mã DH: "+o.getId(), boldFont));
// textCell.addElement(new Paragraph("Tên khách hàng:
// "+o.getAccountId().getHoVaTen(), boldFont));
// textCell.addElement(new Paragraph("Ghi chú: "+o.getGhiChu(), boldFont));
// textCell.setHorizontalAlignment(Element.ALIGN_LEFT); // Căn trái cho văn bản
// textCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Căn giữa theo chiều
// dọc
// table.addCell(textCell);

// PdfPCell imageCell = new PdfPCell();
// imageCell.setBorder(PdfPCell.NO_BORDER); // Không có viền
// imageCell.addElement(image); // Thêm ảnh vào ô
// imageCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Căn giữa cho ảnh
// imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Căn giữa theo chiều
// dọc
// table.addCell(imageCell);

// // Thêm bảng vào tài liệu
// document.add(table);

// // Khoảng cách
// document.add(new Paragraph(" "));

// // Tạo bảng cho thông tin chi tiết đơn hàng
// PdfPTable detailTable = new PdfPTable(2); // 2 cột
// detailTable.setWidthPercentage(100); // Chiếm 100% chiều rộng

// // Header cho bảng
// PdfPCell cell1 = new PdfPCell(new Phrase("Thông tin sản phẩm", boldFont));
// cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
// PdfPCell cell2 = new PdfPCell(new Phrase("Chi tiết", boldFont));
// cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
// detailTable.addCell(cell1);
// detailTable.addCell(cell2);

// // Duyệt qua danh sách sản phẩm và thêm hàng vào bảng
// for (OrderDetail v : o.getOrderDetails()) {
// detailTable.addCell(new Phrase(v.getProduct().getProduct().getTenSanPham()+"(
// "+v.getProduct().getMauSac().getTenMau()+
// " - "+v.getProduct().getKichThuoc().getId(), normalFont));
// detailTable.addCell(new Phrase(v.getSoLuong()+" x "+v.getGiaBan(),
// normalFont));
// }

// // Thêm bảng vào document
// document.add(detailTable);

// // Chỉ dẫn giao hàng
// Paragraph deliveryInstructions = new Paragraph("Địa chỉ giao hàng:",
// boldFont);
// document.add(deliveryInstructions);

// Paragraph instructionsList = new Paragraph(
// "Tổ 4, Ngõ 5 - Phố Hàng Kiếm" +
// "- Đông Đa" +
// "- Hà Nội", normalFont);
// document.add(instructionsList);

// // Khoảng cách
// document.add(new Paragraph(" "));

// // Chữ ký người nhận
// Paragraph signatureLabel = new Paragraph("Tổng đơn: "+o.getTongTien(),
// boldFont);
// document.add(signatureLabel);

// Paragraph signatureLabel1 = new Paragraph("Phí ship: "+o.getPhiShip(),
// boldFont);
// document.add(signatureLabel1);

// Paragraph signatureLabel12 = new Paragraph("Tổng cộng: "+o.getTongTien()+"
// (Đã bao gồm voucher và giảm giá)", boldFont);
// document.add(signatureLabel12);

// Paragraph signatureNote = new Paragraph(message, normalFont);
// document.add(signatureNote);
// document.add(new Paragraph(" ")); // Khoảng cách giữa các phần
// Paragraph separator = new
// Paragraph("---------------------------------------------------------------------------------------------------------------------");
// document.add(separator);

// }

// } catch (DocumentException e) {
// e.printStackTrace();
// } finally {
// document.close();
// }

// return outputStream.toByteArray();
// }
// }
