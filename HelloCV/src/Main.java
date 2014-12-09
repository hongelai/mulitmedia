import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Main {
	public static int width = 352;
	public static int height = 288;
	public static final int BINS = 32;
	public static final float MIN_VALUE = 0.0f;
	public static final float MAX_VALUE = 255.0f;
	public static final float threshold = 0.2f;
	public static final float secondThreshold = 0.8f;
	public static BufferedImage img = new BufferedImage(width,height, BufferedImage.TYPE_3BYTE_BGR);

	public BufferedImage extractImage(File file){
		
		BufferedImage img = new BufferedImage(width,height, BufferedImage.TYPE_3BYTE_BGR);
    	Mat out = new Mat(height, width, CvType.CV_8UC3);
    	try {
	    	InputStream is = new FileInputStream(file);
	
		    long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    
		    int offset = 0;
	        int numRead = 0;
	        
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			    offset += numRead;
			}
			
			byte[] data = new byte[width * height * (int)out.elemSize()];	
	        
	    	int ind = 0;
			for(int i = 0; i < width*height; i++){ 	
				data[i*3] = bytes[ind];
				data[i*3 + 1] = bytes[ind+height*width];
				data[i*3 + 2] = bytes[ind+height*width*2]; 
				ind++;
			}
			
			img.getRaster().setDataElements(0, 0, width, height, data); //convert to BufferedImage

			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return img;
	}
	
	
	
public BufferedImage MergeFilesVertical(List<File> files){
		
		BufferedImage img = new BufferedImage(width, height * 3, BufferedImage.TYPE_3BYTE_BGR);
    	Mat out = new Mat(height, width, CvType.CV_8UC3);
    	
    	byte[] data = new byte[width * height * (int)out.elemSize() * 3];
    	for(int m = 0; m < data.length; m++)			
		{
    		data[m] = (byte) 0xff;
		}
    	
    	int size = files.size();
    	if(size > 3)
    		size = 3;
    	
    	for(int i = 0; i< size; i++)
    	{
    		try {
    	    	InputStream is = new FileInputStream(files.get(i));
    	
    		    long len = files.get(i).length();
    		    byte[] bytes = new byte[(int)len];
    		    
    		    int offset = 0;
    	        int numRead = 0;
    	        
    			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
    			    offset += numRead;
    			} 				
    	        
    	    	int ind = 0;
    			for(int j = width*height*i; j < width*height*(i+1); j++){ 	
    				data[j*3] = bytes[ind];
    				data[j*3 + 1] = bytes[ind+height*width];
    				data[j*3 + 2] = bytes[ind+height*width*2]; 
    				ind++;
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    			
    		
    	}
    	
//    	try {
//	    	InputStream is = new FileInputStream(file);
//	
//		    long len = file.length();
//		    byte[] bytes = new byte[(int)len];
//		    
//		    int offset = 0;
//	        int numRead = 0;
//	        
//			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
//			    offset += numRead;
//			}
//			
//			byte[] data = new byte[width * height * (int)out.elemSize()];	
//	        
//	    	int ind = 0;
//			for(int i = 0; i < width*height; i++){ 	
//				data[i*3] = bytes[ind];
//				data[i*3 + 1] = bytes[ind+height*width];
//				data[i*3 + 2] = bytes[ind+height*width*2]; 
//				ind++;
//			}
//			
			img.getRaster().setDataElements(0, 0, width, height * 3, data); //convert to BufferedImage

			
		
    	return img;
	}
	
	
	 public HashMap getData() 
	 {
	    	
	        String path ="/Volumes/WORK/csci576/CS576_Project_Fall_2014/Dataset";
	        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	        HashMap<ImageFile, List<File> > table = new HashMap<ImageFile, List<File>>();
	        
	        File filePath = new File(path);
	        File[] files =  filePath.listFiles();
	        
//	        FaceDetector fd = new FaceDetector("haarcascade_frontalface_alt.xml");
	        
	      //Add faceDetective module
//	        List<File> faceList = new ArrayList<File>();
	        
	        for (File file : files) {
	            if (file.getName().equals(".DS_Store")) {
	                continue;
	            }
	            //sSystem.out.println("new file");
	            ImageFile target = new ImageFile(file);
	            target.calcHists();
//	            Mat targetMat = FeatureMatcher.calcMat(file);
	            
	            
	            
//	            if (fd.FaceDetective(targetMat) > 0)
//	            {
//	            	faceList.add(file);
//	            }
	            
	            
	            
	            if(table.isEmpty()){
	            	List<File> filelist = new ArrayList<File>();
	            	filelist.add(file);
	            	//target.imageMat = targetMat;  //only store the Mat for the first image of Genre for future calculation
	            	table.put(target, filelist);
	            }else{
	            	Iterator iter = table.entrySet().iterator();
	            	boolean foundGenre = false;
	            	
	            	while(iter.hasNext()){
	            		Map.Entry<ImageFile, List<File>> entry = (Map.Entry<ImageFile, List<File>>) iter.next();
	            		
	            		System.out.println();
	            		ImageFile src= entry.getKey();
	            		double diff = target.calcHistsDiff(src);
	            		
	            		
	            		if(diff < threshold){ // append to this entry

//	            			double fDiff = FeatureMatcher.calcFeatureDiff(src.imageMat,targetMat);
//	            			if(fDiff < secondThreshold){
	            				entry.getValue().add(file);
		            			foundGenre = true;
		            			break;
//	            			}
	            		}
	            	}
	            	if(!foundGenre){ //add one more entry;
	            		List<File> filenames = new ArrayList<File>();
	            		filenames.add(file);
	            		//target.imageMat = targetMat;
	        			table.put(target,filenames);
	            	}
	            	
//	            	if (faceList.size() > 1)
//	            	{
//		            	ImageFile face = new ImageFile(faceList.get(0));
//		            	table.put(face, faceList);
//	            	}
		            
	            }
	        }//for each file
	        
	        HashMap<Integer, List<File>> returnVal = new HashMap<Integer, List<File>>();
	        int ite = 0;
	        Iterator iter = table.entrySet().iterator(); 
	        while (iter.hasNext()) { 
	        	Map.Entry<ImageFile, List<File>> entry = (Map.Entry<ImageFile, List<File>>) iter.next();
	        	List<File> val = entry.getValue();
	        	returnVal.put(ite++, val);
	        }
	        
	    	return returnVal;

	    	
//	        JFrame frame = new JFrame();
//		    JLabel label = new JLabel(new ImageIcon(img));
//		    frame.getContentPane().add(label, BorderLayout.CENTER);
//		    frame.pack();
//		    frame.setVisible(true);
//		    
//	        Iterator it = table.entrySet().iterator();
//	        int count=0;
//	    	while(it.hasNext()){
//	    		Map.Entry<ImageFile, List<File>> entry = (Map.Entry<ImageFile, List<File>>) it.next();
//	    		for(File file: entry.getValue()){
//	    			System.out.print(file.getName()+" ");
//	    			extractImage(file);
//	    			label.repaint();
//	        		try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					} 
//	    			
//	    		}
//	    		System.out.println("end of class "+count++);
//	    	}
	   
	 
	 }
	
	
	
	
	
	
	
    public static void main(String[] args) {
    	
        String path ="/Volumes/WORK/csci576/CS576_Project_Fall_2014/Dataset";
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        HashMap<ImageFile, List<File> > table = new HashMap<ImageFile, List<File>>();
        
        File filePath = new File(path);
        File[] files =  filePath.listFiles();
        
//        FaceDetector fd = new FaceDetector("haarcascade_frontalface_alt.xml");
        
      //Add faceDetective module
//        List<File> faceList = new ArrayList<File>();
        
        for (File file : files) {
            if (file.getName().equals(".DS_Store")) {
                continue;
            }
            //sSystem.out.println("new file");
            ImageFile target = new ImageFile(file);
            target.calcHists();
//            Mat targetMat = FeatureMatcher.calcMat(file);
            
            
            
//            if (fd.FaceDetective(targetMat) > 0)
//            {
//            	faceList.add(file);
//            }
            
            
            
            if(table.isEmpty()){
            	List<File> filelist = new ArrayList<File>();
            	filelist.add(file);
            	//target.imageMat = targetMat;  //only store the Mat for the first image of Genre for future calculation
            	table.put(target, filelist);
            }else{
            	Iterator iter = table.entrySet().iterator();
            	boolean foundGenre = false;
            	
            	while(iter.hasNext()){
            		Map.Entry<ImageFile, List<File>> entry = (Map.Entry<ImageFile, List<File>>) iter.next();
            		
            		System.out.println();
            		ImageFile src= entry.getKey();
            		double diff = target.calcHistsDiff(src);
            		
            		
            		if(diff < threshold){ // append to this entry

//            			double fDiff = FeatureMatcher.calcFeatureDiff(src.imageMat,targetMat);
//            			if(fDiff < secondThreshold){
            				entry.getValue().add(file);
	            			foundGenre = true;
	            			break;
//            			}
            		}
            	}
            	if(!foundGenre){ //add one more entry;
            		List<File> filenames = new ArrayList<File>();
            		filenames.add(file);
            		//target.imageMat = targetMat;
        			table.put(target,filenames);
            	}
            	
//            	if (faceList.size() > 1)
//            	{
//	            	ImageFile face = new ImageFile(faceList.get(0));
//	            	table.put(face, faceList);
//            	}
	            
            }
        }//for each file
        
        JFrame frame = new JFrame();
	    JLabel label = new JLabel(new ImageIcon(img));
	    frame.getContentPane().add(label, BorderLayout.CENTER);
	    frame.pack();
	    frame.setVisible(true);
	    
        Iterator it = table.entrySet().iterator();
        int count=0;
    	while(it.hasNext()){
    		Map.Entry<ImageFile, List<File>> entry = (Map.Entry<ImageFile, List<File>>) it.next();
    		for(File file: entry.getValue()){
    			System.out.print(file.getName()+" ");
    			//extractImage(file);
    			//label.repaint();
        		try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
    			
    		}
    		System.out.println("end of class "+count++);
    	}
    }

}
