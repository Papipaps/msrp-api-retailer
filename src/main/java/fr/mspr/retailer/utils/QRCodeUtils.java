package fr.mspr.retailer.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRCodeUtils {
    // Generate QR code and return image data
    public static byte[] generateQRcode(String data, String charset, int height, int width) throws WriterException, IOException {

        // BitMatrix represents 2D bit matrix
        // MultiFormatWriter finds appropriate Writer subclass and encodes barcode with supplied contents
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, width, height);

        // ByteArrayOutputStream implements output stream in which data is written to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // MatrixToImageWriter writes BitMatrix to BufferedImage
        // ImageIO reads and writes images to files or streams
        ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", outputStream);

        // Return image data as byte array
        return outputStream.toByteArray();
    }


}
