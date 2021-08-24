package edu.uchicago.nmaheshwari.vaadin.security;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

//https://springboot.io/t/topic/78
public class AvatarHelper {

    // Base64 encoding header that can be previewed directly in the <img/> tag or browser address bar
    public static final String BASE64_PREFIX = "data:image/png;base64,";

    public static String createBase64Avatar(int hash) throws IOException {
        return new String(Base64.getEncoder().encode(create(hash)));
    }


    public static byte[] create(int hash) throws IOException {
        int width = 20;
        int grid = 5;
        int padding = width / 2;
        int size = width * grid + width;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D _2d = img.createGraphics();
        _2d.setColor(new Color(35, 51, 72));
        _2d.fillRect(0, 0, size, size);
        _2d.setColor(deterministicColor(hash));
        char[] idchars = createIdent(hash);
        int i = idchars.length;
        for (int x = 0; x < Math.ceil(grid / 2.0); x++) {
            for (int y = 0; y < grid; y++) {
                if (idchars[--i] < 53) {
                    _2d.fillRect((padding + x * width), (padding + y * width), width, width);
                    if (x < Math.floor(grid / 2)) {
                        _2d.fillRect((padding + ((grid - 1) - x) * width), (padding + y * width), width, width);
                    }
                }
            }
        }
        _2d.dispose();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private static Color deterministicColor(int hash) {

        //define some prime numbers for red, green, blue that are close to but not greater than 255
        int redPrime = 239;
        int greenPrime = 241;
        int bluePrime = 251;

        int r = hash % redPrime;
        int g = hash % greenPrime;
        int b = hash % bluePrime;

        return new Color(r, g, b);
    }

    private static char[] createIdent(int hash) {
        BigInteger bi_content = new BigInteger((hash + "").getBytes());
        BigInteger bi = new BigInteger(hash + "identicon" + hash, 36);
        bi = bi.xor(bi_content);
        return bi.toString(10).toCharArray();
    }
}