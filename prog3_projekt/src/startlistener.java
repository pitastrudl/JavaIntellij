import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class startlistener implements ActionListener {
    JFrame okno;
    JButton gumbek;
    // GUI

    public startlistener(JButton gumbek, JFrame okno) {
        this.gumbek = gumbek;
        this.okno = okno;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton gumb = (JButton) e.getSource();

        if (e.getSource() instanceof JButton) {

            if (gumb.getText().equals("Open Image")) {
                System.out.println("it works");
                odpriSliko();
            } else if (gumb.getText().equals("Exit")) {
                okno.dispose();
            }

        }

    }

    // FileDialog za odpret
    public static void odpriSliko() {
        // params for file dialog
        /*
         * JFrame windoww = new JFrame(); FileDialog fd = new
		 * FileDialog(windoww, "Choose an image", FileDialog.LOAD);
		 * 
		 * fd.setDirectory(System.getProperty("user.home"));
		 * 
		 * fd.setFile("*.jpg;*.jpeg"); fd.setVisible(true);
		 * 
		 * 
		 * // reading the image to BufferedImage (the image format which is
		 * saved) String filename = fd.getFile();
		 * 
		 * System.out.println("filename var is " + filename); if (filename ==
		 * null) System.out.println("You cancelled the choice"); else
		 * System.out.println("You chose " + filename); fd.getDirectory();
		 * fd.getFile();
		 */

        JFileChooser chooser = new JFileChooser();

        // setting home dir
        File workingDirectory = new File(System.getProperty("user.home"));
        chooser.setCurrentDirectory(workingDirectory);

        // file filter
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpeg", "jpg", "png");
        chooser.setFileFilter(filter);

        // dialog sutff
        chooser.setDialogTitle("Image selector");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        // actions
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }

    }

    // branje slike
    static BufferedImage img = null;
    static BufferedImage newimg = null;
    public static void readImg() {
        System.out.println(System.getProperty("user.dir"));

        try {
            // img = ImageIO.read(new File("C:\\Users\\Arun\\trump.jpg"));
            // img = ImageIO.read(new File("/home/arun/akira.jpg"));
            // img = ImageIO.read(new File("/home/arun/akira.jpg"));
            //img = ImageIO.read(new File("/home/arun/akira2.jpg"));
            img = ImageIO.read(new File("/home/arun/hamburg.jpg"));
            System.out.println("trying");

			/*
			 * ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			 * ColorConvertOp op = new ColorConvertOp(cs, null); BufferedImage
			 * image = op.filter(img, null);
			 */
        } catch (IOException e) {
        }
        newimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    static int[][] slika = new int[1][1];

    public static void toRGB() {

        // printamo sliko v matriko z rgb
        StringBuilder stringBuilder = new StringBuilder();

        String sss = "";

        // size for image
        int[][] slika = new int[img.getWidth()][img.getHeight()];

        // fill the matric with rgb
        for (int i = 0; i < img.getWidth(); i++) {
            stringBuilder.append("\n");

            for (int j = 0; j < img.getHeight(); j++) {
                sss = img.getRGB(i, j) + "";
                stringBuilder.append(sss);
                slika[i][j] = img.getRGB(i, j);
            }
        }

        // build the string
        // String finalString = stringBuilder.toString();
    }

    public static void printIMG() {
        // Print the rgb matrix
        for (int i = 0; i < slika.length; i++) {
            for (int j = 0; j < slika[i].length; j++) {
                // System.out.println(slika[i][j] +"");
                // printPixelARGB(slika[i][j]);
            }
        }
    }

    public static void changeImg() {
        for (int i = 0; i < slika.length; i++) {
            for (int j = 0; j < slika[i].length; j++) {
                // System.out.println(slika[i][j] +"");
                // printPixelARGB(slika[i][j]);
                slika[i][j] = slika[i][j] + 10;
            }
        }

    }

    public static void changeImg2() {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                // System.out.println(slika[i][j] +"");
                // printPixelARGB(slika[i][j]);
                img.setRGB(i, j, img.getRGB(i, j) + 100);
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
    }



    public static void main(String[] args) {

        readImg();
        int[] matId = new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0};
        int[] matEdge = new int[]{-1, -1, -1, -1, 8, -1, -1, -1, -1};
        int[] matEdge3 = new int[]{1, 1, 1, 1, -7, 1, 1, 1, 1};
        int[] matBBlur = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        int[] matSomeblur = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};
        int[] matEdge2 = new int[]{-1, 0, 1, -2, 0, 2, -1, 0, 1};
        int[] matEmboss = new int[]{-2, -1, 0, -1, 1, 1, 0, 1, 2};
        int[] matSharp = new int[]{0, -1, 0, -1, 5, -1, 0, -1, 0};
        int[] matEdgedet = new int[]{0, 1, 0, 1, -4, 1, 0, 1, 0};
        int div = 1;
        // convulate(matSomeblur, 16);
        // convulate(matEmboss,1);
        //convulate(matId,1);
        // convulate(matBBlur,9);
        //convulate(matEdge, div);
        //convulateGray(matId,div);
        //convulateGray(matBBlur,9);
        //convulateGray(matEdge,1);
        //convulate2(matEdge,1);
        //convulate2(matBBlur,9);
        //convulate2(matSharp,1);
        //convulate2(matEmboss,1);	//works
        //convulate2(matEdgedet, 1); //works
        //convVzp(matEdge,1);
        long avgtime = 0;

        int times = 0;
        for (int i = 0; i < 10; i++) {
            convVzp(matEdge,1);
            avgtime += runtime;
            System.out.println(runtime);
        }
        System.out.println(avgtime /10 );
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

    static long runtime = 0;
    public static void printPixelARGB(int pixel) {
		/*
		 * int alpha = (pixel >> 24) & 0xff; int red = (pixel >> 16) & 0xff; int
		 * green = (pixel >> 8) & 0xff; int blue = (pixel) & 0xff;
		 * System.out.println("argb: " + alpha + ", " + red + ", " + green +
		 * ", " + blue);
		 */
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
        System.out.println("height: " + img.getHeight() + " width: "+ img.getWidth());
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

        System.out.println("zaporedno ms: "  + ( end - start) );
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

    public static void convVzp(int[] mat, int div){
        //size of threadpool
        ExecutorService executor = Executors.newFixedThreadPool(360);

        //doing the threads?
        int n = img.getHeight();

        long start = System.currentTimeMillis();

        for (int i = 1; i < n -1; i++) {
            Runnable worker = new Worker(i,mat,div);
            executor.execute(worker);
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            //System.out.println("waiting");
        }

        long end = System.currentTimeMillis();
        System.out.println("vzporedno ms: "  + ( end - start) );
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

            e.printStackTrace();
        }
        */



    }




    public static void convulate(int[] mat, int div) {

        // make copy of img
        BufferedImage newimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        newimg = img;
        Color[] temparray = new Color[9];
        int redsum = 0;
        int greensum = 0;
        int bluesum = 0;
        long start = System.currentTimeMillis();
        // int alphasum= 0;

        // huehuhe convulate
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
                    System.out.println(temparray[k] + " " + mat[k]);

                    redsum += temparray[k].getRed() * mat[k];
                    greensum += temparray[k].getGreen() * mat[k];
                    bluesum += temparray[k].getBlue() * mat[k];

                }

                System.out.println(redsum);
                System.out.println(greensum);
                System.out.println(bluesum);
                System.out.println(" ");
                redsum = redsum / div;
                greensum = greensum / div;
                bluesum = bluesum / div;
				/*
				 * System.out.println(redsum); System.out.println(greensum);
				 * System.out.println(bluesum);
				 */

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

				/*
				 * if (alphasum > 255){ alphasum = 255; }else if (alphasum < 0){
				 * alphasum = 0; }
				 */

                // System.out.println("after div " + alphasum);
                System.out.println(redsum + " " + greensum + " " + bluesum);

                Color newpixel = new Color(redsum, greensum, bluesum);

                newimg.setRGB(i, j, newpixel.getRGB());

                redsum = 0;
                greensum = 0;
                bluesum = 0;

            }
        }
       // long end = System.currentTimeMillis();
        //System.out.println(end - start);
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
    }

    public static void convulateGray(int[] mat, int div) {
        // converting to gray

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);

        BufferedImage newimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
        BufferedImage newimg2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
        newimg2 = op.filter(img, null);
        img = newimg2;


        Color[] temparray = new Color[9];
        int redsum = 0;
        int greensum = 0;
        int bluesum = 0;

        // int alphasum= 0;

        // huehuhe convulate
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
                    System.out.println(temparray[k] + " " + mat[k]);

                    redsum += temparray[k].getRed() * mat[k];
                    greensum += temparray[k].getGreen() * mat[k];
                    bluesum += temparray[k].getBlue() * mat[k];

                }

                System.out.println(redsum);
                System.out.println(greensum);
                System.out.println(bluesum);
                System.out.println(" ");
                redsum = redsum / div;
                greensum = greensum / div;
                bluesum = bluesum / div;
				/*
				 * System.out.println(redsum); System.out.println(greensum);
				 * System.out.println(bluesum);
				 */

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

				/*
				 * if (alphasum > 255){ alphasum = 255; }else if (alphasum < 0){
				 * alphasum = 0; }
				 */

                // System.out.println("after div " + alphasum);
                System.out.println(redsum + " " + greensum + " " + bluesum);

                Color newpixel = new Color(redsum, greensum, bluesum);
                System.out.println(i + " " + j + " " + newpixel.getRGB());
                System.out.println(newpixel.getRGB());
                System.out.println(newimg.getHeight() + " " + newimg.getWidth());
                newimg.setRGB(i, j, newpixel.getRGB());

                redsum = 0;
                greensum = 0;
                bluesum = 0;

            }
        }

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
    }


}
