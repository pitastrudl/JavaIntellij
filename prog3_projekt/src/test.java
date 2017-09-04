import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class test {

    public static void main(String[] args) {

            int[] mat = new int[]{-1, -1, -1, -1, 8, -1, -1, -1, -1};
            int div = 1;
            BufferedImage img = null;

            try {
                //provide nxn image and absolute path to the image
                img = ImageIO.read(new File("/home/arun/doggo.jpg"));
            } catch (IOException e) {

            }

            BufferedImage newimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

            //the line that breaks the code
            newimg = img;

            Color[] temparray = new Color[9];

            int redsum = 0;
            int greensum = 0;
            int bluesum = 0;

            for (int i = 1; i < img.getHeight() - 1; i++) {

                for (int j = 1; j < img.getWidth() - 1; j++) {

                    temparray[0] = new Color(img.getRGB(i - 1, j - 1));
                    temparray[1] = new Color(img.getRGB(i - 1, j));
                    temparray[2] = new Color(img.getRGB(i - 1, j + 1));

                    temparray[3] = new Color(img.getRGB(i, j - 1));
                    temparray[4] = new Color(img.getRGB(i, j));
                    temparray[5] = new Color(img.getRGB(i, j + 1));

                    temparray[6] = new Color(img.getRGB(i + 1, j - 1));
                    temparray[7] = new Color(img.getRGB(i + 1, j));
                    temparray[8] = new Color(img.getRGB(i + 1, j + 1));

                    for (int k = 0; k < temparray.length; k++) {

                        redsum += temparray[k].getRed() * mat[k];
                        greensum += temparray[k].getGreen() * mat[k];
                        bluesum += temparray[k].getBlue() * mat[k];

                    }

                    redsum = redsum / div;
                    greensum = greensum / div;
                    bluesum = bluesum / div;

                    if (redsum > 255) {
                        redsum = 255;
                    } else if (redsum < 0) {
                        redsum = 0;
                    }

                    if (greensum > 255) {
                        greensum = 255;
                    } else if (greensum < 0) {
                        greensum = 0;

                    }

                    if (bluesum > 255) {
                        bluesum = 255;
                    } else if (bluesum < 0) {
                        bluesum = 0;
                    }

                    Color newpixel = new Color(redsum, greensum, bluesum);

                    newimg.setRGB(i, j, newpixel.getRGB());

                    redsum = 0;
                    greensum = 0;
                    bluesum = 0;

                }
            }

            File outputfile = new File("/home/arun/convulated");
            outputfile.getParentFile().mkdirs();

            try {
                ImageIO.write(newimg, "png", outputfile);

            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
