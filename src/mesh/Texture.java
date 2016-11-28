package mesh;

import opengl.GLUtil;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by lowery on 11/7/2016.
 */
public class Texture {
    private static final String TEXTURE_DIR = "/textures/";

    private int width;
    private int height;
    private ByteBuffer buffer;

    public void loadImage(String filename) {
        try {
            BufferedImage image = ImageIO.read(GLUtil.class.getResourceAsStream(TEXTURE_DIR + filename));

            width = image.getWidth();
            height = image.getHeight();

            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, image.getWidth());

            buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 3); //4 for RGBA, 3 for RGB

            for(int y = 0; y < image.getHeight(); y++){
                for(int x = 0; x < image.getWidth(); x++){
                    int i = y * image.getWidth() + x;
                    int pixel = pixels[i];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    buffer.put((byte) (pixel & 0xFF));               // Blue component
                    //buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }

            buffer.flip();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getByteBuffer() {
        return buffer;
    }
}