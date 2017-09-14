import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Vzporedno {
    // branje slike
    static BufferedImage img = null;
    static BufferedImage newimg = null;
    static int[][] slika = new int[1][1];
    static long runtime = 0;



    public static void main(String[] args) {

        readImg();
        int[] matId = new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0};
        int[] matEdge = new int[]{-1, -1, -1, -1, 8, -1, -1, -1, -1};
        int[] matBBlur = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        int[] matSharp = new int[]{0, -1, 0, -1, 5, -1, 0, -1, 0};
        int div = 1;


        convVzp(matEdge, 1);
        convulate2(matEdge, 1);
        //7537 zap
        //4696 vzp 4 threadpool
        //4476 vzp 8 threadpool
        //4454 vzp 12 threadpool
        //4805 vzp 3 threadpool
        //4415 vzp 16 threadpool
        //4301 vzp 36 threadpool
        //4267 vzp 120 threadpool
        //4389 vzp 360 threadpool
    }

    public static void convulate2(int[] mat, int div) {
        long start = System.currentTimeMillis();
        BufferedImage newimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        /*
        System.out.println("type of newimg: " + newimg.getType() + "\n" + newimg.getColorModel() +"\n" + newimg.getSampleModel());
        System.out.println();
        System.out.println("type of img: " + img.getType() + "\n" + img.getColorModel() +"\n" + img.getSampleModel());
        System.out.println(img + "\n" + newimg);
        //newimg = img; //this cost me 4 hours of troubleshooting
        */

        Color[] temparray = new Color[9];

        int redsum = 0;
        int greensum = 0;
        int bluesum = 0;
        System.out.println("height: " + img.getHeight() + " width: " + img.getWidth());
        for (int i = 1; i < img.getWidth() - 2; i++) {

            for (int j = 1; j < (img.getHeight() - 2); j++) {

                //System.out.println(i + " - "+j);
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

    public static void convVzp(int[] mat, int div) {
        //size of threadpool
        ExecutorService executor = Executors.newFixedThreadPool(16);

        //doing the threads?
        int n = img.getHeight();

        long start = System.currentTimeMillis();

        for (int i = 1; i < n - 1; i++) {
            Runnable worker = new Worker(i, mat, div);
            executor.execute(worker);
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            //System.out.println("waiting");
        }

        long end = System.currentTimeMillis();
        System.out.println("vzporedno ms: " + (end - start));
        runtime = end - start;

        // write img
        File outputfile = new File("C:\\Users\\Arun\\convulated.png");
        //File outputfile = new File("/home/arun/convulated");
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

            e.printStackTrace();
        }


    }

    public static void readImg() {
        System.out.println(System.getProperty("user.dir"));

        try {
            img = ImageIO.read(new File("C:\\Users\\Arun\\dog.jpg"));
            System.out.println("trying");
        } catch (IOException e) {
        }
        newimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public static void toRGB() {

        // size for image
        int[][] slika = new int[img.getWidth()][img.getHeight()];

        // fill the matric with rgb
        for (int i = 0; i < img.getWidth(); i++) {

            for (int j = 0; j < img.getHeight(); j++) {
                slika[i][j] = img.getRGB(i, j);
            }
        }

    }

    public static void writeImage(String Name) {

        File outputfile = new File("/home/arun/" + Name);
        outputfile.getParentFile().mkdirs();

        try {
            ImageIO.write(img, "jpg", outputfile);
            System.out.println("Writing");
        } catch (IOException e) {
            e.printStackTrace();
        }

		/*
		 * String path = "/home/arun/" + Name + ".png"; BufferedImage image =
		 * new BufferedImage(slika.length, slika[0].length,
		 * BufferedImage.TYPE_INT_RGB); for (int x = 0; x < slika.length; x++) {
		 * for (int y = 0; y < slika[x].length; y++) { image.setRGB(x, y,
		 * slika[x][y]); } }
		 *
		 * File ImageFile = new File(path); try { ImageIO.write(image, "png",
		 * ImageFile); } catch (IOException e) { e.printStackTrace(); }
		 */
    }}