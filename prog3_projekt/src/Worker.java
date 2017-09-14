import java.awt.*;

public class Worker implements Runnable {

    private int index;
    private int[] mat;
    private int div;

    public Worker(int index, int[] mat, int div) {
        this.index = index;
        this.mat = mat;
        this.div = div;
    }


    @Override
    public void run() {
        //System.out.println(Thread.currentThread().getName()+ " Start!");
        compute();
    }

    private void compute() {

        Color[] temparray = new Color[9];

        int redsum = 0;
        int greensum = 0;
        int bluesum = 0;

        int j = index;

        for (int i = 1; i < Vzporedno.img.getWidth() - 1; i++) {
            temparray[0] = new Color(Vzporedno.img.getRGB(i - 1, j - 1));
            temparray[1] = new Color(Vzporedno.img.getRGB(i - 1, j));
            temparray[2] = new Color(Vzporedno.img.getRGB(i - 1, j + 1));

            temparray[3] = new Color(Vzporedno.img.getRGB(i, j - 1));
            temparray[4] = new Color(Vzporedno.img.getRGB(i, j));
            temparray[5] = new Color(Vzporedno.img.getRGB(i, j + 1));

            temparray[6] = new Color(Vzporedno.img.getRGB(i + 1, j - 1));
            temparray[7] = new Color(Vzporedno.img.getRGB(i + 1, j));
            temparray[8] = new Color(Vzporedno.img.getRGB(i + 1, j + 1));

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
            Vzporedno.newimg.setRGB(i, j, newpixel.getRGB());
            redsum = 0;
            greensum = 0;
            bluesum = 0;
        }
    }


}


