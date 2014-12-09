import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;


public class DBSCAN {
	public static float threshold= 0.2f;
	public static float Bigthreshold= 0.33f;
	public static int minNumber = 4;
	public static Vector<List> resultList = new Vector<List>();
	public static Vector<File> FileList = new Vector<File>();
    public static Vector<File> Neighbours ;
    public static Vector<File> humanPic = new Vector<File>();
    public static Vector<File> VisitList = new Vector<File>();
    public static String path ="Dataset";
    public static HashMap<String, Double> dist_table = new HashMap<String, Double>();
    HashMap<ImageFile, List<File> > table = new HashMap<ImageFile, List<File>>();
	
    public DBSCAN(){
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
	public  Vector<List> applyDbscan()
	{
		resultList.clear();
		FileList.clear();
		VisitList.clear();
		FileList=getList();
		
		int index =0;
		Vector<File> spam = new Vector<File>();
					
		while (FileList.size()>index){
			File p =FileList.get(index);
			 if(!isVisited(p)){
			
				Visited(p);
				
				Neighbours = getNeighbours(p);
				
				if (Neighbours.size()>=minNumber){
	
					int ind=0;
					while(Neighbours.size()>ind){
						
						File r = Neighbours.get(ind);
						if(!isVisited(r)){
							Visited(r);
						Vector<File> Neighbours2 = getNeighbours(r);
						if (Neighbours2.size() >= minNumber){

							Neighbours=Merge(Neighbours, Neighbours2);
							
							}
						} 
						ind++;
					}

					System.out.println("N"+Neighbours.size());
					resultList.add(Neighbours);
					}
				else
				{
					spam.add(p);
				}

			 }
			 index++;
		}
		
		//Get unvisited image
		
		System.out.println(spam.size());
//		List<File> files = resultList.get(0);
//		List<File> oldFiles = new Vector<File>();
//		for (int i = 0; i < files.size();){ 
//			Mat targetMat = FeatureMatcher.calcMat(files.get(i));
//			for(int j = 1; j < resultList.size(); j++){
//				File keyFile = (File)resultList.get(j).get(0);
//				
//				Mat keyImage = FeatureMatcher.calcMat(keyFile);
//				double dist= FeatureMatcher.calcFeatureDiff(targetMat,keyImage);
//				if(dist > 0.97){
//					resultList.get(j).add(files.get(i));
////					files.remove(i);
//					break;
//				}
//				else
//				{
//					oldFiles.add(files.get(i));
//				}
//			}
//			i++;
//		}
////		files = oldFiles;
//		resultList.set(0, oldFiles);
		spam.addAll(resultList.get(0));
		resultList.remove(0);
		
		//use 
		
		for(int j=0;j<spam.size();j++)
		{
			File file = spam.get(j);
			ImageFile target = new ImageFile(file);
            target.calcHists();
            
			if(table.isEmpty()){
            	List<File> filelist = new ArrayList<File>();
            	filelist.add(file);
            	//target.imageMat = targetMat;  //only store the Mat for the first image of Genre for future calculation
            	table.put(target, filelist);
			}
			else
			{
				Iterator iter = table.entrySet().iterator();
            	boolean foundGenre = false;
            	
            	while(iter.hasNext()){
            		Map.Entry<ImageFile, List<File>> entry = (Map.Entry<ImageFile, List<File>>) iter.next();
            		
            		System.out.println();
            		ImageFile src= entry.getKey();
            		double diff = target.calcHistsDiff(src);
            		
            		
            		if(diff < Bigthreshold){ // append to this entry

            				entry.getValue().add(file);
	            			foundGenre = true;
	            			break;

            		}
            	}
            	if(!foundGenre){ //add one more entry;
            		List<File> filenames = new ArrayList<File>();
            		filenames.add(file);
            		//target.imageMat = targetMat;
        			table.put(target,filenames);
            	}
			}
		}
		
		//Merge HashMap to ArrayList

		HashMap<Integer, List<File>> returnVal = new HashMap<Integer, List<File>>();
        int ite = 0;
        Iterator iter = table.entrySet().iterator(); 
        while (iter.hasNext()) { 
        	Map.Entry<ImageFile, List<File>> entry = (Map.Entry<ImageFile, List<File>>) iter.next();
        	List<File> val = entry.getValue();
        	resultList.add(val);
        }
		
	    Vector<File> junkList =new Vector<File>();
	    
	    for(int j=0;j<resultList.size();)
	    {
	    	if(resultList.get(j).size() < 2)
	    	{
	    		junkList.addAll(resultList.get(j));
	    		resultList.remove(j);
	    	}else j++;
	    }
	    
	    for(int i =13;i<humanPic.size();)
	    {
	    	junkList.add(humanPic.get(i));
	    	humanPic.remove(i);
	    }
	    
        resultList.add(humanPic);
        resultList.add(junkList);
		
		return resultList;	
	}
	
	public double getDistance (File a, File b){
		String pairAB = a.getName()+b.getName();
		String pairBA = b.getName()+a.getName();
		
		if(dist_table.containsKey(pairAB)){
			return dist_table.get(pairAB);
		}else if(dist_table.containsKey(pairBA)){
			return dist_table.get(pairBA);
		}else {

			ImageFile if1 = new ImageFile(a);
	        ImageFile if2 = new ImageFile(b);
	        
	        if1.calcHists();
	        if2.calcHists();
	        double diff = if1.calcHistsDiff(if2);
	        Double dist = new Double(diff);
	        dist_table.put(pairAB, dist);
	        
	        return diff;
		}
	}


	public  Vector<File> getNeighbours(File p){
		Vector<File> neigh =new Vector<File>();
		Iterator<File> Files = FileList.iterator();
		while(Files.hasNext()){
				File q = Files.next();
				if(getDistance(p,q)<= threshold){
				neigh.add(q);
				}
		}
		return neigh;
	}

	public  void Visited(File d){
		VisitList.add(d);
	}

	public  boolean isVisited(File c){
		if (VisitList.contains(c))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public  Vector<File> Merge(Vector<File> a,Vector<File> b){
	
		Iterator<File> it5 = b.iterator();
		while(it5.hasNext()){
			File t = it5.next();
			if (!a.contains(t) ){
				a.add(t);
			}
		}
		return a;
	}



	//  Returns FilesList 
	public  Vector<File> getList() {
	
		Vector<File> newList =new Vector<File>();
		newList.clear();
		
		File filePath = new File(path);
	    File[] files =  filePath.listFiles();
	    
	    for (File file : files){ 
	    	if (file.getName().equals(".DS_Store")) {
                continue;
            } 
	    	//detective human face
	    	FaceDetector fd = new FaceDetector();
	    	if (fd.FaceDetective(FeatureMatcher.calcMat(file)) > 0)
	    	{
	    		this.humanPic.add(file);
	    		System.out.println(fd.FaceDetective(FeatureMatcher.calcMat(file)));
	    	}
	    	else
	    	{
		    	newList.add(file);
	    	}	    	
	    }
	    System.out.println(this.humanPic.size());	    	
	    
	    	    
		return newList;
	}		

}
