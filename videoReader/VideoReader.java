import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import java.awt.Graphics;
import java.lang.Thread;

public class VideoReader extends JPanel{
  public static BufferedImage image;
  public static BufferedImage newImage;
  public static BufferedImage filterImage;
  public static BufferedImage printImage;
  private static int width = 352;
  private static int height = 288;
  private static InputStream videoFile;
  private static byte[] rawBuffer;
  private static int frameSize = width * height * 3;
  private static double fps = 30;
  private static long frameDuration;
  private static File file;
  private static JFrame frame;
  private static JLabel label;
  private static double WScale;
  private static double HScale;
  private static int scaleWidth ;
  private static int scaleHeight;
  private static int size =3;//median filter size
  private static int args6;
  private static int antialiasing;
  private static String fileName;
  private static double nlPercent = 0.7;
  private static boolean fileReaded = false;
/********************************************bilinear interoilation********************
 * @param newImage
 * @param oriBufImage
 * @return new resize Image
 */
  public static void resize( BufferedImage newImage, BufferedImage oriBufImage) {   
    
    int dstWidth = newImage.getWidth();
    int dstHeight = newImage.getHeight();
    int oriWidth = oriBufImage.getWidth();   
    int oriHeight = oriBufImage.getHeight();   

    double srcCenterX = oriWidth / 2.0;   
    double srcCenterY = oriHeight / 2.0;   
    double dstCenterX = dstWidth / 2.0;   
    double dstCenterY = dstHeight / 2.0;   
    double xScale = (double) dstWidth /oriWidth;   
    double yScale = (double) dstHeight / oriHeight;   

    double xlimit = oriWidth - 1.0, xlimit2 = oriWidth - 1.001;   
    double ylimit = oriHeight - 1.0, ylimit2 = oriHeight - 1.001;
   
    dstCenterX += xScale / 2.0;   
    dstCenterY += yScale / 2.0;   
       

    double xs, ys;   
    for (int y = 0; y <= dstHeight - 1; y++) {   
        ys = (y - dstCenterY) / yScale + srcCenterY;   
        for (int x = 0; x <= dstWidth - 1; x++) {   
            xs = (x - dstCenterX) / xScale + srcCenterX;   

            if (xs < 0.0)   
                xs = 0.0;   
            if (xs >= xlimit)   
                xs = xlimit2;  
            if(ys > ylimit)
              ys = ylimit2;
            newImage.setRGB(x, y,  getInterpolatedPixel(xs, ys, oriBufImage));   

        }   
    }   
     printImage = newImage;
  }   
   
    /**  
     * Uses bilinear interpolation to find the pixel value at real coordinates  
     * (x,y).  
     */   
    private static final int getInterpolatedPixel(double x, double y,BufferedImage bi) {   
        int xbase = (int) x;   
        int ybase = (int) y;   
        double xFraction = x - xbase;   
        double yFraction = y - ybase;   
        if(xbase+1 >= bi.getWidth()) xbase = bi.getWidth()-2;
        if(ybase+1 >= bi.getHeight()) ybase = bi.getHeight()-2;

        int lowerLeft = bi.getRGB(xbase, ybase);   
        // lowerLeft = lowerLeft << 8 >>> 8;   
        int rll = (lowerLeft & 0xff0000) >> 16;   
        int gll = (lowerLeft & 0xff00) >> 8;   
        int bll = lowerLeft & 0xff;   

        int lowerRight = bi.getRGB(xbase + 1, ybase);   
        // lowerRight = lowerRight << 8 >>> 8;   
        int rlr = (lowerRight & 0xff0000) >> 16;   
        int glr = (lowerRight & 0xff00) >> 8;   
        int blr = lowerRight & 0xff;   

        int upperRight = bi.getRGB(xbase + 1, ybase + 1);   
        // upperRight = upperRight << 8 >>> 8;   
        int rur = (upperRight & 0xff0000) >> 16;   
        int gur = (upperRight & 0xff00) >> 8;   
        int bur = upperRight & 0xff;   

        int upperLeft = bi.getRGB(xbase, ybase + 1);   
        // upperLeft = upperLeft << 8 >>> 8;   
        int rul = (upperLeft & 0xff0000) >> 16;   
        int gul = (upperLeft & 0xff00) >> 8;   
        int bul = upperLeft & 0xff;   
   
        int r, g, b;   
        double upperAverage, lowerAverage;   
        upperAverage = rul + xFraction * (rur - rul);   
        lowerAverage = rll + xFraction * (rlr - rll);   
        r = (int) (lowerAverage + yFraction * (upperAverage - lowerAverage) + 0.5);   
        upperAverage = gul + xFraction * (gur - gul);   
        lowerAverage = gll + xFraction * (glr - gll);   
        g = (int) (lowerAverage + yFraction * (upperAverage - lowerAverage) + 0.5);   
        upperAverage = bul + xFraction * (bur - bul);   
        lowerAverage = bll + xFraction * (blr - bll);   
        b = (int) (lowerAverage + yFraction * (upperAverage - lowerAverage) + 0.5);   
   
        return 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff;   
    }   
    /*
     * *****************************averaging filter******************************
     * 
     */
    public static int median(int[] a) {
        int temp;
        int asize = a.length;
        //sort the array in increasing order
        for (int i = 0; i < asize ; i++)
            for (int j = i+1; j < asize; j++)
                if (a[i] > a[j]) {
                    temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
        //if it's odd
        if (asize%2 == 1)
            return a[asize/2];
        else
            return ((a[asize/2]+a[asize/2 - 1])/2);
    }
    
    public static int[] getArray(BufferedImage image, int x, int y){
        int[] n; //store the pixel values of position(x, y) and its neighbors
        int h = image.getHeight();
        int w = image.getWidth();
        int xmin, xmax, ymin, ymax; //the limits of the part of the image on which the filter operate on
        xmin = x - size/2;
        xmax = x + size/2;
        ymin = y - size/2;
        ymax = y + size/2;
        
        //special edge cases
        if (xmin < 0)
            xmin = 0;
        if (xmax > (w - 1))
            xmax = w - 1;
        if (ymin < 0)
            ymin = 0;
        if (ymax > (h - 1))
            ymax = h - 1;
        //the actual number of pixels to be considered
        int nsize = (xmax-xmin+1)*(ymax-ymin+1);
        n = new int[nsize];
        int k = 0;
        for (int i = xmin; i <= xmax; i++)
            for (int j = ymin; j <= ymax; j++){
                n[k] = image.getRGB(i, j); //get pixel value
                k++;
            }
        return n;
    }
    
    public static void filter(BufferedImage srcImage, BufferedImage dstImage) {
        int height = srcImage.getHeight();
        int width = srcImage.getWidth();
        
        int[] a; //the array that gets the pixel value at (x, y) and its neightbors
        
        for (int k = 0; k < height; k++){
            for (int j = 0; j < width; j++) {
                a = getArray(srcImage, j, k);
                int[] red, green, blue;
                red = new int[a.length];
                green = new int[a.length];
                blue = new int[a.length];
                //get the red,green,blue value from the pixel
                for (int i = 0; i < a.length; i++) {
                    red[i] = (a[i] >> 16) & 0xff;
                    green[i] =(a[i] >> 8) & 0xff;
                    blue[i] = a[i] & 0xff;
                }
                //find the median for each color
                int R = median(red);
                int G = median(green);
                int B = median(blue);
                //set the new pixel value using the median just found
                int spixel = 0xff000000 | ((R & 0xff) << 16) | ((G & 0xff) << 8) | B & 0xff; 
                dstImage.setRGB(j, k, spixel);
            }
        }
        printImage = dstImage;

    }
    
    public static boolean readNextFrame(){
    
            try {
                    //Read raw data
                int offset = 0;
              int numRead = 0;
              while (offset < frameSize && (numRead=videoFile.read(rawBuffer, offset, frameSize-offset)) >= 0) {
                  offset += numRead;
              }
              if(numRead < 0)
                fileReaded = true;
              //Generate Buffered Image
              int ind = 0;
                for(int y = 0; y < height; y++){
        
                        for(int x = 0; x < width; x++){
                 
                                //byte a = 0;
                                byte r = rawBuffer[ind];
                                byte g = rawBuffer[ind+height*width];
                                byte b = rawBuffer[ind+height*width*2]; 
                                
                                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                                //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                                image.setRGB(x,y,pix);
                                ind++;
                        }
                }
                if (args6 == 0) {
                  resize(newImage,image); //bilinear interpolation
                }else{
                  nonLinearMapping(image); //nonlinear mapping
                }
                

                if(antialiasing == 1){
                  filter(newImage, filterImage);
                }
                
            } catch (IOException e) {
      
                    e.printStackTrace();
                    return false;
            }
    return true;
    }

    public void paint(Graphics g) {
      super.paint(g);
        g.drawImage(printImage, 0, 0, null);
        
    }
    
    public void displayVideo(){
      if (antialiasing == 1) {
        frameDuration /= 10;
      }
      while(fileReaded == false){
        
        try {
            Thread.sleep(frameDuration); 
            readNextFrame();
            label.repaint();
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
      }
    }
    
/*
 * ******************non linear mapping********************************************
 * 
 */

    public static void nonLinearMapping(BufferedImage srcImage) {
      
      
      int width = srcImage.getWidth();
      int height = srcImage.getHeight();
      int dstWidth = scaleWidth , dstHeight = scaleHeight;
      int linearWidth,nonlinearWidth; // widht on the dst image;
      int linearMappingWidth,nonlinearMappingWidth; // width on the original image
      if((double)dstWidth/dstHeight != 1.222){
        if((double)dstWidth/dstHeight < 1.222)
          linearWidth = (int)(nlPercent*dstWidth);
        else
          linearWidth= (int)(nlPercent * width * dstHeight / height);

        nonlinearWidth = dstWidth - linearWidth;
        linearMappingWidth = (int)(linearWidth * height / dstHeight);
        nonlinearMappingWidth = width - linearMappingWidth;
        
        for(int y = 0; y < dstHeight -1; y++)
          for(int x = 0; x < dstWidth -1; x++){
            int xs,ys;
            if(x < (int)(nonlinearWidth/2) || x > (int)( nonlinearWidth/2 + linearWidth)){
              if(x > (int)( nonlinearWidth/2 + linearWidth))
                xs = (x  - nonlinearWidth/2 - linearWidth) * nonlinearMappingWidth / nonlinearWidth + (linearMappingWidth+ nonlinearMappingWidth/2);
              else
                xs =(int)(x * nonlinearMappingWidth / nonlinearWidth);
              ys = (int)(y * height / dstHeight);
              
            }else{
              xs = (x - nonlinearWidth/2) * height / dstHeight + nonlinearMappingWidth/2;
              ys = y * height / dstHeight;
            }
            if(xs > width-1)
              xs = width -1;
            if(ys > height -1)
              ys = height -1;
            
            newImage.setRGB(x, y, srcImage.getRGB(xs, ys));
          }
      }else{

        newImage = srcImage;
      }
      
      printImage = newImage;
    }
  
   public static void main(String[] args) {
     
  if(args.length != 6){
    System.out.println("Usage: java fileName WidthScaleFactor HeightScaleFactor FrameRate AntianliasingSwitch NonLinearMapingSwitch");
    return;
  }
  else{
    fileName = args[0];
    WScale = Double.parseDouble(args[1]);
    HScale = Double.parseDouble(args[2]);
    fps = Integer.parseInt(args[3]);
    antialiasing = Integer.parseInt(args[4]);
    if(antialiasing != 0 && antialiasing != 1){
      System.out.println("antialiasing factor has to be 0 or 1.");
      return;
    }
    args6 = Integer.parseInt(args[5]);
    if(args6 != 0 && args6 != 1){
      System.out.println("this factor has to be 0 or 1. Seam Carving is not included.");
      return;
    }
  }

  try {
    file = new File(fileName);
    videoFile = new FileInputStream(file);
    rawBuffer = new byte[frameSize];
    frameDuration = (long) (1000/fps);
    scaleWidth = (int)Math.ceil(width * WScale);
    scaleHeight = (int)Math.ceil(height * HScale);
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    newImage = new BufferedImage(scaleWidth,scaleHeight, BufferedImage.TYPE_INT_RGB);
    filterImage = new BufferedImage(scaleWidth,scaleHeight, BufferedImage.TYPE_INT_RGB);

  } catch (FileNotFoundException e) {
    e.printStackTrace();
  }
    
  
    // Use a label to display the image
    frame = new JFrame();
    readNextFrame();
    label = new JLabel(new ImageIcon(printImage));
    frame.getContentPane().add(label, BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
    VideoReader rd = new VideoReader();
    rd.displayVideo();
    
  }
}