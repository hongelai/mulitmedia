package imageCompression;

import java.util.Arrays;


public class DWT {
    public int  level, nrows, ncols;  
    public double  DWTresult[][];  
    int  i, j, i1, j1, i2, j2, k;  
    int  nr, nc, nr2, nc2;
    public static int zigzag[][];

	public static void makeDWTmatrix(int width,int height){
		int row, col;
		row =  width;
		col = height;
		int m[][] = new int[col][row];
		int boxSize = 1;
		int index = 1;
		m[0][0] = 0;
		int maxBox;
		if(row>col) maxBox = (int)row/2;
		else maxBox = (int)col/2;
		
		while(boxSize <= maxBox){
			int v = boxSize;
			for(int i = 0; i<boxSize;i++)
				for(int j = v; j< boxSize+v;j++){
					if(i>=col || j >=row) continue;
					m[i][j] = index++;
				}
			for(int i = v; i<boxSize+v;i++)
				for(int j = 0; j< boxSize;j++){
					if(i>=col || j >=row) continue;
					m[i][j] = index++;
				}
			for(int i = v; i<boxSize+v;i++)
				for(int j = v; j< boxSize+v;j++){
					if(i>=col || j >=row) continue;
					m[i][j] = index++;
				}
			boxSize *=2;
		}
		zigzag = m;	
	}
	
    public double[] zigZag(double[][] m) {
    	double[] zz = new double[m.length*m[0].length];
    	for (int i=0;i<m.length;i++) {
    		for (int j=0;j<m[0].length;j++) zz[zigzag[i][j]]=m[i][j];
    	}
    	return zz;
    }

    /* write zig zag ordered coefficients into matrix */
    public double[][] unZigZag(double[] zz,int row,int col) {
      double[][] m = new double[col][row];
      for (int i=0;i<col;i++) {
        for (int j=0;j<row;j++) {
          m[i][j]=zz[zigzag[i][j]];
        }
      }
      return m;
    }
	
    public double[] forwardDWT(int level,int width,int height, double inputdata[][]){
    	nrows = nr = width;
    	ncols = nc = height;
	    double source[][] = new double[nrows][ncols];  
	    DWTresult = new double[nrows][ncols];  
	  
	    for (i=0; i<nrows; i++)  
	    	for (j=0; j<ncols; j++)  
	    		source[i][j] = inputdata[i][j];  
	  
	    for (k=1; k<=level; k++, nr/=2, nc/=2) {
	    	// Horizontal processing:  
	    	nc2 = nc/2;  
	    	for (i=0; i<nr; i++)
	    		for (j=0; j<nc; j+=2) {  
	    			j1 = j+1;  
	    			j2 = j/2;  
	    			DWTresult[i][j2] = (source[i][j] + source[i][j1])/2;  
	    			DWTresult[i][nc2+j2] = (source[i][j] - source[i][j1])/2; 
	    		}  
	    	
	        // Copy to source:  
	        for (i=0; i<nr; i++)  
	    	    for (j=0; j<nc; j++)  
	    		    source[i][j] = DWTresult[i][j];  
	  
	        // Vertical processing:  
	        nr2 = nr/2;  
	        for (i=0; i<nr; i+=2)   
	    	    for (j=0; j<nc; j++) {  
	    		    i1 = i+1;  
	    		    i2 = i/2;  
	    		    DWTresult[i2][j] = (source[i][j] + source[i1][j])/2;  
	    		    DWTresult[nr2+i2][j] = (source[i][j] - source[i1][j])/2;  
	    	    }    
	      
	        // Copy to source:  
	        for (i=0; i<nr; i++)  
	    	    for (j=0; j<nc; j++)  
	    		    source[i][j] = DWTresult[i][j];  
	        
	    }  
	    return zigZag(source);
    }

    public double[][] inverseDWT(int level,int width,int height, double data[],int length){
    	double[] input = Arrays.copyOf(data, data.length);
    	for(int i = length; i< width*height;i++) input[i] = 0;
    	double inputdata[][] =  unZigZag(input,width,height);
    	
    	int tmp = (int)Math.pow(2, level-1);   
    	nrows = width;
    	ncols = height;
        nr = nrows/tmp;   
        nc = ncols/tmp;   
        double source[][] = new double[nrows][ncols];  
        double IDWTresult[][] = new double[nrows][ncols];
        
        for (i=0; i<nrows; i++)  
	    	for (j=0; j<ncols; j++)
	    		source[i][j] = inputdata[i][j]; 
	    	
        for (k=level; k>=1; k--, nr*=2, nc*=2) {   
	          // Vertical processing:   
	          nr2 = nr/2;   
	          for (i=0; i<nr2; i++) {   
		            for (j=0; j<nc; j++) {   
			              i2 = i*2;   
			              IDWTresult[i2][j]   = source[i][j] + source[nr2+i][j];   
			              IDWTresult[i2+1][j] = source[i][j] - source[nr2+i][j];   
		            }   
	          }   
	          // Copy to source:   
	          for (i=0; i<nr; i++)   
	        	  for (j=0; j<nc; j++)   
	        		  source[i][j] = IDWTresult[i][j];   
	          // Horizontal processing:   
	          nc2 = nc/2;   
	          for (i=0; i<nr; i++) {   
	        	  for (j=0; j<nc2; j++) {   
		              j2 = j*2;   
		              IDWTresult[i][j2]   = source[i][j] + source[i][nc2+j];   
		              IDWTresult[i][j2+1] = source[i][j] - source[i][nc2+j];   
	        	  }   
	          }   
	          // Copy to source:   
	          for (i=0; i<nr; i++)   
	        	  for (j=0; j<nc; j++)   
	        		  source[i][j] = IDWTresult[i][j];   
        }    
       
        
		  for (i=0; i<nrows; i++)   
			  for (j=0; j<ncols; j++)   
				  if (source[i][j] > 255){
					  source[i][j] = 255;  
				  }else if(source[i][j] < 0){ 
					  source[i][j] = 0;   
				  }

		  return source;
	} 
    
}
