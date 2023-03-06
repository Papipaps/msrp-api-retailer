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
    public static byte[] generateQRcode(String data, String charset, int height, int width) throws WriterException, IOException {

        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", outputStream);

        return outputStream.toByteArray();
    }


}
