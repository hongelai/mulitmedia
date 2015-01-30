import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;


public class EntropyUtils {
	public static int width = 352;
	public static int height = 288;
	public static final int BINS = 128;
	public static final float  log2 = (float) Math.log(2);
	public static final float MIN_VALUE = 0.0f;
	public static final float MAX_VALUE = 255.0f;
	
    public static float getEntropy(byte[] data) {
    	
    	Mat out = new Mat(height, width, CvType.CV_8UC3);

		out.put(0, 0, data);

    	MatOfInt mChannels[] = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
    	List<Mat> norm_hist = new ArrayList<Mat>();
    		
//		Core.split(out, channels);
    	
		Mat histR = new Mat();
		Mat histG = new Mat();
		Mat histB = new Mat();
		float entropy_r,entropy_g,entropy_b;
		
		Imgproc.calcHist(Arrays.asList( out ), mChannels[0], 
										new Mat(), histB, new MatOfInt(BINS), new MatOfFloat(MIN_VALUE, MAX_VALUE));		
		entropy_b = calcEntropy(histB);
		
		Imgproc.calcHist(Arrays.asList( out), mChannels[1], 
				 						new Mat(), histG, new MatOfInt(BINS), new MatOfFloat(MIN_VALUE, MAX_VALUE));
		entropy_g = calcEntropy(histG);
		
		Imgproc.calcHist(Arrays.asList( out), mChannels[2], 
										new Mat(), histR, new MatOfInt(BINS), new MatOfFloat(MIN_VALUE, MAX_VALUE));
		entropy_r = calcEntropy(histR);
		
		return entropy_r+entropy_g+entropy_b;
		
		
    }
    public static float calcEntropy(Mat hist){
    	
    	int imageSize = width*height;
    	float entr = 0.0f;
    	
    	for(int i = 0; i < hist.height(); i++){
    		
    		float frequency = (float) (hist.get(i, 0)[0] / imageSize + 0.000001);
    		entr -= frequency * (Math.log(frequency) / log2);
    		
    	}

    	return entr;
    }
}
