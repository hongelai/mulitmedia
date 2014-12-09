
import java.io.File;

public class myThread extends Thread{
	private int index;
	public myThread(int index)
	{
		this.index = index;
	}
	
	public void run(){
		MediaEntry.PlayVideo(index);
		//imageReader.videoRead(this.file);
		//.VideoPlay(file);
    }
}
