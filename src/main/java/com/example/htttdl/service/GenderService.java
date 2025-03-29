package com.example.htttdl.service;

import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class GenderService {

    // ✅ Tạo mã Barcode (CODE_128) dưới dạng mảng byte
    public byte[] generateBarcodeAsBytes(Integer id) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(String.valueOf(id), BarcodeFormat.CODE_128, 300, 100);
        return convertBitMatrixToByteArray(bitMatrix);
    }

    // ✅ Tạo mã QR Code dưới dạng mảng byte
    public byte[] generateQRCodeAsBytes(Integer text) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(String.valueOf(text), BarcodeFormat.QR_CODE, 300, 300);
        return convertBitMatrixToByteArray(bitMatrix);
    }

    // ✅ Chuyển BitMatrix thành mảng byte (PNG)
    private byte[] convertBitMatrixToByteArray(BitMatrix bitMatrix) throws IOException {
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        return outputStream.toByteArray();
    }
}
