
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class VideoData {

	public ArrayList<ExtractVideo> videos;
	public ArrayList<BufferedImage> images;
	public ArrayList<File> allFiles;


	public VideoData()
	{
		videos = new ArrayList<ExtractVideo>(10);
		images = new ArrayList<BufferedImage>(10);
		allFiles = new ArrayList<File>(10);
	}
	
	public void LoadAllFile(String path)
	{
		File filePath = new File(path);
        File[] files =  filePath.listFiles();
                       
        for (File file : files)
        {
            if (file.getName().startsWith("video")) {
            	ExtractVideo videoTemp = new ExtractVideo();
            	videoTemp.LoadFiles(file);
            	videos.add(videoTemp);
            	allFiles.add(file);
            	images.add(videoTemp.ExtractImage());
            }
        }
	}
	
	
	public void LoadOnlyFile(String path)
	{
		File filePath = new File(path);
        File[] files =  filePath.listFiles();
                       
        for (File file : files)
        {
            if (file.getName().startsWith("video")) {
            	ExtractVideo videoTemp = new ExtractVideo();
            	videoTemp.LoadFiles(file);
//            	videos.add(videoTemp);
            	allFiles.add(file);
//            	images.add(videoTemp.ExtractImage());
            }
        }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		VideoData te = new VideoData();
		te.LoadAllFile("Dataset");
		System.out.println("run over");
	}

}
