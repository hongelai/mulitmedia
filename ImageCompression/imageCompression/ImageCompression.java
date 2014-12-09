package imageCompression;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ImageCompression extends JLabel{
	public static int width = 512;
	public static int height = 512;
	public static int dctBlockSize = 8;
	public static double YCbCrImage[][][] = new double[3][height][width];
	public static int tileX = (int)Math.ceil(width/dctBlockSize);
	public static int tileY =(int)Math.ceil(height/dctBlockSize);
	public static double tiles[][][][] = new double[tileX*tileY][3][dctBlockSize][dctBlockSize];
	public static int tileNum = tileX*tileY;
	public static double dequantizeTiles[][][][];
	public static BufferedImage dctImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	public static BufferedImage dwtImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	public static BufferedImage newImage = new BufferedImage(dwtImage.getWidth()+dctImage.getWidth(),Math.max(dwtImage.getHeight(),dctImage.getHeight()), BufferedImage.TYPE_INT_ARGB);
	public static JLabel label;
	public static double outputdata[][][][];
	public static LinkedList<double[][][][]> imgList;  

	public static void extractRGB(byte[] bytes){
		int ind = 0;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
		 
				short r = (short)(bytes[ind] & 0xff);
				short g = (short)(bytes[ind+height*width]& 0xff);
				short b = (short)(bytes[ind+height*width*2]& 0xff); 
				ind++;
		      
	            YCbCrImage[0][y][x]=r;
	            YCbCrImage[1][y][x]=g;
	            YCbCrImage[2][y][x]=b;

			}
		}
	}
	
	public static void splitImage(){
		
		int tileIndex = 0;
	    for (int i=0;i<tileY;i++) {
	        int u=i*dctBlockSize;
	        for (int j=0;j<tileX;j++) {
	            int v=j*dctBlockSize;
	            
	            for(int y = 0 ; y < dctBlockSize;	y++){
	            	int coY  = u+y;
	            	for(int x = 0; x < dctBlockSize; x++){
	            		
	            		int coX = v+x;
	            		if(coY >= height || coX >= width) // set to black if exceeds original image size
	            		{
	            			tiles[tileIndex][0][y][x] = 0;
		            		tiles[tileIndex][1][y][x] = 0;
		            		tiles[tileIndex][2][y][x] = 0;
	            		}else{
		            		tiles[tileIndex][0][y][x] = YCbCrImage[0][coY][coX];
		            		tiles[tileIndex][1][y][x] = YCbCrImage[1][coY][coX];
		            		tiles[tileIndex][2][y][x] = YCbCrImage[2][coY][coX];
	            		}
	            	}
	            }
	            tileIndex++;
	            
	        }
	    }		
	}
	
	public static void constructImage(double dequantizeTiles[][][][]){
		//construct image from dequantized tiles
		 int tileIndex = 0;
		 for(int i = 0; i < tileY; i++){
			 int u=i*dctBlockSize;
			 for(int j = 0; j < tileX; j++){
				 int v=j*dctBlockSize;
				 
				 for(int y = 0 ; y < dctBlockSize;	y++){
					 int coY  = u+y;
					 for(int x = 0; x < dctBlockSize; x++){	       		
	            		int coX = v+x;
	            		
	            		short r = (short)Math.round(dequantizeTiles[tileIndex][0][y][x]);
	            		short g = (short)Math.round(dequantizeTiles[tileIndex][1][y][x]);
	            		short b = (short)Math.round(dequantizeTiles[tileIndex][2][y][x]);
						
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);

						dctImage.setRGB(coX,coY,pix);
	            	}
				 }
				 tileIndex++;
			 }
		 }
	}
	
	public static void constructDWTImage(double[][][] input){
		
		 for(int y = 0 ; y < input[0].length;	y++)
			 for(int x = 0; x < input[0][0].length; x++){	       		
        		
        		short r = (short)input[0][y][x];
        		short g =  (short)input[1][y][x];
        		short b =  (short)input[2][y][x];
        		
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);

				dwtImage.setRGB(x,y,pix);
        	}
	}
	
	@Override
  	protected void paintComponent(Graphics g) {
  		super.paintComponent(g);
        g.drawImage(dctImage, 0, 0, null);
        g.drawImage(dwtImage, dctImage.getWidth()+20, 0, null);
       
    }
  	
	public static void main(String []args){
		String fileName = args[0];
		int coefPerBlock = (int)(Integer.parseInt(args[1])/((width/dctBlockSize)*(height/dctBlockSize)));
		
		try {
		    File file = new File(fileName);
		    InputStream is = new FileInputStream(file);
		    long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    
		    int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        
	        extractRGB(bytes);
	        
	        if(Integer.parseInt(args[1])>0 && Integer.parseInt(args[1]) <= width*height){
	        	
	        	dequantizeTiles = new double[tileNum][3][dctBlockSize][dctBlockSize];
		        splitImage();
		
				 DCT dct = new DCT();
				 dct.initMatrix();
				 for(int i=0; i < tileNum; i++)
					 for(int channel = 0; channel < 3; channel++){

						double output[] = dct.forwardDCT(tiles[i][channel]);
						dequantizeTiles[i][channel] = dct.inverseDCT(output,coefPerBlock);
					 }
				 constructImage(dequantizeTiles);

				 //process dwt
				 DWT dwt = new DWT();
				 dwt.makeDWTmatrix(width, height);
				 double result[][] = new double[3][width*height];
				 double outputdata[][][] = new double[3][height][width];
				 int level = (int)(Math.log(width)/Math.log(2));
				 for(int channel = 0; channel < 3; channel++){
					  result[channel] = dwt.forwardDWT(level, width, height, YCbCrImage[channel]);
					  outputdata[channel] = dwt.inverseDWT(level, width, height, result[channel],Integer.parseInt(args[1]));
				 }
				 constructDWTImage(outputdata);
				 
	        }else if(Integer.parseInt(args[1]) == -1){
	        	
	        	outputdata = new double[64][3][height][width];
	        	imgList = new LinkedList<double[][][][]>();
		        splitImage();
				
		        //initialize image list
		        for(int it = 0;it<dctBlockSize*dctBlockSize;it++){
		        	double temp[][][][] = new double[tileNum][3][dctBlockSize][dctBlockSize];
		        	imgList.add(temp);
		        }
				 DCT dct = new DCT();
				 dct.initMatrix();
				 for(int i=0; i < tileNum; i++)
					 for(int channel = 0; channel < 3; channel++){

						double output[] = dct.forwardDCT(tiles[i][channel]);
						for(int it = 0;it<dctBlockSize*dctBlockSize;it++){
							double temp[][][][];
							temp = imgList.get(it);
							temp[i][channel] = dct.inverseDCT(output,it+1);
						}
					 }
	     
	          	 DWT dwt = new DWT();
				 dwt.makeDWTmatrix(width, height);
				 double result[][] = new double[3][width*height];
				 
				 int level = (int)(Math.log(width)/Math.log(2));
				 
				 for(int channel = 0; channel < 3; channel++){
					  result[channel] = dwt.forwardDWT(level, width, height, YCbCrImage[channel]);
					  for(int it = 0;it<64;it++){
						  outputdata[it][channel] = dwt.inverseDWT(level, width, height, result[channel],4096*(it+1));
					  }
				 }
				 		 

	        }else{
	        	System.out.println("Error input");
	        }
	        
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		
		JFrame frame = new JFrame();
        Graphics2D g2 = newImage.createGraphics();
        g2.drawImage(dctImage, 0, 0, null);
        g2.drawImage(dwtImage, dctImage.getWidth()+20, 0, null);

	    label = new JLabel(new ImageIcon(newImage));
	    frame.getContentPane().add(label, BorderLayout.CENTER);
	    frame.pack();
	    frame.setVisible(true);
	    
	    if(Integer.parseInt(args[1]) == -1){
	    
        	int count = 0;
        	while(count < 64){
        		try {

        			constructImage(imgList.get(count));
    	    	    constructDWTImage(outputdata[count++]);

    	            g2.drawImage(dctImage, 0, 0, null);
    	            g2.drawImage(dwtImage, dctImage.getWidth()+20, 0, null);
    	            
            		label.repaint();
            		Thread.sleep(2000); 
    	    	} catch(InterruptedException ex) {
    	    	    Thread.currentThread().interrupt();
    	    	}
        		

        	}
	    }
	}
}
