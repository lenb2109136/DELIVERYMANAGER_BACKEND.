package com.example.htttdl.controller.Shipper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class PDFController {

    @GetMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePDF() throws IOException {
        // Tạo tài liệu PDF
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // Load font từ thư mục resources
        InputStream fontStream = getClass().getClassLoader().getResourceAsStream("Fonts/Roboto-ExtraLight.ttf");
        if (fontStream == null) {
            throw new IOException("Không tìm thấy file font trong resources/Fonts");
        }
        PDType0Font font = PDType0Font.load(document, fontStream);

        // Thiết lập ContentStream để vẽ lên trang
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Bắt đầu viết văn bản vào PDF
        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Hello, đây là PDF có tiếng Việt: â, ấ, ệ, ợ, ừ, ả, ạ!");
        contentStream.endText();

        // Đóng content stream
        contentStream.close();

        // Chuyển tài liệu PDF thành byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        byte[] pdfBytes = outputStream.toByteArray();

        // Thiết lập headers để trả về file PDF
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/pdf");
        headers.add("Content-Disposition", "inline; filename=generated.pdf");

        // Trả về file PDF dưới dạng byte array
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
