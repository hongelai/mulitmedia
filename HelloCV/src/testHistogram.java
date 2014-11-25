import java.io.File;
import org.opencv.core.Core;


public class testHistogram {
	public static int width = 352;
	public static int height = 288;
	public static final int BINS = 32;
	public static final float MIN_VALUE = 0.0f;
	public static final float MAX_VALUE = 255.0f;
	public static final float threshold = 0.5f;
	
    public static void main(String[] args) {

        String path1 ="/Volumes/WORK/csci576/CS576_Project_Fall_2014/Dataset/image151.rgb";
        String path2 ="/Volumes/WORK/csci576/CS576_Project_Fall_2014/Dataset/image155.rgb";
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        File file1 = new File(path1);
        File file2 = new File(path2);
        ImageFile if1 = new ImageFile(file1);
        ImageFile if2 = new ImageFile(file2);
        if1.calcHists();
        if2.calcHists();
        double diff = if1.calcHistsDiff(if2);
        System.out.println("distance: "+ diff);

    }

}
