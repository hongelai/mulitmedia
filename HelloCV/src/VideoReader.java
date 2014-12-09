
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VideoReader {

	private File file;
	private int width;
	private int height;
	
	public VideoReader(File file, int width, int height)
	{
		this.file = file;
		this.width = width;
		this.height = height;
	}
	
	public List<BufferedImage> videoToImgList(File file)
	{
		List<BufferedImage> imgList = new ArrayList<BufferedImage>();
		try {	
		    InputStream is = new FileInputStream(file);

		    long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    
		    int offset = 0;
	        int numRead = 0;		    		 		   			
						
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
			
			long color_dis = len/3;
			int numberOfFrame = (int)color_dis/(width*height);
			
			for(int k = 0; k < numberOfFrame; k++)
			{
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				int ind = 0;
				int start = k*width*height*3;
				for(int y = 0; y < height; y++){
					
					for(int x = 0; x < width; x++){
				 
						byte a = 0;
						byte r = bytes[ind+start];
						byte g = bytes[ind+start+width*height];
						byte b = bytes[ind+start+width*height*2]; 
						
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						img.setRGB(x, y, pix);						
						ind++;
					}
				}	
				imgList.add(img);
			}									
			
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		
		
		return imgList;
		
		
	}
	
}

