import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector {

	public String xmlPath = "haarcascade_frontalface_alt.xml";
	
    public FaceDetector()
    {
    	
    }
    
    public int FaceDetective(Mat _image)
    {
//    	CascadeClassifier faceDetector = new CascadeClassifier(
//    			FaceDetector.class.getResource("haarcascade_frontalface_alt.xml").getPath());
    	
    	CascadeClassifier faceDetector = new CascadeClassifier(
    			FaceDetector.class.getResource(this.xmlPath).getPath());
    	MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(_image, faceDetections);

        return faceDetections.toArray().length;
        //System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));  	
    }
    
//    public static void main(String[] args) {
//
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.out.println("\nRunning FaceDetector");
//
//        CascadeClassifier faceDetector = new CascadeClassifier(FaceDetector.class.getResource("haarcascade_frontalface_alt.xml").getPath());
//        //Mat image = Highgui
//        //        .imread(FaceDetector.class.getResource("shekhar.JPG").getPath());
//        
//        FaceDetector fd = new FaceDetector("haarcascade_frontalface_alt.xml");
//        //traverse 
//        File _file = new File("Resources/CS576_Project_Dataset_2/image081.rgb");
//        //ImageFile im = new ImageFile(_file, 352, 288);
//        
//        //Mat image = im.getFile(_file);
//        
//        System.out.println(fd.FaceDetective(image));
//
//        MatOfRect faceDetections = new MatOfRect();
//        faceDetector.detectMultiScale(image, faceDetections);
//
//        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
//
//        for (Rect rect : faceDetections.toArray()) {
//            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//                    new Scalar(0, 255, 0));
//        }
//
//        String filename = "ouput.png";
//        System.out.println(String.format("Writing %s", filename));
//        Highgui.imwrite(filename, image);
//    }
//    
}
