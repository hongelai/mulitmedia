
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Calendar;

import javax.swing.*;



public class imageReader {
	
		
	public static void videoRead(File file) {
	   	
		float sX = 1;
		float sY = 1;
		int frequency = 30;
		int antiAliasing =  0;
		int analysis = 0;
		
		int width = 352;
		int height = 288;
		
	    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    BufferedImage img_form = new BufferedImage((int)(width*sX), (int)(height*sY), BufferedImage.TYPE_INT_RGB);

	    try {
		    InputStream is = new FileInputStream(file);

		    long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    
		    int offset = 0;
	        int numRead = 0;

	        JFrame frame = new JFrame();
	        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
	        JLabel label = new JLabel(new ImageIcon(img_form));
	        frame.getContentPane().add(label,BorderLayout.CENTER);
	        frame.pack();			
		
	        frame.setVisible(true);
		    
		    Calendar ct = Calendar.getInstance();		 		    
		    long mi = ct.getTimeInMillis();
			int interval = 1000/frequency;
			
						
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
			
			long color_dis = len/3;
			int numberOfFrame = (int)color_dis/(width*height);
			
			for(int k = 0; k < numberOfFrame; k++)
			{
				int ind = 0;
				int start = k*width*height*3;
				for(int y = 0; y < height; y++){
					
					for(int x = 0; x < width; x++){
				 
						byte a = 0;
						byte r = bytes[ind+start];
						byte g = bytes[ind+start+width*height];
						byte b = bytes[ind+start+width*height*2]; 
						
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						img.setRGB(x,y,pix);						
						ind++;
					}
				}
				//frequency
				while(((Calendar.getInstance().getTimeInMillis()) - interval) < mi){}
				mi = Calendar.getInstance().getTimeInMillis();				
				
				switch(analysis)
				{
					case 0: img_form = scale(img, sX, sY); break;
					case 1: img_form = ratioAnalysis(img,sX,sY,0); break;
					case 2: break;//Extra Credit
				}
				
				if(antiAliasing==1)
					img_form = average(img_form);
				
				label.setIcon(new ImageIcon(img_form));
				label.updateUI();
			}						
			
			
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    

	}
	
	public static BufferedImage scale(BufferedImage img, float sX, float sY)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		int new_width = (int) ((float)width * sX);
		int new_height = (int) ((float)height * sY);		
		BufferedImage new_image_temp = new BufferedImage(new_width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage new_image = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
		int pointer = 0;
		for(int i = 0; i < width; i++)
		{
			int new_i = (int)((float)(i+1) * sX);
			while(new_i > pointer)
			{
				for(int j = 0; j < height; j++)
				{
					new_image_temp.setRGB(pointer, j, img.getRGB(i, j));
				}
				pointer ++;
				
			}
		}		
		
		pointer = 0; 
		for(int j = 0; j < height; j++)
		{
			int new_j = (int)((float)(j+1) * sY);
			while(new_j > pointer)
			{
				for(int i = 0; i < new_width; i++)
				{
					new_image.setRGB(i, pointer, new_image_temp.getRGB(i, j));
				}
				pointer ++;
				
			}
		}		
		
		return new_image;
	}

	
	public static BufferedImage average(BufferedImage img)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage new_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int j = 1; j < height-1; j ++)
		{
			for(int i = 1; i < width-1; i ++)
			{
				int pix = 0x00000000;
				int r = 0;
				int g = 0;
				int b = 0;
				for(int m = -1; m < 2; m++)
				{
					for(int n = -1; n < 2; n++)
					{
						pix = img.getRGB(i+n, j+m);
						r += (pix & 0x00ff0000) >> 16;
						g += (pix & 0x0000ff00) >> 8;
						b += (pix & 0x000000ff); 
					}
				}
				int rA = r/9;
				int gA = g/9;
				int bA = b/9;
				int pixA = 0xff000000 | ((rA & 0xff) << 16) | ((gA & 0xff) << 8) | (bA & 0xff);
				new_image.setRGB(i, j, pixA);
			}
		}
		
		for(int j=0; j<height; j++)
		{
			new_image.setRGB(0,j,img.getRGB(0, j));
			new_image.setRGB(width-1, j, img.getRGB(width-1, j));
		}
		for(int i=1; i<width-1; i++)
		{
			new_image.setRGB(i, 0,img.getRGB(i, 0));
			new_image.setRGB(i, height-1, img.getRGB(i, height-1));
		}
		
		
		return new_image;
		
	}
	
	public static BufferedImage ratioAnalysis(BufferedImage img, float sX, float sY, int type)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		int new_width = (int)(width*sX);
		int new_height = (int)(height*sY);
		BufferedImage new_imageX = new BufferedImage(new_width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage new_imageY = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
		int pointer = 0;
		
		switch(type)
		{
		case 0://solition 1. (x-a)^2+(y-b)^2=R^2
			
			float a=0, b=0, R=0;			
			pointer = 0;
			//width process
			if(width == new_width)
				new_imageX = img;
			else
			{
				if(width > new_width)
				{
					R = (float) (Math.sqrt(2) * (Math.pow(width,2)+Math.pow(new_width,2))/(4*(width-new_width)));
					a = (float) (width/2 - Math.sqrt(2)*R/2);
					b = (float) (new_width/2 + Math.sqrt(2)*R/2);
				}
				if(width < new_width)
				{
					R = (float) (Math.sqrt(2) * (Math.pow(width,2)+Math.pow(new_width, 2))/(4*(new_width-width)));
					a = (float) (width/2 + Math.sqrt(2)*R/2);
					b = (float) (new_width/2 - Math.sqrt(2)*R/2);
				}		
						
				pointer = new_width/2;
				for(int i = width/2; i >= 0; i--)
				{
					int new_i;
					if(width < new_width)
						new_i = (int) (Math.sqrt(Math.pow(R, 2)-Math.pow(i-a, 2))+b);
					else
						new_i = (int) (-Math.sqrt(Math.pow(R, 2)-Math.pow(i-a, 2))+b);
					while(new_i < pointer && pointer >= 0)
					{
						for(int j = 0; j < height; j++)
						{
							new_imageX.setRGB(pointer, j, img.getRGB(i, j));
							new_imageX.setRGB(new_width-pointer-1, j, img.getRGB(width-i-1, j));
						}
						pointer --;						
					}
				}
			}
			//height process			
			pointer = 0;
			if(height == new_height)
			{
				new_imageY = new_imageX;
			}
			else
			{
				if(height > new_height)
				{
					R = (float) (Math.sqrt(2) * (Math.pow(height,2)+Math.pow(new_height,2))/(4*(height-new_height)));
					a = (float) (height/2 - Math.sqrt(2)*R/2);
					b = (float) (new_height/2 + Math.sqrt(2)*R/2);
				}
				else
				{
					R = (float) (Math.sqrt(2) * (Math.pow(height,2)+Math.pow(new_height, 2))/(4*(new_height-height)));
					a = (float) (height/2 + Math.sqrt(2)*R/2);
					b = (float) (new_height/2 - Math.sqrt(2)*R/2);
				}		
				
				pointer = new_height/2;
				for(int j = height/2; j >= 0; j--)
				{
					int new_j;
					if(height < new_height)
						new_j = (int) (Math.sqrt(Math.pow(R, 2)-Math.pow(j-a, 2))+b);
					else
						new_j = (int) (-Math.sqrt(Math.pow(R, 2)-Math.pow(j-a, 2))+b);
					while(new_j < pointer && pointer >= 0)
					{
						for(int i = 0; i < new_width; i++)
						{
							new_imageY.setRGB(i, pointer, new_imageX.getRGB(i, j));
							new_imageY.setRGB(i, new_height-pointer-1, new_imageX.getRGB(i, height-j-1));
						}
						pointer --;						
					}
				}
			}
			break;
			
		case 1://solution 2. Two segments	
			
			float ratio = (float) 0.7;
			//width process
			float sX_left = (new_width-width*ratio)/(width-width*ratio);
			pointer = 0;
			for(int i = 0; i < width*(1-ratio)/2; i++)
			{
				int new_i = (int)((float)(i+1) * sX_left);
				while(new_i > pointer)
				{
					for(int j = 0; j < height; j++)
					{
						new_imageX.setRGB(pointer, j, img.getRGB(i, j));
						new_imageX.setRGB(new_width-pointer-1, j, img.getRGB(width-i-1, j));
					}
					pointer ++;
					
				}
			}
			for(int i = (int)(width-width*ratio)/2; i < (int)(width+width*ratio)/2; i++)
			{
				for(int j = 0; j < height; j++)
				{
					new_imageX.setRGB(pointer, j, img.getRGB(i, j));
				}
				pointer ++;
			}
			
			//height process
			float sY_up = (new_height-height*ratio)/(height-height*ratio);
			pointer = 0;
			for(int j = 0; j < height*(1-ratio)/2; j++)
			{
				int new_j = (int)((float)(j+1) * sY_up);
				while(new_j > pointer)
				{
					for(int i = 0; i < new_width; i++)
					{
						new_imageY.setRGB(i, pointer, new_imageX.getRGB(i, j));
						new_imageY.setRGB(i, new_height-pointer-1, new_imageX.getRGB(i, height-j-1));
					}
					pointer ++;
					
				}
			}
			for(int j = (int)(height-height*ratio)/2; j < (int)(height+height*ratio)/2; j++)
			{
				for(int i = 0; i < new_width; i++)
				{
					new_imageY.setRGB(i, pointer, new_imageX.getRGB(i, j));
				}
				pointer ++;
			}
			break;
		}
	
		return new_imageY;
	}
}


