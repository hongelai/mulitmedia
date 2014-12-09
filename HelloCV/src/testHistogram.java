import java.io.File;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;


public class testHistogram {
	public static int width = 352;
	public static int height = 288;
	public static final int BINS = 32;
	public static final float MIN_VALUE = 0.0f;
	public static final float MAX_VALUE = 255.0f;
	public static final float threshold = 0.5f;
	
    public static void main(String[] args) {

        String path1 ="/Volumes/WORK/csci576/CS576_Project_Fall_2014/Dataset";
        String path2 ="/Volumes/WORK/csci576/CS576_Project_Fall_2014/Dataset/image039.rgb";
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        File file1 = new File(path1);
        File file2 = new File(path2);
//        ImageFile if1 = new ImageFile(file1);
//        ImageFile if2 = new ImageFile(file2);
//        if1.calcHists();
//        if2.calcHists();
//        float diff = Math.abs(HistogramUtils.getEntropy(file1)-HistogramUtils.getEntropy(file2));
//        double diff = if1.calcHistsDiff(if2);
//        double diff = DBSCAN.getDistance(file1, file2);
//        System.out.println("distance: "+ diff);
//        double diffs = DBSCAN.getDistance(file2, file1);
//        DBSCAN scanner = new DBSCAN();
//        Vector<List> cluster = scanner.applyDbscan();
//        float en = EntropyUtils.getEntropy(file1);
//        System.out.println("distance: "+ en);
        File filePath = new File(path1);
        File[] files =  filePath.listFiles();
        System.out.println(files.length);

    }

}
