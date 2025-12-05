package com.lostedin.authenticator.auth_service.model;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

public class QrUtil {

    /**
     * Generate a QR code PNG as bytes from the given content.
     * @param content text/URI to encode
     * @param size square size in pixels (min 64, max 2048)
     */
    public static byte[] generatePng(String content, int size) {
        BufferedImage image = generateImage(content, size);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            javax.imageio.ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode QR PNG", e);
        }
    }

    /**
     * Generate a QR BufferedImage from the given content.
     */
    public static BufferedImage generateImage(String content, int size) {
        BitMatrix matrix = encode(content, size);
        MatrixToImageConfig cfg = new MatrixToImageConfig();
        return MatrixToImageWriter.toBufferedImage(matrix, cfg);
    }

    /**
     * Encode content into a BitMatrix using ZXing with sensible defaults.
     */
    public static BitMatrix encode(String content, int size) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("content must not be empty");
        }
        int s = Math.max(64, Math.min(2048, size <= 0 ? 256 : size));
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        try {
            return new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, s, s, hints);
        } catch (WriterException e) {
            throw new IllegalStateException("Failed to generate QR matrix", e);
        }
    }
}

