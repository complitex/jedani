package ru.complitex.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov
 * 30.01.2020 7:59 PM
 */
public class Images {
    public static BufferedImage scale(BufferedImage bufferedImage, int maxWidth, int maxHeight) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int newWidth = width;
        int newHeight = height;

        if (width > maxWidth) {
            newWidth = maxWidth;
            newHeight = (newWidth * height) / width;
        }

        if (newHeight > maxHeight) {
            newHeight = maxHeight;
            newWidth = (newHeight * width) / height;
        }

        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, bufferedImage.getType());
        
        Graphics2D g = scaledImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);
        
        g.dispose();
        
        return scaledImage;
    }

    public static void write(InputStream inputStream, int maxWidth, int maxHeight, String formatName, File file) throws IOException {
        ImageIO.write(scale(ImageIO.read(inputStream), maxWidth, maxHeight), formatName, file);
    }
}
