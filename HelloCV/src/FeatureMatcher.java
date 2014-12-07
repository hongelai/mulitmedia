import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

public class FeatureMatcher {
	public static int width = 352;
	public static int height = 288;
	public static double dist_threshold = 0.2;
	
    public static Mat calcMat(File file) {
    	
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
			//img.getRaster().setDataElements(0, 0, width, height, data); //convert to BufferedImage
			is.close();
			out.put(0, 0, data);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

        return out;
    }
    
	public static double calcFeatureDiff(Mat image1, Mat image2){
 
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
        detector.detect(image1, keypoints1);
        detector.detect(image2, keypoints2);

        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();
        long time3=System.currentTimeMillis(); 
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
        extractor.compute( image1, keypoints1, descriptors1 );
        extractor.compute( image2, keypoints2, descriptors2 );

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);

        List<DMatch> matchList = matches.toList();
        Iterator it = matchList.iterator(); 
        double min_dist = 100;
        int totalPoints = 0;
        while(it.hasNext()) 
        {
              DMatch element = (DMatch) it.next(); 
              double dist = element.distance;
              if( dist < min_dist ) min_dist = dist;
              totalPoints++;
              
        }
        Iterator it2 = matchList.iterator(); 
        int goodMatch = 0;
        while(it2.hasNext()) 
        {
              DMatch element = (DMatch) it2.next(); 
              double dist = element.distance;
              if( dist <= 0.2 ) goodMatch++;
              
        }
        double percent = (double)goodMatch/totalPoints;

        return 1 - percent;
	}
}
