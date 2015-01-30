import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;


public class HistogramUtils {
	public static int width = 352;
	public static int height = 288;
	public static final int BINS = 128;
	public static final float MIN_VALUE = 0.0f;
	public static final float MAX_VALUE = 255.0f;
	public static final float  log2 = (float) Math.log(2);
	public static HashMap<File, Float[]> entropy_table = new HashMap<File, Float[]>();
	
    public List<Mat> calcHists(File file) 
    {	
    	Mat out = new Mat(height, width, CvType.CV_8UC3);

    	try {

	    	InputStream is = new FileInputStream(file);
		    long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    int offset = 0;
	        int numRead = 0;
	        byte[] data = new byte[width * height * (int)out.elemSize()];
	    	int ind = 0;

			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length-offset)) >= 0) 
			{
			    offset += numRead;
			}

			for(int i = 0; i < width*height; i++)
			{ 	
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

        return calcHists(out, file);
    }
    
    public List<Mat> calcHists(Mat out,File file) 
    {	
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
		
		entropy_table.put(file,new Float[] {entropy_r,entropy_g,entropy_b});

		Core.normalize(histB, histB, 0,1, Core.NORM_MINMAX, -1, new Mat());
		histB = fixOverflow(histB);
		norm_hist.add(histB);
		
		Core.normalize(histG, histG, 0,1, Core.NORM_MINMAX, -1, new Mat());
		histG = fixOverflow(histG);
		norm_hist.add(histG);

		Core.normalize(histR, histR, 0,1, Core.NORM_MINMAX, -1, new Mat());
		histR = fixOverflow(histR);
		norm_hist.add(histR);

        return norm_hist;
    }

    
    public  double calcDiff(List<Mat> target,List<Mat> src)
    {
    	double b = Imgproc.compareHist( target.get(0), src.get(0), Imgproc.CV_COMP_BHATTACHARYYA );
    	double g = Imgproc.compareHist( target.get(1), src.get(1), Imgproc.CV_COMP_BHATTACHARYYA );
    	double r = Imgproc.compareHist( target.get(2), src.get(2), Imgproc.CV_COMP_BHATTACHARYYA );
    	
    	return (r+g+b)/3;
    }
    
    public Mat fixOverflow(Mat target)
    {	
    	double data [] = {0.0f};

    	for(int i = 0; i < target.height(); i++)
    	{
    		if(target.get(i, 0)[0] < 0 || target.get(i, 0)[0] > 1)
    			target.put(i,0,data);

    	}

    	return target;
    }
    
    public float calcEntropy(Mat hist)
    {	
    	int imageSize = width * height;
    	float entr = 0.0f;
    	
    	for(int i = 0; i < hist.height(); i++)
    	{
    		float frequency = (float) (hist.get(i, 0)[0] / imageSize + 0.000001);
    		entr -= frequency * (Math.log(frequency) / log2);
    		
    	}

    	return entr;
    }

    public float getEntropy(File file)
    {
    	return entropy_table.get(file)[0] + entropy_table.get(file)[1] + entropy_table.get(file)[2];
    }
}
