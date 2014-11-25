import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;


public class ImageFile {
	private File file;
	List<Mat> norm_hist = new ArrayList<Mat>();
	Mat imageMat = new Mat();
	
	public ImageFile(File file) {
        this.file = file;
    }
	
	public void calcMat(){
		imageMat = FeatureMatcher.calcMat(this.file);
	}
	
    public void calcHists() {
        norm_hist = HistogramUtils.calcHists(file);
    }
    
    public double calcHistsDiff(ImageFile ifile) {
    	List<Mat> target = ifile.norm_hist;
        double diff = HistogramUtils.calcDiff(target, norm_hist);
        
        return diff;
    }
}
