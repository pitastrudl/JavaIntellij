import mpi.MPI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConvDistributed {
    private static int id;
    private static int size;
    private static int imagewidth;
    private static int imageheight;
    private static int div = 1;
    private static BufferedImage newimg = null;

    /**
     * @param args
     */
    public static BufferedImage image;

    public static void main(String[] args) throws Exception {

        MPI.Init(args);  //inicalizacija

        id = MPI.COMM_WORLD.Rank();  //id procesa
        size = MPI.COMM_WORLD.Size(); //velikost sveta

        System.out.println("jaz sem " + id + " od " + size);

        int[] picture = new int[0];
        int[] totalLength = new int[1];

        //first process tries to read the file. Maybe the first one does all the preparation for distributing the process
        if (id == 0) {
            try { //check OS type
                if (System.getProperty("os.name").startsWith("Windows")) {
                    image = ImageIO.read(new File(System.getProperty("user.home") + "\\" + "dog.jpg"));
                } else {
                    image = ImageIO.read(new File(System.getProperty("user.home") + "/" + "hamburg.jpg"));
                }
                imagewidth = image.getWidth();
                imageheight = image.getHeight();
            } catch (IOException e) {
            }

            //new image that will get written
            newimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

            //info print
            System.out.println("width: " + image.getWidth() + " height: " + image.getHeight());

            //total lenght of image.
            totalLength[0] = image.getWidth() * image.getHeight();

            System.out.println("one chunk is: " + (totalLength[0] / size) + " pixels");
            System.out.println("Total pixels is: " + totalLength[0]);
            System.out.println("Sum of leftover pixels: " + (totalLength[0] - ((totalLength[0] / size) * size)));

            //whole image gets transferred into an array.
            picture = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        }


        //broadcasting the total length to the world and the other stuff.
        MPI.COMM_WORLD.Bcast(totalLength, 0, 1, MPI.INT, 0);

        //broadcasting the imagewidth
        int[] iw = new int[1];
        iw[0] = imagewidth;
        MPI.COMM_WORLD.Bcast(iw, 0, 1, MPI.INT, 0);

        //broadcasting the divisor
        int[] divsend = new int[1];
        divsend[0] = div;
        MPI.COMM_WORLD.Bcast(divsend, 0, 1, MPI.INT, 0);

        if (id != 0) {
            picture = new int[totalLength[0]]; // ceprav tega ne rabimo, moramo inicializirat, ker ce ne bo vrgu MPJ exception.
        }

        //size of vrstica is a fragment for a process
        int[] chunk = new int[totalLength[0] / size];
        long start = System.currentTimeMillis();
        ////////////////////////////////////////////////////////////////
        MPI.COMM_WORLD.Scatter(picture, 0, chunk.length, MPI.INT, chunk, 0, chunk.length, MPI.INT, 0);

        int redsum = 0;
        int greensum = 0;
        int bluesum = 0;

        //switching
        int[] temparray = new int[9];
        int imagew = iw[0];
        int divrecv = divsend[0];
        int stev = 0;

        System.out.println("divsend: " + divrecv);
        int[] newarrayimage = new int[chunk.length];
        newarrayimage = chunk;
        System.out.println("imagewidth: " + iw[0]);


        //imagewidth is 2x becuase its the center, you can do the traversal in 3 different ways anyways.
        //for loop outside always increases size for 2
        //one solution to deal with edge cases is to somehow do a partial normalization instead of using the whole 3x3 matrix
        for (int i = 0; i <= (chunk.length / (imagew)); i++) { //here you can add -3 so it goes out of bounds but that is not good.
            for (int j = 1; j <= imagew; j++) {  //+1 at the start so its not out of bounds, -2 also

                try {

                    temparray[0] = chunk[(j - 1) + (imagew * i)];
                    temparray[1] = chunk[(j) + (imagew * i)];
                    temparray[2] = chunk[(j + 1) + (imagew * i)];

                    temparray[3] = chunk[j + (imagew) - 1 + (imagew * i)];
                    temparray[4] = chunk[j + (imagew) + (imagew * i)];
                    temparray[5] = chunk[j + (imagew) + 1 + (imagew * i)];

                    temparray[6] = chunk[j + (imagew * 2) - 1 + (imagew * i)];
                    temparray[7] = chunk[j + (imagew * 2) + (imagew * i)];
                    temparray[8] = chunk[j + (imagew * 2) + 1 + (imagew * i)];

                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Worker " + id + " OutOfBounds " + stev + "x : " + " j " + j + " imagew*i " + (imagew * i) + " sum " + ((j + 1) + (imagew * i)));
                    System.out.println("Worker " + id + " OutOfBounds " + stev + "x : " + " j " + j + " imagew*i " + (imagew * i) + " sum " + (j + (imagew) + 1 + (imagew * i)));
                    System.out.println("Worker " + id + " OutOfBounds " + stev + "x : " + " j " + j + " imagew*i " + (imagew * i) + " sum " + (j + (imagew * 2) + 1 + imagew * i));
                    stev++;
                }

                //applying the convolution matrix
                int[] matEdge = new int[]{-1, -1, -1, -1, 8, -1, -1, -1, -1};
                for (int k = 0; k < temparray.length; k++) {
                    Color tempColor = new Color(temparray[k]);
                    redsum += tempColor.getRed() * matEdge[k];
                    greensum += tempColor.getGreen() * matEdge[k];
                    bluesum += tempColor.getBlue() * matEdge[k];
                }

                //normalization
                redsum /= divrecv;
                greensum /= divrecv;
                bluesum /= divrecv;

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

                //writing the new image
                try {
                    Color newpixel = new Color(redsum, greensum, bluesum);
                    newarrayimage[j + (imagew * i) - 2] = newpixel.getRGB();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("writing new image: out of bounds");
                }

                //reset sums
                redsum = 0;
                greensum = 0;
                bluesum = 0;

            }
        }

        int[] combined = new int[totalLength[0]];

        ////////////////////////////////////////////////////////////////
        MPI.COMM_WORLD.Gather(newarrayimage, 0, newarrayimage.length, MPI.INT, combined, 0, newarrayimage.length, MPI.INT, 0);

        if (id == 0) { //merging part
            System.out.println("zdrzueno: " + combined.length);
            int w = imagewidth;
            int h = imageheight;

            System.out.println("w " + w + " h " + h + " newarray: " + newarrayimage.length);

            BufferedImage writeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            writeImage.setRGB(0, 0, w, h, combined, 0, w);

            System.out.println(writeImage.getColorModel() + "" + writeImage.getType() + "" + writeImage.getSampleModel());
            long end = System.currentTimeMillis();
            System.out.println("zaporedno ms: " + (end - start));
            //writing the file
            if (System.getProperty("os.name").startsWith("Windows")) {

                try {
                    File outputfile = new File(System.getProperty("user.home") + "\\" + "convulated.jpg");
                    outputfile.getParentFile().mkdirs();
                    ImageIO.write(writeImage, "jpg", outputfile);
                    System.out.println("Writing");
                    try {
                        if (outputfile.exists()) {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(outputfile);
                            } else {
                                System.out.println("File does not exists!");
                            }
                        }
                    } catch (Exception ert) {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("fail writing");
                }

            } else {
                try {
                    File outputfile = new File(System.getProperty("user.home") + "/convulated.jpg");
                    outputfile.getParentFile().mkdirs();
                    ImageIO.write(writeImage, "jpg", outputfile);
                    System.out.println("Writing");
                    try {
                        if (outputfile.exists()) {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(outputfile);
                            } else {
                                System.out.println("File does not exists!");
                            }
                        }
                    } catch (Exception ert) {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("fail writing");
                }
            }
        }
        //konec
        MPI.Finalize();
    }
}

