package com.example.employee.service.attachment;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WatermarkService {

    public byte[] addImageWatermark(byte[] imageBytes, String watermarkText, String formatName) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) {
            return imageBytes;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage watermarked = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) watermarked.getGraphics();
        g2d.drawImage(image, 0, 0, null);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font font = new Font("SansSerif", Font.PLAIN, Math.max(14, width / 30));
        g2d.setFont(font);
        g2d.setColor(new Color(200, 200, 200, 120));

        FontMetrics fm = g2d.getFontMetrics();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String fullText = watermarkText + " | " + timestamp;

        int textWidth = fm.stringWidth(fullText);
        int textHeight = fm.getHeight();

        int diagonalCount = Math.max(3, (width + height) / (textWidth + 100));
        double angle = -Math.PI / 6;

        for (int i = 0; i < diagonalCount; i++) {
            for (int j = 0; j < diagonalCount; j++) {
                int x = (i * width / diagonalCount) - width / 2;
                int y = (j * height / diagonalCount) + textHeight;
                g2d.translate(x, y);
                g2d.rotate(angle);
                g2d.drawString(fullText, 0, 0);
                g2d.rotate(-angle);
                g2d.translate(-x, -y);
            }
        }

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(watermarked, formatName != null ? formatName : "png", baos);
        return baos.toByteArray();
    }

    public boolean isPreviewableImage(String mimeType) {
        return mimeType != null && (
                mimeType.equalsIgnoreCase("image/jpeg") ||
                        mimeType.equalsIgnoreCase("image/jpg") ||
                        mimeType.equalsIgnoreCase("image/png") ||
                        mimeType.equalsIgnoreCase("image/gif") ||
                        mimeType.equalsIgnoreCase("image/bmp") ||
                        mimeType.equalsIgnoreCase("image/webp")
        );
    }

    public boolean isPreviewable(String mimeType) {
        if (mimeType == null) return false;
        return isPreviewableImage(mimeType) ||
                mimeType.equalsIgnoreCase("application/pdf") ||
                mimeType.equalsIgnoreCase("text/plain") ||
                mimeType.startsWith("text/");
    }

    public String getImageFormatName(String mimeType) {
        if (mimeType == null) return "png";
        switch (mimeType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/bmp":
                return "bmp";
            default:
                return "png";
        }
    }
}
