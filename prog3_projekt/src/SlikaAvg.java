import java.io.File;
import java.io.IOException;

import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;


import mpi.*;

public class SlikaAvg {


    private static int id;
    private static int size;

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
                if (System.getProperty("os.name").startsWith("Windows")){
                    image = ImageIO.read(new File("C:\\Users\\Arun\\hamburg.jpg"));
                }else {
                    image = ImageIO.read(new File("/home/arun/hamburg.jpg"));
                }
                System.out.println("trying");

            } catch (IOException e) {

            }

            //URL url = new URL("https://www.google.si/images/icons/product/chrome-32.png");
            //BufferedImage image = ImageIO.read(url);
            System.out.println("width: " + image.getWidth() + " height: " + image.getHeight() );

            //total lenght of image.
            totalLength[0] = image.getWidth()*image.getHeight();

            //whole image gets transferred into an array.
            slika = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
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
