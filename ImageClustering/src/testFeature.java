import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;


public class testFeature {

	public static void main(String[] args){
		
		String path1 ="/Volumes/WORK/csci576/CS576_Project_Fall_2014/Dataset/image150.rgb";
        String path2 ="/Volumes/WORK/csci576/CS576_Project_Fall_2014/Dataset/image153.rgb";
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        File file1 = new File(path1);
        File file2 = new File(path2);
        FeatureMatcher matcher = new FeatureMatcher();
		Mat image1 = matcher.calcMat(file1);
		Mat image2 = matcher.calcMat(file2);
		double diff= matcher.calcFeatureDiff(image1,image2);
		System.out.println(diff);
	}
}
