

import javax.swing.*;

import org.opencv.core.CvType;
import org.opencv.core.Mat;











import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MediaEntry {
	
	static int currentClass = -1;
	static int currentItem = -1;
	static int image_width = 352;
	static int image_height = 288;
	static int frame_width = 1200;
	static int frame_height = 1000;
	static float scale = 1.0f;
	static int window_width = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	static int window_height = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	
	/*************************new variables***************************/
	
	private JFrame aframe = new JFrame();
	static JPanel mainPanel = new JPanel();
	private JPanel menuPanel = new JPanel();
	
	private JButton backButton = new JButton("Back");
	private static JButton allButton = new JButton("All");
	private JButton lastButton = new JButton("Back Page");
	private JButton nextButton = new JButton("Next Page");
	private JButton zoomInButton = new JButton("Zoom In");
	private JButton zoomOutButton = new JButton("Zoom Out");
	private JButton playButton = new JButton("Play");
	
//	public HashMap<Integer,List<File>> my_hm;
	static VideoData my_vd;
	static Vector<List> my_hm;
	static ArrayList<File> my_fi;
	static myThread my_th;
		
	public MediaEntry()
	{
		ImportData();
		my_vd = new VideoData();
		my_vd.LoadAllFile("Dataset");
		my_fi = my_vd.allFiles;
	}
	
	public MediaEntry(String s)
	{
		
		
	}
	/*****************************************************************/
	
	public void ImportData()
	{
		Main mm = new Main();
//		this.my_hm =  mm.getData();
		
		DBSCAN dbs = new DBSCAN();
		this.my_hm = dbs.applyDbscan();
	}
	
	
	public void ImportImages()
	{
		Main mm = new Main();
		
		mainPanel.removeAll();
		int sizeOfImage = this.my_hm.size();
		
		for(int i=0;i<sizeOfImage;i++)
		{
			List<File> _file = this.my_hm.get(i);	
			BufferedImage img = new BufferedImage(image_width, image_height * 3,BufferedImage.TYPE_INT_RGB);
	    	img = mm.MergeFilesVertical(_file);
	    	
	    	JLabel alabel = new JLabel();
			alabel.setSize(352, 864);
			alabel.setIcon(new ImageIcon(img));
			alabel.setName(String.valueOf(i));
			alabel.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					currentClass = Integer.parseInt(((JLabel) (e.getSource())).getName());
					ShowImages(currentClass);
				}
			  });
			alabel.addMouseMotionListener(new MouseAdapter(){  
			       public void mouseMoved(MouseEvent e) {  
			    	   ((JLabel) (e.getSource())).setToolTipText("Group " + ((JLabel) (e.getSource())).getName());			            
			       }  
			   });
			
			mainPanel.add(alabel);
			
		}
		
	    JLabel alabel = new JLabel();
		alabel.setSize(352, 864);
		BufferedImage img_temp = new BufferedImage(image_width, image_height * 3, BufferedImage.TYPE_INT_RGB);
		img_temp = MergeVideoImages();
		alabel.setIcon(new ImageIcon(img_temp));
		alabel.setName(String.valueOf(sizeOfImage+1));
		alabel.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				currentClass = Integer.parseInt(((JLabel) (e.getSource())).getName());
				ShowVideos();
			}
		});
		alabel.addMouseMotionListener(new MouseAdapter(){  
			public void mouseMoved(MouseEvent e) {  
			    ((JLabel) (e.getSource())).setToolTipText("Videos");			            
			}  
		});
			mainPanel.add(alabel);
		
		
		mainPanel.updateUI();
		allButton.setVisible(false);
		
	}
	
	public void ScaleImage(int index, int ind, float s)
	{
		int width = (int) (image_width * s);
		int height = (int) (image_height * s);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);;
		try {		    	
			
			List<File> _file = this.my_hm.get(index);
			int size = _file.size();
			if(ind < 0)
			{
				currentItem = 0;
				return;
			}			
			if(ind >= size)
			{
				currentItem = size - 1;
				return;
			}
			
			File file =_file.get(ind);
			InputStream is = new FileInputStream(file);
		    long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    
		    int offset = 0;
	        int numRead = 0;
	        
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			    offset += numRead;
			} 				
	        
			for(int j = 0; j < image_height; j ++){
				for(int i = 0; i < image_width; i ++){
					byte r = bytes[j * image_width + i];
					byte g = bytes[(j * image_width + i)+image_height*image_width];
					byte b = bytes[(j * image_width + i)+image_height*image_width*2]; 
					int pixel = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					img.setRGB(i, j, pixel);
					ind ++;
				}
			}
			
			is.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel alabel = new JLabel();		
		alabel.setIcon(new ImageIcon(img));
		mainPanel.removeAll();
		mainPanel.add(alabel);
		mainPanel.updateUI();	
	}
	
	public static void ShowVideos()
	{	
		int size = my_vd.images.size();
		int width = image_width * ((size + 2) / 3);
		int height = image_height * 3;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < size; i ++)
		{	
			for(int m = 0; m < image_height; m ++){
				for(int n = 0; n < image_width; n ++){
					img.setRGB(i/3*image_width+n, i%3*image_height+m, my_vd.images.get(i).getRGB(n, m));
				}
			}		
		}
		JLabel alabel = new JLabel();
		alabel.setIcon(new ImageIcon(img));
		alabel.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				int x_position = e.getX();
				int y_position = e.getY();
				int ind = y_position / image_height + x_position / image_width * 3 ;
				currentItem = ind;
				my_th = new myThread(currentItem);
				my_th.start();
			}
		});
		allButton.setVisible(true);
		mainPanel.removeAll();
		mainPanel.add(alabel);
		mainPanel.updateUI();
		
	}
	
	public void ShowImages(int index)
	{		
		JLabel alabel = new JLabel();		
		List<File> _file = this.my_hm.get(index);		
		int size = _file.size();
		int width;
		int height;
		int single_width;
		int single_height;
		if(size >9)
		{
			single_width = image_width / 2;
			single_height = image_height / 2;
			width = single_width * ((size + 5) / 6);
			height = single_height * 6;
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			
			for(int i = 0; i < size; i ++)
			{			  	    	
				try {		    	
					InputStream is = new FileInputStream(_file.get(i));
				    long len = _file.get(i).length();
				    byte[] bytes = new byte[(int)len];
				    
				    int offset = 0;
			        int numRead = 0;
			        
					while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
					    offset += numRead;
					} 				
			        
					int ind = 0;
					for(int m = 0; m < image_height; m += 2){
						for(int n = 0; n < image_width; n += 2){
							byte r = bytes[m * image_width + n];
							byte g = bytes[(m * image_width + n)+image_height*image_width];
							byte b = bytes[(m * image_width + n)+image_height*image_width*2]; 
							int pixel = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
							img.setRGB(i/6*single_width+n/2, i%6*single_height+m/2, pixel);
							ind ++;
						}
					}
					
					is.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
				
			alabel.setIcon(new ImageIcon(img));
			alabel.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					int x_position = e.getX();
					int y_position = e.getY();
					int ind = y_position / (image_height / 2) + x_position / (image_width / 2) * 6 ;
					currentItem = ind;
					scale = 1.0f;
					ShowSingleImage(currentClass, currentItem, scale);
				}
			  });
			alabel.addMouseMotionListener(new MouseAdapter(){  
			       public void mouseMoved(MouseEvent e) {  
			    	   ((JLabel) (e.getSource())).setToolTipText("Group " + String.valueOf(currentClass));			            
			       }  
			  });
		}
		else
		{
			single_width = image_width;
			single_height = image_height;
			width = single_width * ((size + 2) / 3);
			height = single_height * 3;
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			
			for(int i = 0; i < size; i ++)
			{			  	    	
				try {		    	
					InputStream is = new FileInputStream(_file.get(i));
				    long len = _file.get(i).length();
				    byte[] bytes = new byte[(int)len];
				    
				    int offset = 0;
			        int numRead = 0;
			        
					while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
					    offset += numRead;
					} 				
			        
					int ind = 0;
					for(int m = 0; m < image_height; m ++){
						for(int n = 0; n < image_width; n ++){
							byte r = bytes[m * image_width + n];
							byte g = bytes[(m * image_width + n)+image_height*image_width];
							byte b = bytes[(m * image_width + n)+image_height*image_width*2]; 
							int pixel = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
							img.setRGB(i/3*single_width+n, i%3*single_height+m, pixel);
							ind ++;
						}
					}
					
					is.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
				
			alabel.setIcon(new ImageIcon(img));
			alabel.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					int x_position = e.getX();
					int y_position = e.getY();
					int ind = y_position / image_height + x_position / image_width * 3 ;
					currentItem = ind;
					scale = 1.0f;
					ShowSingleImage(currentClass, currentItem, scale);
				}
			  });
			alabel.addMouseMotionListener(new MouseAdapter(){  
			       public void mouseMoved(MouseEvent e) {  
			    	   ((JLabel) (e.getSource())).setToolTipText("Group " + String.valueOf(currentClass));			            
			       }  
			  });
		}
		
		
		lastButton.setVisible(false);
		nextButton.setVisible(false);
		allButton.setVisible(true);
		backButton.setVisible(false);
		zoomInButton.setVisible(false);
		zoomOutButton.setVisible(false);
		mainPanel.removeAll();
		mainPanel.add(alabel);
		mainPanel.updateUI();
		
		
	}
	
	public BufferedImage MergeVideoImages()
	{
		BufferedImage img = new BufferedImage(image_width, image_height * 3, BufferedImage.TYPE_INT_RGB);
		int size = this.my_vd.images.size();
		if(size>3)
			size = 3;
		for(int i=0; i<size; i++)
		{
			for(int n=0; n<image_height; n++)
				for(int m=0; m<image_width; m++)
				{
					img.setRGB(m, i*image_height + n, this.my_vd.images.get(i).getRGB(m, n));
				}
		}
		return img;
	}
/*
	public static void VideoPlay(File file)
	{
				int frequency = 30;
				int width = 352;
				int height = 288;
				
			    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			    try {
				    InputStream is = new FileInputStream(file);

				    long len = file.length();
				    byte[] bytes = new byte[(int)len];
				    
				    int offset = 0;
			        int numRead = 0;

			        JLabel label = new JLabel(new ImageIcon(img));
			        mainPanel.removeAll();
			        mainPanel.add(label);
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

						
						label.setIcon(new ImageIcon(img));
						label.updateUI();
					}		
					is.close();
					
					
			    } catch (FileNotFoundException e) {
			      e.printStackTrace();
			    } catch (IOException e) {
			      e.printStackTrace();
			    }
			    

	}
*/	
	public static void PlayVideo (int index)
	{		
		File file = my_fi.get(index);
		VideoReader avideo = new VideoReader(file, image_width, image_height);
		List<BufferedImage> imgList = new ArrayList<BufferedImage>(avideo.videoToImgList(file));
		BufferedImage img = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_RGB);
		int size = imgList.size();
			
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); 
		JLabel label = new JLabel(new ImageIcon(img));
		frame.getContentPane().add(label,BorderLayout.CENTER);
        frame.pack();	
        frame.setVisible(true);
        int width = frame.getWidth();
        int height = frame.getHeight();
        
        frame.setLocation((window_width - width) / 2, (window_height - height) / 2);
        
        Calendar ct = Calendar.getInstance();		 		    
	    long mi = ct.getTimeInMillis();
		int interval = 1000/30;
        
		for(int i=0; i<size; i++)
		{	
			
			while(((Calendar.getInstance().getTimeInMillis()) - interval) < mi){}
			mi = Calendar.getInstance().getTimeInMillis();
			//img = imgList.get(i);
			label.setIcon(new ImageIcon(imgList.get(i)));
			label.repaint();
			
		}
		
	}
	
	public void ShowSingleImage(int index, int ind, float s)
	{
		List<File> _file = this.my_hm.get(index);
		int size = _file.size();
		File file =_file.get(ind);
		long len = file.length();
		if(len > image_width * image_height * 3)
		{
			//VideoPlay(file);
			PlayVideo(ind);
			return;
		}
		
		if(s>3)
		{
			scale = 3.0f;
			s = scale;
		}
		if(s<0.2)
		{
			scale = 0.2f;
			s = scale;
		}
		
		int width = (int) (image_width * s);
		int height = (int) (image_height * s);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);;
		BufferedImage pre_img = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_RGB);;
		try {		    	
			
			
			
			if(ind < 0)
			{
				currentItem = 0;
				return;
			}			
			if(ind >= size)
			{
				currentItem = size - 1;
				return;
			}
			
			InputStream is = new FileInputStream(file);
		    byte[] bytes = new byte[(int)len];
		    
		    int offset = 0;
	        int numRead = 0;
	        
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			    offset += numRead;
			} 				
	        
			for(int j = 0; j < image_height; j ++){
				for(int i = 0; i < image_width; i ++){
					byte r = bytes[j * image_width + i];
					byte g = bytes[(j * image_width + i)+image_height*image_width];
					byte b = bytes[(j * image_width + i)+image_height*image_width*2]; 
					int pixel = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					pre_img.setRGB(i, j, pixel);
					ind ++;
				}
			}
			
			is.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JLabel alabel = new JLabel();	
		if(s == 1.0f)
			alabel.setIcon(new ImageIcon(pre_img));
		else
		{
			img = ImgProcess.scale(pre_img, s, s);
			alabel.setIcon(new ImageIcon(img));
		}
		lastButton.setVisible(true);
		nextButton.setVisible(true);
		backButton.setVisible(true);
		allButton.setVisible(true);
		zoomInButton.setVisible(true);
		zoomOutButton.setVisible(true);
		mainPanel.removeAll();
		mainPanel.add(alabel);
		mainPanel.updateUI();		
	}
	
	public void ShowSingleVideo(int index)
	{
		JLabel alabel = new JLabel();				
		int size= this.my_vd.videos.get(index).frameNum;
		int width;
		int height;
		int single_width = image_width;
		int single_height = image_height;
		width = single_width * ((size + 2) / 3);
		height = single_height * 3;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		//img.setRGB(i/3*single_width+n, i%3*single_height+m, pixel);
		ExtractVideo ev = this.my_vd.videos.get(index);
		//this.my_vd.videos.get(index);
		//ev._images.size()
		for(int i = 1; i < 10; i ++)
		{	
			for(int m = 0; m < image_height; m++){
				if(m==10)
				{
					System.out.println("run here");
				}
				for(int n = 0; n < image_width; n++){
					img.setRGB(i/3*single_width+n, i%3*single_height+m, ev.DataToBufferedImage(ev._images.get(i)).getRGB(n, m));
				}
			}
			
			
			
			
			
//			    byte[] bytes = this.my_vd.videos.get(index)._images.get(i);			    
//			    int offset = 0;
//		        int numRead = 0;				
//		        
//				int ind = 0;
//				for(int m = 0; m < image_height; m ++){
//					for(int n = 0; n < image_width; n ++){
//						byte r = bytes[m * image_width + n];
//						byte g = bytes[(m * image_width + n)+image_height*image_width];
//						byte b = bytes[(m * image_width + n)+image_height*image_width*2]; 
//						int pixel = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
//						
//						ind ++;
//					}
//				}
		}
		alabel.setIcon(new ImageIcon(img));
		mainPanel.removeAll();
		mainPanel.add(alabel);
		mainPanel.updateUI();
	    
	}
	
	public void InitializeUI()
	{
		aframe.setSize(frame_width, frame_height);
		aframe.setLocation((window_width - frame_width) / 2, (window_height - frame_height) / 2);
		aframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aframe.setLayout(null);
		aframe.setVisible(true);
			
		mainPanel.setBounds(0, 0, aframe.getWidth(), 900);
		
		JScrollPane scroll = new JScrollPane(mainPanel);
		scroll.setBounds(0, 0, aframe.getWidth(), 900);
		scroll.getHorizontalScrollBar().setUnitIncrement(30);
		
		menuPanel.setBounds(0, scroll.getHeight() + 20, aframe.getWidth(), 50);
		menuPanel.setLayout(null);
		
		allButton.setBounds(50, 0, 100, 30);
		backButton.setBounds(allButton.getLocation().x + allButton.getWidth() + 50, 0, 100, 30);
		lastButton.setBounds(backButton.getLocation().x + backButton.getWidth() + 100, 0, 100, 30);
		nextButton.setBounds(lastButton.getLocation().x + lastButton.getWidth() + 50, 0, 100, 30);
		zoomInButton.setBounds(nextButton.getLocation().x + nextButton.getWidth() + 50, 0, 100, 30);
		zoomOutButton.setBounds(zoomInButton.getLocation().x + zoomInButton.getWidth() + 50, 0, 100, 30);
		playButton.setBounds(zoomOutButton.getLocation().x + zoomOutButton.getWidth() + 50, 0, 100, 30);
		
		backButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
            	ShowImages(currentClass);
            }
        });
		
		allButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
            	ImportImages();
            }
        });
		
		
		lastButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
            	currentItem --;
            	scale = 1.0f;
            	ShowSingleImage(currentClass, currentItem, 1);
            }
        });
		nextButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
            	currentItem ++;
            	scale = 1.0f;
            	ShowSingleImage(currentClass, currentItem, 1);
            }
        });
		
		zoomInButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
            	scale += 0.2;
            	ShowSingleImage(currentClass, currentItem, scale);
            }
        });
		
		zoomOutButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
            	scale -= 0.2;
            	ShowSingleImage(currentClass, currentItem, scale);
            }
        });
		
		playButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
            	//File file = new File("Dataset/video05.rgb");
            	//myThread mythread = new myThread(file);
            	//mythread.start();
            }
        });
		
		allButton.setVisible(false);
		backButton.setVisible(false);
		lastButton.setVisible(false);
		nextButton.setVisible(false);
		zoomInButton.setVisible(false);
		zoomOutButton.setVisible(false);
		playButton.setVisible(false);
		
		menuPanel.add(allButton);
		menuPanel.add(backButton);
		menuPanel.add(lastButton);
		menuPanel.add(nextButton);
		menuPanel.add(zoomInButton);
		menuPanel.add(zoomOutButton);
		menuPanel.add(playButton);
		
		aframe.add(scroll);
		aframe.add(menuPanel);

	}
		
	public static void main(String[] args) {
		MediaEntry entry = new MediaEntry();
		entry.InitializeUI();
		entry.ImportImages();
		//File file = new File("Dataset/Video05.rgb");
		
		//imageReader.videoRead(file);
	}

}


