import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConvVzporednoThread extends Thread {
    static int[] mat = new int[]{-1, -1, -1, -1, 8, -1, -1, -1, -1}; //edge
    static int div = 1;

    public static void main(String[] args) {
        readImg();

        long start = System.currentTimeMillis();

        for (int i = 1; i < steviloNiti; i++) {
            polje[i] = new ConvVzporednoThread();
            polje[i].nitId = i;
        }

        for (int i = 1; i < polje.length; i++) {
            polje[i].start();
        }

        for (int i = 1; i < polje.length; i++) {
            try {
                polje[i].join();

            } catch (InterruptedException e) {
                System.out.println("uuuuups");
            }
        }
        long end = System.currentTimeMillis();

        System.out.println("vzporedno ms: " + (end - start));


    }


    static long runtime = 0;
    static BufferedImage img = null;
    static BufferedImage newimg = null;
    static ConvVzporednoThread[] polje = new ConvVzporednoThread[0];
    static int steviloNiti = 0;
    int nitId;

    public void run() {

        Color[] temparray = new Color[9];

        int redsum = 0;
        int greensum = 0;
        int bluesum = 0;
        int j = nitId;

        for (int i = 1; i < img.getWidth() - 2; i++) {
            //System.out.println(i + " - "+j );

            try {

                temparray[0] = new Color(img.getRGB(i - 1, j - 1));
                temparray[1] = new Color(img.getRGB(i - 1, j));
                temparray[2] = new Color(img.getRGB(i - 1, j + 1));

                temparray[3] = new Color(img.getRGB(i, j - 1));
                temparray[4] = new Color(img.getRGB(i, j));
                temparray[5] = new Color(img.getRGB(i, j + 1));

                temparray[6] = new Color(img.getRGB(i + 1, j - 1));
                temparray[7] = new Color(img.getRGB(i + 1, j));
                temparray[8] = new Color(img.getRGB(i + 1, j + 1));
                for (int l = 0; l < temparray.length; l++) {
                    System.out.println(temparray[l] + " " + l);
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("OUTOB: " + i + " - " + j);
            }

            for (int k = 0; k < temparray.length; k++) {

                try {
                    redsum += temparray[k].getRed() * mat[k];
                    greensum += temparray[k].getGreen() * mat[k];
                    bluesum += temparray[k].getBlue() * mat[k];

                } catch (NullPointerException e) {
                    System.out.println("NULLP: : " + k);
                }

            }

            redsum /= div;
            greensum /= div;
            bluesum /= div;

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


    public static void convulate2(int[] mat, int div) {
        long start = System.currentTimeMillis();

        long end = System.currentTimeMillis();

        System.out.println("zaporedno ms: " + (end - start));
        runtime = end - start;

        /*
        // write img
        File outputfile = new File("/home/arun/convulated");
        outputfile.getParentFile().mkdirs();

        try {
            ImageIO.write(newimg, "png", outputfile);
            System.out.println("Writing");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Desktop.getDesktop().open(outputfile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
    }


    public static void readImg() {
        System.out.println(System.getProperty("user.dir"));

        try {
            // img = ImageIO.read(new File("C:\\Users\\Arun\\trump.jpg"));
            // img = ImageIO.read(new File("/home/arun/akira.jpg"));
            // img = ImageIO.read(new File("/home/arun/akira.jpg"));
            //img = ImageIO.read(new File("/home/arun/akira2.jpg"));
            img = ImageIO.read(new File("/home/arun/hh.jpg"));
            System.out.println("trying");

			/*
             * ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			 * ColorConvertOp op = new ColorConvertOp(cs, null); BufferedImage
			 * image = op.filter(img, null);
			 */
        } catch (IOException e) {
        }
        newimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        polje = new ConvVzporednoThread[img.getHeight()];
        steviloNiti = img.getHeight();
        System.out.println(img.getHeight() + " polje " + polje.length);

    }
}