import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;


public class ImageFile {
	public File file;
	public List<Mat> norm_hist = new ArrayList<Mat>();
	public Mat imageMat = new Mat();
	
	public ImageFile(File file) 
	{
        this.file = file;
    }
	
	public void calcMat()
	{
		FeatureMatcher fm = FeatureMatcher();
		imageMat = fm.calcMat(this.file);
	}
	
    public void calcHists() 
    {
    	HistogramUtils hu = new HistogramUtils();
        norm_hist = hu.calcHists(file);
    }
    
    public double calcHistsDiff(ImageFile ifile) 
    {
    	List<Mat> target = ifile.norm_hist;
    	HistogramUtils hu = new HistogramUtils();
        double diff = hu.calcDiff(target, norm_hist);
        
        return diff;
    }
}
