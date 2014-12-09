
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ExtractVideo {

	public int width = 352;
	public int height = 288;
	public int frameNum = 300;
	public String filePath = "";
	public ArrayList<byte []> _images = new ArrayList<byte[]>();
	public double[][] bytes = null;
	
	public BufferedImage ExtractImage()
	{
    	BufferedImage _img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
    	float MaxEntropy = 0;
    	int MaxIndex = 0;
    	
    	for(int i=0;i<this._images.size();i++)
    	{
    		float ent = EntropyUtils.getEntropy(_images.get(i));
    		if (MaxEntropy < ent)
    			{
    				MaxEntropy = ent;
    				MaxIndex = i;
    			}
    	}
    	return DataToBufferedImage(_images.get(MaxIndex));
	}
	
    public Mat BufferImageToMat(byte[] bi)
    {
    	Mat out = new Mat(height, width, CvType.CV_8UC3);
    	out.put(0, 0, bi);
    	return out;
    }
    
	public void LoadFiles(File _imageFile)
	{
        //this.img = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);

        //Each pic contain 3 channels of R, G, B.
        byte[] _bytes = null;

        try {

            InputStream is = new FileInputStream(_imageFile);

            long len = _imageFile.length();
            _bytes = new byte[(int)len];

            int offset = 0;
            int numRead = 0;
            while (offset < _bytes.length && (numRead=is.read(_bytes, offset, _bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        for(int j=0;j<this.frameNum;j++)
        {
	        byte[] data = new byte[width * height * 3];	 
	    	int ind = 0;
			for(int i = 0; i < width*height; i++){ 	
				data[i*3] = _bytes[ind
				                   + j*height*width*3];
				data[i*3 + 1] = _bytes[ind+height*width
				                   + j*height*width*3];
				data[i*3 + 2] = _bytes[ind+height*width*2
				                   + j*height*width*3]; 
				ind++;
			}
			
			this._images.add(data);
        }
        
        
//        for(int j=0;j<this.frameNum;j++)
//        {
//        	BufferedImage _img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
//        	for(int m = 0; m<this.height;m++)
//        	{
//        		for(int n=0;n<this.width;n++)
//        		{
//        			int r = _bytes[n + m*this.width  
//        			               + j * this.width * this.height * 3]&0xFF;
//        			int g = _bytes[n + m*this.width + this.width*this.height
//        			               + j * this.width * this.height * 3]&0xFF;
//        			int b = _bytes[n + m*this.width + 2*this.width*this.height
//        			               + j * this.width * this.height * 3]&0xFF;
//			
//        			int pix = ((r & 0xff) << 16) |
//                            ((g & 0xff) << 8) | (b & 0xff);
//        			
//                    _img.setRGB(n, m, pix);
//        		}
//        	}
//        	
//        	this._images.add(_img);
//        }
	}
	
	public BufferedImage DataToBufferedImage(byte[] _in)
	{
    	BufferedImage _img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
    	
    	
    	int ind = 0;
		for(int i = 0; i < width*height; i++){ 	
			int r = _in[i*3]&0xFF;
			int g = _in[i*3 + 1]&0xFF;
			int b = _in[i*3 + 2]&0xFF;
			ind++;
			int pix = ((r & 0xff) << 16) |
                  ((g & 0xff) << 8) | (b & 0xff);
			_img.setRGB(i%width, i/width, pix);
		}
    	
//    	for(int m = 0; m<this.height;m++)
//        	{
//        		for(int n=0;n<this.width;n++)
//        		{
//        			int r = _in[n + m*this.width]&0xFF;
//        			int g = _in[n + m*this.width + this.width*this.height]&0xFF;
//        			int b = _in[n + m*this.width + 2*this.width*this.height]&0xFF;
//			
//        			int pix = ((r & 0xff) << 16) |
//                            ((g & 0xff) << 8) | (b & 0xff);
//        			
//                    _img.setRGB(n, m, pix);
//        		}
//        	}
    	return _img;

	}
	
	
	public void DispPicByImg(JFrame frame, BufferedImage img)
	{
		frame.setTitle("Theme");
        frame.setLocation(200, 200);
        JLabel label = new JLabel();
        label.removeAll();
        label.setIcon(new ImageIcon(img));
        label.updateUI();
        
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
	}
	
    public void DispPic(JFrame _frame, int i)
    {
    	JFrame frame = new JFrame();
//        frame.setTitle(String.valueOf(i));
//        frame.setLocation(500, 500);
//        frame.removeAll();
//        JLabel label = new JLabel(new ImageIcon(this._images.get(i)));
//        frame.getContentPane().add(label, BorderLayout.CENTER);
//        frame.pack();
//        frame.setVisible(true);
        
        frame.setTitle(String.valueOf(i));
        frame.setLocation(200, 200);
        JLabel label = new JLabel();
        label.removeAll();
        label.setIcon(new ImageIcon(this.DataToBufferedImage(_images.get(i))));
        label.updateUI();
        
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExtractVideo ev = new ExtractVideo();
		ev.LoadFiles(new File("Dataset/video05.rgb"));
		JFrame frame = new JFrame();
		
//		ev.DispPicByImg(frame, ev.ExtractImage());
        frame.setVisible(true);
        
		for(int i=0;i<100;i++)
		{
			ev.DispPic(frame, i);
			try {
			    Thread.sleep(100);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	}

}
