import mpi.*;


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
    private static int div=1;
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
                    //image = ImageIO.read(new File("C:\\Users\\Arun\\hamburg.jpg"));
                    image = ImageIO.read(new File("C:\\Users\\Arun\\alster.jpg"));
                }else {
                    image = ImageIO.read(new File("/home/arun/doggo.jpg"));
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
        MPI.COMM_WORLD.Bcast(divsend, 0, 1, MPI.INT, 0);

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

        //change
        int[] temparray = new int[9];
        int imagew = iw[0];
        int divrecv = divsend[0];
        int stev = 0;

        System.out.println("divsend: " + divrecv);
        //temp buffer
        int[] newarrayimage =  new int[vrstica.length ];
        int[] tempNewarrayimage =  new int[vrstica.length+(3*imagew) ];
        //newarrayimage = vrstica;
        System.out.println("imagewidth: " + iw[0]);


            //imagewidth is 2x becuase its the center, you can do the traversal in 3 different ways anyways.
            //for loop outside always increases size for 2
            //onemogocit ce je imagehit mansji od 3 da se ustavi.
            //dodat na koncu 3xwidth pixlov pa vzet od prejsnih.
        int[] overlapArr = new int[vrstica.length + (3*imagew)];
        for (int i = 0; i < overlapArr.length; i++) {

            if (i < vrstica.length) {
                overlapArr[i] = vrstica[i];
            }else{
                overlapArr[i] = vrstica[i-(3*imagew)];
            }

        }

        int numWrites = 0;
        //MAIN LOOP
        for (int i = 0;i <= (vrstica.length / (imagew)) ;i++){ //here you can add -3 so it goes out of bounds but that is not good.
            for (int j = 1 ; j <= imagew ; j++) {  //+1 at the start so its not out of bounds, -2 also
                //notrani for loop bi moral biti vredu
                try{

                //ker ne najde je treba iskat expection

                    //ena resitev je da se vzame drugo povprecje in se nekako cudno resuje al pa spremeni framework.

                    //maybe write the whole 9 locations into newarrayimage.
                    //workerju dat overlapping
                if (((j-1) + (imagew*i)) <= overlapArr.length){temparray[0] = overlapArr[(j-1) + (imagew*i)];}
                if (((j)+ (imagew*i)) <= overlapArr.length){temparray[1] = overlapArr[(j)+ (imagew*i)];}
                if (((j+1)+ (imagew*i)) <= overlapArr.length){temparray[2] = overlapArr[(j+1)+ (imagew*i)];}

                if ( (j + (imagew ) - 1+ (imagew*i) ) <= overlapArr.length){temparray[3] = overlapArr[j + (imagew ) - 1+ (imagew*i)];}
                if ( (j + (imagew )   +  (imagew*i) ) <= overlapArr.length){temparray[4] = overlapArr[j + (imagew )   +  (imagew*i)];}
                if ( (j + (imagew ) + 1+ (imagew*i) ) <= overlapArr.length){temparray[5] = overlapArr[j + (imagew ) + 1+ (imagew*i)];}

                if ( (j + (imagew * 2) - 1  + (imagew*i) ) <= overlapArr.length){temparray[6] = overlapArr[j + (imagew * 2) - 1  + (imagew*i)];}
                if ( (j + (imagew * 2)  +    (imagew*i) ) <= overlapArr.length){temparray[7] = overlapArr[j + (imagew * 2)  +    (imagew*i)];}
                if ( (j + (imagew * 2)  + 1 + (imagew*i) ) <= overlapArr.length){temparray[8] = overlapArr[j + (imagew * 2)  + 1 + (imagew*i)];}
                numWrites++;

                /*temparray[0] = vrstica[(j-1) + (imagew*i)];
                temparray[1] = vrstica[(j)+ (imagew*i)];
                temparray[2] = vrstica[(j+1)+ (imagew*i)];

                temparray[3] = vrstica[j + (imagew ) - 1+ (imagew*i)];
                temparray[4] = vrstica[j + (imagew )   +  (imagew*i)];
                temparray[5] = vrstica[j + (imagew ) + 1+ (imagew*i)];

                temparray[6] = vrstica[j + (imagew * 2) - 1  + (imagew*i)];
                temparray[7] = vrstica[j + (imagew * 2)  +    (imagew*i)];
                temparray[8] = vrstica[j + (imagew * 2)  + 1 + (imagew*i)];*/

                } catch (IndexOutOfBoundsException e) {

                    //System.out.println("Worker "+id+" OutOfBounds "+stev+"x : "+" j "+j+" imagew*i "+(imagew*i)+" sum "+ ((j+1)+ (imagew*i)));
                   // System.out.println("Worker "+id+" OutOfBounds "+stev+"x : "+" j "+j+" imagew*i "+(imagew*i)+" sum "+ (j + (imagew ) + 1+ (imagew*i) ));
                    //System.out.println("Worker "+id+" OutOfBounds "+stev+"x : "+" j "+j+" imagew*i "+(imagew*i)+" sum "+ (j + (imagew * 2)  + 1+imagew*i));
                    stev++;
                }

                int[] matBBlur = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
                int[] matEdge = new int[]{-1, -1, -1, -1, 8, -1, -1, -1, -1};
                for (int k = 0; k < temparray.length; k++) {
                    //System.out.print(" temparray " + temparray[k] + " " + k);
                    Color tempColor = new Color(temparray[k]);

                    redsum += tempColor.getRed() * matEdge[k];
                    greensum += tempColor.getGreen()* matEdge[k];
                    bluesum += tempColor.getBlue()* matEdge[k];
                    //temparray[k] = 0;
                   /* redsum += tempColor.getRed() * matBBlur[k];
                    greensum += tempColor.getGreen()* matBBlur[k];
                    bluesum += tempColor.getBlue()* matBBlur[k];*/

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

                //THIS IS WHAT THE FUCK THIS -2

                try{ //newarrayimage[j + (imagew * i) -2] = newpixel.getRGB();
                    //tempNewarrayimage[j + (imagew * i) -2] = newpixel.getRGB();
                    tempNewarrayimage[(j + (imagew )   +  (imagew*i))+2] = newpixel.getRGB();


                    //newarrayimage[j + (imagew )   +  (imagew*i)] = newpixel.getRGB();
                   // newarrayimage[j + (imagew )+imagew*i] = newpixel.getRGB();
                    //newarrayimage[j + (imagew * i)] = newpixel.getRGB();

                   // System.out.println("newarrayimage " + newarrayimage[j + (imagew * i)]);
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println("writing new image: out of bounds: " + (j + (imagew * i) -2));
                }

                redsum = 0;
                greensum = 0;
                bluesum = 0;

            }
        }


        for (int i = 0; i < newarrayimage.length; i++) {
            newarrayimage[i] = tempNewarrayimage[i];
        }
        int[] zdruzeno = new int[totalLength[0]];
        System.out.println("Wrote: " + numWrites + "x");
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
                    File outputfile = new File("/home/arun/convulated.jpg");
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
            }






        }

        //konec
        MPI.Finalize();
    }
}



/*
*
*
* */
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