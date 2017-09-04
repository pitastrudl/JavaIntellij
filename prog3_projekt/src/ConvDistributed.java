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
    private static int div;
    private static BufferedImage newimg = null;
    private static int[] newarrayimage;

    /**
     * @param args
     */
    public static BufferedImage image;

    public static void main(String[] args) throws Exception {

        MPI.Init(args);  //inicalizacija

        id = MPI.COMM_WORLD.Rank();  //id procesa
        size = MPI.COMM_WORLD.Size(); //velikost sveta

        System.out.println("jaz sem "+id+" od "+size);

        //what?
        int[] slika = new int[0];
        int[] totalLength = new int[1];

        //first process tries to read the file. Maybe the first one does all the preparation for distribution.
        if (id == 0){

            try {

                image = ImageIO.read(new File("/home/arun/akira.jpg"));
                imagewidth = image.getWidth();
                imageheight = image.getHeight();

                System.out.println("trying");

            } catch (IOException e) {

            }
            newimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB)


            //URL url = new URL("https://www.google.si/images/icons/product/chrome-32.png");
            //BufferedImage image = ImageIO.read(url);
            System.out.println("width: " + image.getWidth() + " height: " + image.getHeight() );

            //total lenght of image.
            totalLength[0] = image.getWidth()*image.getHeight();

            //whole image gets transferred into an array.
            slika = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            newarrayimage = slika;
        }


        //whats up here? syso does not trigger here untill later, huh.

        //broadcasting the total length?
        MPI.COMM_WORLD.Bcast(totalLength, 0, 1, MPI.INT, 0);


        if (id != 0) {
            slika = new int[totalLength[0]]; // ceprav tega ne rabimo, moramo inicializirat, ker ce ne bo vrgu MPJ exception.
        }
        //System.out.println("size je: "+ size);

        //size of vrstica is a fragment for a process
        int[] vrstica = new int[totalLength[0] / size];

        //System.out.println(MPI.INT);

        MPI.COMM_WORLD.Scatter(slika, 0, vrstica.length, MPI.INT, vrstica, 0, vrstica.length, MPI.INT, 0);


        //here the computing starts
        int redsum = 0;
        int greensum = 0;
        int bluesum = 0;


        int[] temparray = new int[9];


        try {
            for (int i = imagewidth+1; i <= imagewidth*2-2;i++){

                temparray[0] = vrstica[(i-imagewidth)-1];
                temparray[1] = vrstica[(i-imagewidth)];
                temparray[2] = vrstica[(i-imagewidth)+1];

                temparray[3] = vrstica[i-1];
                temparray[4] = vrstica[i];
                temparray[5] = vrstica[i+1];

                temparray[3] = vrstica[(i+imagewidth)*2-1];
                temparray[4] = vrstica[(i+imagewidth)*2];
                temparray[5] = vrstica[(i+imagewidth)*2+1];


                for (int k  = 0;k < temparray.length;k++){
                    Color tempColor = new Color(temparray[k]);
                    redsum = tempColor.getRed();
                    greensum = tempColor.getGreen();
                    bluesum = tempColor.getBlue();
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

                //treba dat zdruzeno al neki tuki?
                newarrayimage[i] = newpixel.getRGB();
                //newimg.setRGB(i, j, newpixel.getRGB());

                redsum = 0;
                greensum = 0;
                bluesum = 0;


            }

        }catch (IndexOutOfBoundsException e){

            System.out.println("out of bounds");
        }





        //racunamo avg
        //dobimo ze nafilano vrsitco z vrednostimi, sklepamo da to nafila scatter.
        int r = 0, g = 0, b = 0;
        for (int i=0; i<vrstica.length; i++)
        {
            //System.out.println(vrstica[i]);
            Color c = new Color(vrstica[i]);
            r += c.getRed();
            g += c.getGreen();
            b += c.getBlue();
        }
        r = r / vrstica.length;
        g = g / vrstica.length;
        b = b / vrstica.length;

        int[] zapakirano = new int[1];
        zapakirano[0] = new Color(r, g, b).getRGB();

        int[] zdruzeno = new int[size];

        MPI.COMM_WORLD.Gather(zapakirano, 0, 1, MPI.INT, zdruzeno, 0, 1, MPI.INT, 0);

        if (id == 0) {
            r = 0;
            g = 0;
            b = 0;

            for (int i=0; i<zdruzeno.length; i++)
            {
                Color c = new Color(zdruzeno[i]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
            r = r / zdruzeno.length;
            g = g / zdruzeno.length;
            b = b / zdruzeno.length;

            System.out.println("Rdeca: " + r + " Zelena: " + g + " Plava: " + b);
        }

        //konec
        MPI.Finalize();
    }


}

