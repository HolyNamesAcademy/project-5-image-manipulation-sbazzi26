import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class represents an image and provides operations on top of an image such
 * as creating a new image, getting/setting a pixel, and drawing the image.
 *
 * Under the hood, this class is a wrapper class for the BufferedImage class.
 * That means that when this class's methods are called, the class simply calls
 * BufferedImage to do the actual work. We have this class to do some of the extra
 * work that needs to be done before calling BufferedImage, such as bit-shifting.
 */
public class Img extends JPanel {
    private static BufferedImage image;

    // Constructors

    /**
     * Creates an Img object from the image at the path specified
     * @param imageFilePath path to image
     * @throws IOException
     */
    public Img(String imageFilePath) throws IOException {
        image = ImageIO.read(new File(imageFilePath));
    }

    /**
     * Creates an empty image object with the size given by the params
     * @param xWidth width of the image
     * @param yWidth height of the image
     */
    public Img(int xWidth, int yWidth) {
        image = new BufferedImage(xWidth, yWidth, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Gets the pixel at the given (x, y) coordinates
     * @param xVal x coordinate
     * @param yVal y coordinate
     * @return RGB representation of the specified pixel
     */
    public RGB GetRGB(int xVal, int yVal) {
        int rgb = image.getRGB(xVal, yVal);
        int red = (rgb >> 16) & 0x000000FF;
        int green = (rgb >> 8) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;
        return new RGB(red, green, blue);
    }

    /**
     * Sets the RGB values at the given (x, y) coordinates
     * @param xVal x coordinate
     * @param yVal y coordinate
     * @param rgb RGB value to set
     */
    public void SetRGB(int xVal, int yVal, RGB rgb) {
        int rgbVal = ((rgb.GetRed() & 0x000000FF) << 16)
                | ((rgb.GetGreen() & 0x000000FF) << 8)
                | ((rgb.GetBlue() & 0x000000FF));
        image.setRGB(xVal, yVal, rgbVal);

    }

    /**
     * Get width of the image
     * @return width of the image
     */
    public int GetWidth() {
        return image.getWidth();
    }

    /**
     * Get height of the image
     * @return height of the image
     */
    public int GetHeight() {
        return image.getHeight();
    }

    /**
     * Saves the image to given file path
     * @param format format in which to save the image (ex. "jpg")
     * @param savePath path to save the image to
     * @throws IOException
     */
    public void Save(String format, String savePath) throws IOException {
        ImageIO.write(image, format, new File(savePath));
    }

    /**
     * Returns the width of the image if the height was 800
     * @return scaled width of image
     */
    public int GetScaledWidth() { return (int) (800 * GetWidth() / (double) GetHeight());}

    /**
     * Returns the scaled height of the image (800)
     * @return 800
     */
    public int GetScaledHeight() { return 800; }

    /**
     * Draws the image
     * @param g
     */
    public void paint(Graphics g) {
        g.drawImage(image.getScaledInstance(GetScaledWidth(), GetScaledHeight(), java.awt.Image.SCALE_DEFAULT), 0, 0, this);
    }
}
