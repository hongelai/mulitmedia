
import java.awt.image.BufferedImage;

public class ImgProcess {

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
}
