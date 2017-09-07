import mpi.MPI;
import sun.rmi.runtime.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class ConvDistributed {


    private static int id;
    private static int size;
    private static int imagewidth;
    private static int imageheight;
    private static int div=9;
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

        //what?
        int[] slika = new int[0];
        int[] totalLength = new int[1];

        //first process tries to read the file. Maybe the first one does all the preparation for distribution.
        if (id == 0) {

            //read file
            try {
                if (System.getProperty("os.name").startsWith("Windows")){
                    image = ImageIO.read(new File("C:\\Users\\Arun\\hamburg.jpg"));
                    //image = ImageIO.read(new File("C:\\Users\\Arun\\alster.jpg"));
                }else {
                    image = ImageIO.read(new File("/home/arun/hamburg.jpg"));
                }
                imagewidth = image.getWidth();
                imageheight = image.getHeight();

                System.out.println("trying to get image");

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
            System.out.println("Sum of leftover pixels: " + (totalLength[0] - ((totalLength[0] / size) * size)) );

            //whole image gets transferred into an array.
            slika = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            //newarrayimage = slika; //make a copy of the image
        }

        //whats up here? syso does not trigger here untill later, huh.

        //broadcasting the total length to the world and the other stuff.
        MPI.COMM_WORLD.Bcast(totalLength, 0, 1, MPI.INT, 0);

        int[] iw = new int[1];
        iw[0] = imagewidth;
        MPI.COMM_WORLD.Bcast(iw, 0, 1, MPI.INT, 0);

        int[] divsend = new int[1];
        divsend[0] = div;
        MPI.COMM_WORLD.Bcast(iw, 0, 1, MPI.INT, 0);

        if (id != 0) {
            slika = new int[totalLength[0]]; // ceprav tega ne rabimo, moramo inicializirat, ker ce ne bo vrgu MPJ exception.
        }
        //System.out.println("size je: "+ size);

        //size of vrstica is a fragment for a process
        int[] vrstica = new int[totalLength[0] / size];

        ////////////////////////////////////////////////////////////////
        MPI.COMM_WORLD.Scatter(slika, 0, vrstica.length, MPI.INT, vrstica, 0, vrstica.length, MPI.INT, 0);
        //MPI.COMM_WORLD.Scatter(slika, 0, vrstica.length, MPI.INT, vrstica, 0, vrstica.length, MPI.INT, 0);

            //here the computing starts
            int redsum = 0;
            int greensum = 0;
            int bluesum = 0;
            int r = 0;
            int g= 0;
            int b= 0;

            //check if it gets vrstica stuff
            for (int i = 0; i < vrstica.length; i++) {
                //System.out.println(vrstica[i]);
            }

            //change
            int[] temparray = new int[9];
            int imagew = iw[0];

            int divrecv = divsend[0];

            //temp buffer
            int[] newarrayimage =  new int[vrstica.length];
            //newarrayimage = vrstica;
            System.out.println("imagewidth: " + iw[0]);
            for (int i = 0; i < vrstica.length; i++) {
                //System.out.println(newarrayimage[i]);
                //System.out.println(vrstica[i]);
            }

                //imagewidth is 2x becuase its the center, you can do the traversal in 3 different ways anyways.
                //for loop outside always increases size for 2
                //onemogocit ce je imagehit mansji od 3 da se ustavi.
            int stev = 0;

            for (int i = 0;i <= vrstica.length / (imagew ) ;i++){
                for (int j = 1 ; j <= imagew - 2; j++) {  //+1 at the start so its not out of bounds, -2 also
                    //notrani for loop bi moral biti vredu
                    try{

                    temparray[0] = vrstica[(j-1) +imagew*i];
                    temparray[1] = vrstica[(j)+imagew*i];
                    temparray[2] = vrstica[(j+1)+imagew*i];

                    temparray[3] = vrstica[j + (imagew ) - 1+ imagew*i];
                    temparray[4] = vrstica[j + (imagew )+imagew*i];
                    temparray[5] = vrstica[j + (imagew ) + 1+imagew*i];

                    temparray[6] = vrstica[j + (imagew * 2) - 1+imagew*i];
                    temparray[7] = vrstica[j + (imagew * 2+imagew*i)];
                    temparray[8] = vrstica[j + (imagew * 2)  + 1+imagew*i];

                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Worker " + id+ " out of bounds: " + stev);
                        stev++;
                    }

                    int[] matBBlur = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
                    //int[] matEdge = new int[]{-1, -1, -1, -1, 8, -1, -1, -1, -1};
                    for (int k = 0; k < temparray.length; k++) {
                        //System.out.print("|  id " + id + " temparray " + temparray[k] + " " + k);
                        Color tempColor = new Color(temparray[k]);
                        redsum += tempColor.getRed() * matBBlur[k];
                        greensum += tempColor.getGreen()* matBBlur[k];
                        bluesum += tempColor.getBlue()* matBBlur[k];

                        if (temparray[k] == 0){
                            System.out.println(k + " i: " + i + " j: "+ j);
                        }
                    }

                    //System.out.println("");

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

                    Color newpixel = new Color(redsum, greensum, bluesum);
                    //System.out.println("pixels " + " "+redsum+ " "+greensum+" "+bluesum + " div " + divrecv);
                    //treba dat zdruzeno al neki tuki?
                    try{

                        //newarrayimage[(j)+imagew*i] = newpixel.getRGB();
                       // newarrayimage[j + (imagew )+imagew*i] = newpixel.getRGB();
                        newarrayimage[j + (imagew * i)] = newpixel.getRGB();

                       // System.out.println("newarrayimage " + newarrayimage[j + (imagew * i)]);

                    }
                    catch (IndexOutOfBoundsException e) {

                        System.out.println("writing new image: out of bounds");
                    }

                    redsum = 0;
                    greensum = 0;
                    bluesum = 0;

                }
            }

        for (int i = 0; i < newarrayimage.length; i++) {
            Color tempColor = new Color(newarrayimage[i]);
            if (newarrayimage[i] == 0){
                //newarrayimage[i] = newarrayimage[i+2];
            }
            //System.out.println(newarrayimage[i] + " "  +tempColor.getRed() +" "+ tempColor.getGreen() +" "+tempColor.getBlue());
        }

        int[] zdruzeno = new int[totalLength[0]];

        ////////////////////////////////////////////////////////////////
        MPI.COMM_WORLD.Gather(newarrayimage, 0, newarrayimage.length, MPI.INT, zdruzeno, 0, newarrayimage.length, MPI.INT, 0);

        if (id == 0) { //zdruzevalni del.

            System.out.println("zdrzueno: " + zdruzeno.length);
            int w = imagewidth;
            int h = imageheight;
            System.out.println("w " + w + " h " + h + " newarray: " + newarrayimage.length);
            BufferedImage writeImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
            int ste = 0;
            for (int i = 0; i < zdruzeno.length; i++) {
               //System.out.println(zdruzeno[i]+" "+ ste);
                ste++;

            }

            writeImage.setRGB(0,0,w,h,zdruzeno,0,w);
            System.out.println(writeImage.getColorModel() +""+ writeImage.getType() +""+ writeImage.getSampleModel());

            if (System.getProperty("os.name").startsWith("Windows")){
                try {
                    File outputfile = new File("C:\\Users\\Arun\\convulated.jpg");
                    outputfile.getParentFile().mkdirs();
                    ImageIO.write(writeImage, "jpg", outputfile);
                    System.out.println("Writing");
                    try
                    {


                        if (outputfile.exists())
                        {
                            if (Desktop.isDesktopSupported())
                            {
                                Desktop.getDesktop().open(outputfile);
                            }

                            else
                            {
                                System.out.println("File does not exists!");
                            }

                        }
                    }
                    catch(Exception ert)
                    {}

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("fail writing");
                }


            }else {


                try {
                    File outputfile = new File("/home/arun/convulated");
                    outputfile.getParentFile().mkdirs();
                    ImageIO.write(writeImage, "jpg", outputfile);
                    System.out.println("Writing");
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


 /*
            for (int i = 1;i <= (imageheight / size);i++){

                for (int j = (imagewidth)*i + 1 ; j <= (imagewidth * 2 - 1)*i; j++) {  //+1 at the start so its not out of bounds
                    try {
                    temparray[0] = vrstica[(j - imagewidth) - 1];
                    temparray[1] = vrstica[(j - imagewidth)];
                    temparray[2] = vrstica[(j - imagewidth) + 1];

                    temparray[3] = vrstica[j - 1];
                    temparray[4] = vrstica[j];
                    temparray[5] = vrstica[j + 1];

                    temparray[3] = vrstica[(j + imagewidth) * 2 - 1];
                    temparray[4] = vrstica[(j + imagewidth) * 2];
                    temparray[5] = vrstica[(j + imagewidth) * 2 + 1];
                    } catch (IndexOutOfBoundsException e) {

                        //System.out.println("out of bounds: " + stev);
                        stev++;
                    }

                    for (int k = 0; k < temparray.length; k++) {
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
                    try{
                        newarrayimage[j] = newpixel.getRGB();
                    }
                    catch (IndexOutOfBoundsException e) {

                        //System.out.println("out of bounds");
                    }



                    redsum = 0;
                    greensum = 0;
                    bluesum = 0;

                }
            }
            */