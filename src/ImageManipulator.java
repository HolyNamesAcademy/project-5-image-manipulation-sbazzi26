import java.io.IOException;
import java.util.ArrayList;

/**
 * Static utility class that is responsible for transforming the images.
 * Each function (or at least most functions) take in an Image and return
 * a transformed image.
 */
public class ImageManipulator {
    /**
     * Loads the image at the given path
     * @param path path to image to load
     * @return an Img object that has the given image loaded
     * @throws IOException
     */
    public static Img LoadImage(String path) throws IOException {
        Img image = new Img(path);
        return image;
    }

    /**
     * Saves the image to the given file location
     * @param image image to save
     * @param path location in file system to save the image
     * @throws IOException
     */
    public static void SaveImage(Img image, String path) throws IOException {
        image.Save(path.substring(path.length()-3),path);
    }

    /**
     * Converts the given image to grayscale (black, white, and gray). This is done
     * by finding the average of the RGB channel values of each pixel and setting
     * each channel to the average value.
     * @param image image to transform
     * @return the image transformed to grayscale
     */
    public static Img ConvertToGrayScale(Img image) {
        for(int i = 0; i < image.GetHeight(); i++){
            for(int j = 0; j < image.GetWidth(); j++){
                RGB vals = image.GetRGB(j,i);
                int avg = (vals.GetBlue() + vals.GetGreen() + vals.GetRed())/3;
                vals.SetGreen(avg);
                vals.SetBlue(avg);
                vals.SetRed(avg);
                image.SetRGB(j, i, vals);
            }
        }
        return image;
    }

    /**
     * Inverts the image. To invert the image, for each channel of each pixel, we get
     * its new value by subtracting its current value from 255. (r = 255 - r)
     * @param image image to transform
     * @return image transformed to inverted image
     */
    public static Img InvertImage(Img image) {
        for(int i = 0; i < image.GetHeight(); i++){
            for(int j = 0; j < image.GetWidth(); j++){
                RGB vals = image.GetRGB(j,i);
                vals.SetBlue(225-vals.GetBlue());
                vals.SetRed(225-vals.GetRed());
                vals.SetGreen(225- vals.GetGreen());
                image.SetRGB(j,i,vals);
            }
        }
        return image;
    }

    /**
     * Converts the image to sepia. To do so, for each pixel, we use the following equations
     * to get the new channel values:
     * r = .393r + .769g + .189b
     * g = .349r + .686g + .168b
     * b = 272r + .534g + .131b
     * @param image image to transform
     * @return image transformed to sepia
     */
    public static Img ConvertToSepia(Img image) {
        for(int i = 0; i < image.GetHeight(); i++){
            for(int j = 0; j < image.GetWidth(); j++){
                RGB vals = image.GetRGB(j,i);
                int r = vals.GetRed();
                int g = vals.GetGreen();
                int b = vals.GetBlue();
                vals.SetBlue((int)(272*r + .534*g + .131*b));
                vals.SetRed((int)(.393*r + .769*g + .189*b));
                vals.SetGreen((int)(349*r + .686*g + .168*b));
                image.SetRGB(j,i,vals);
            }
        }
        return image;
    }

    /**
     * Creates a stylized Black/White image (no gray) from the given image. To do so:
     * 1) calculate the luminance for each pixel. Luminance = (.299 r^2 + .587 g^2 + .114 b^2)^(1/2)
     * 2) find the median luminance
     * 3) each pixel that has luminance >= median_luminance will be white changed to white and each pixel
     *      that has luminance < median_luminance will be changed to black
     * @param image image to transform
     * @return black/white stylized form of image
     */
    public static Img ConvertToBW(Img image) {
        Img imagE = image;
        ArrayList<Double> luminance = new ArrayList<>();
        RGB white = new RGB(225,225,225);
        RGB black = new RGB(0,0,0);
        for(int i = 0; i < imagE.GetHeight(); i++){
            for(int j = 0; j < imagE.GetWidth(); j++){
                RGB vals = imagE.GetRGB(j,i);
                int r = vals.GetRed();
                int g = vals.GetGreen();
                int b = vals.GetBlue();
                double lum = (Math.sqrt(.299*r*r + .587*g*g + .114*b*b));
                luminance.add(lum);
            }
        }
        Double lumi = 0.0;
        int pos = 0;
        for (int i = 0; i < luminance.size(); i++){
            lumi = luminance.get(i);
            pos= i;
            while(0<pos && lumi.compareTo(luminance.get(pos-1))>0){
                luminance.set(pos, luminance.get(pos-1));
                pos--;
            }
            luminance.set(pos, lumi);
        }
        double medianLuminance = luminance.get(luminance.size()/2);
        for(int i = 0; i < imagE.GetHeight(); i++){
            for(int j = 0; j < imagE.GetWidth(); j++){
                RGB vals = imagE.GetRGB(j,i);
                int r = vals.GetRed();
                int g = vals.GetGreen();
                int b = vals.GetBlue();
                double lum = (Math.sqrt(.299*r*r + .587*g*g + .114*b*b));
                if(lum < medianLuminance){
                    imagE.SetRGB(j,i,white);
                }
                else{
                    imagE.SetRGB(j,i,black);
                }
            }
        }
        return imagE;
    }

    /**
     * Rotates the image 90 degrees clockwise.
     * @param image image to transform
     * @return image rotated 90 degrees clockwise
     */
    public  static Img RotateImage(Img image) {
        Img rotatedImage = new Img(image.GetHeight(), image.GetWidth());
        for(int i = 0; i < image.GetHeight(); i++){
            for(int j = 0; j < image.GetWidth(); j++){
                RGB vals = image.GetRGB(j,i);
                int newJ = i;
                int newI = image.GetWidth() - j - 1;
                rotatedImage.SetRGB(newJ,newI,vals);
            }
        }
        return rotatedImage;
    }

    /**
     * Applies an Instagram-like filter to the image. To do so, we apply the following transformations:
     * 1) We apply a "warm" filter. We can produce warm colors by reducing the amount of blue in the image
     *      and increasing the amount of red. For each pixel, apply the following transformation:
     *          r = r * 1.2
     *          g = g
     *          b = b / 1.5
     * 2) We add a vignette (a black gradient around the border) by combining our image with an
     *      an image of a halo (you can see the image at resources/halo.png). We take 65% of our
     *      image and 35% of the halo image. For example:
     *          r = .65 * r_image + .35 * r_halo
     * 3) We add decorative grain by combining our image with a decorative grain image
     *      (resources/decorative_grain.png). We will do this at a .95 / .5 ratio.
     * @param image image to transform
     * @return image with a filter
     * @throws IOException
     */
    public static Img InstagramFilter(Img image) throws IOException {
        for(int i = 0; i < image.GetHeight(); i++){
            for(int j = 0; j < image.GetWidth(); j++){
                RGB vals = image.GetRGB(j,i);
                int r = vals.GetRed();
                int b = vals.GetBlue();
                vals.SetRed((int)(r*1.2));
                vals.SetBlue((int)(b/1.5));
                image.SetRGB(j,i,vals);
            }
        }
        return image;
    }

    /**
     * Sets the given hue to each pixel image. Hue can range from 0 to 360. We do this
     * by converting each RGB pixel to an HSL pixel, Setting the new hue, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param hue amount of hue to add
     * @return image with added hue
     */
    public static Img SetHue(Img image, int hue) {
        for(int i = 0; i < image.GetHeight(); i++){
            for(int j = 0; j < image.GetWidth(); j++){
                HSL hPixel = image.GetRGB(j,i).ConvertToHSL();
                hPixel.SetHue(hue);
                RGB rPixel = hPixel.GetRGB();
                image.SetRGB(j,i,rPixel);
            }
        }
        return image;
    }

    /**
     * Sets the given saturation to the image. Saturation can range from 0 to 1. We do this
     * by converting each RGB pixel to an HSL pixel, setting the new saturation, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param saturation amount of saturation to add
     * @return image with added hue
     */
    public static Img SetSaturation(Img image, double saturation) {
        for(int i = 0; i < image.GetHeight(); i++){
            for(int j = 0; j < image.GetWidth(); j++){
                HSL hPixel = image.GetRGB(j,i).ConvertToHSL();
                hPixel.SetSaturation(saturation);
                RGB rPixel = hPixel.GetRGB();
                image.SetRGB(j,i,rPixel);
            }
        }
        return image;
    }

    /**
     * Sets the lightness to the image. Lightness can range from 0 to 1. We do this
     * by converting each RGB pixel to an HSL pixel, setting the new lightness, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param lightness amount of hue to add
     * @return image with added hue
     */
    public static Img SetLightness(Img image, double lightness) {
        for(int i = 0; i < image.GetHeight(); i++){
            for(int j = 0; j < image.GetWidth(); j++){
                HSL hPixel = image.GetRGB(j,i).ConvertToHSL();
                hPixel.SetLightness(lightness);
                RGB rPixel = hPixel.GetRGB();
                image.SetRGB(j,i,rPixel);
            }
        }
        return image;
    }
}
