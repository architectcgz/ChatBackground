package com.example.chatplatform.util;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class QRCodeUtils {
    public static String generateQRCodeBase64(String text, int width, int height) throws WriterException, IOException {
        //返回Base64编码后的结果
        return Base64.getEncoder().encodeToString(generateQRCodeByte(text, width, height));
    }
    public static byte[] generateQRCodeByte(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        //返回Base64编码后的结果
        return pngOutputStream.toByteArray();
    }

    public static String generateQRCodeBase64(String text, byte[] avatar, int width, int height) throws WriterException, IOException {
        byte[] result = generateQRCodeByte(text, avatar, width, height);
        // 返回Base64编码后的结果
        return Base64.getEncoder().encodeToString(result);
    }
    public static byte[] generateQRCodeByte(String text, byte[] avatar, int width, int height) throws WriterException, IOException {
        // 生成高容错级别的二维码
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 设置容错级别为H
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        // 创建二维码图像
        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = qrImage.createGraphics();

        // 设置二维码的背景为白色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        // 绘制二维码的前景为黑色
        graphics.setColor(Color.BLACK);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }

        // 读取头像图像
        ByteArrayInputStream bais = new ByteArrayInputStream(avatar);
        BufferedImage avatarImage = ImageIO.read(bais);

        // 计算头像大小，设置为二维码宽度的六分之一
        int avatarSize = Math.min(width, height) / 6;
        int avatarX = (width - avatarSize) / 2;
        int avatarY = (height - avatarSize) / 2;

        // 为头像添加白色边框
        int borderSize = avatarSize / 10;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(avatarX - borderSize, avatarY - borderSize, avatarSize + 2 * borderSize, avatarSize + 2 * borderSize);

        // 绘制头像到二维码图像，保持头像的原始颜色
        graphics.drawImage(avatarImage, avatarX, avatarY, avatarSize, avatarSize, null);

        // 释放图形上下文
        graphics.dispose();

        // 将二维码图像转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);

        return baos.toByteArray();
    }


}
