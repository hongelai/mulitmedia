package imageCompression;

import java.util.Arrays;


public class DCT {
	public Object quantum[] = new Object[2];
	public int dctBlockSize = 8;
	public int[][] zigzag;
	public double[][] coefficient;
	public double cosXY[][];

	
	public void initMatrix(){
		
		initCoefficients();
		zigzag = initZigZagMatrix();
		
		int quantum_luminance[] = new int[]
			{
			16, 11, 10, 16, 24, 40, 51, 61, 12, 12,
			14, 19, 26, 58, 60, 55, 14, 13, 16, 24,
			40, 57, 69, 56, 14, 17, 22, 29, 51, 87,
			80, 62, 18, 22, 37, 56, 68,109,103, 77,
			24, 35, 55, 64, 81,104,113, 92, 49, 64,
			78, 87,103,121,120,101, 72, 92, 95, 98,
			112,100,103, 99
			};
		int quantum_chrominance[] = new int[]
			{
			17,18,24,47,99,99,99,99,18,21,
			26,66,99,99,99,99,24,26,56,99,
			99,99,99,99,47,66,99,99,99,99,
			99,99,99,99,99,99,99,99,99,99,
			99,99,99,99,99,99,99,99,99,99,
			99,99,99,99,99,99,99,99,99,99,
			99,99,99,99
			};
		quantum[0] = quantum_luminance;
		quantum[1] = quantum_chrominance;
		
		cosXY = new double[8][8];
		
		for (int y=0;y<dctBlockSize;y++)
	          for (int x=0;x<dctBlockSize;x++)
	        	  cosXY[y][x] = Math.cos(((2*x+1)/(2.0*dctBlockSize))*y*Math.PI);
		
	}
	
	public int[][] initZigZagMatrix() {
        int[][] zz = new int[dctBlockSize][dctBlockSize];
        int zval=0;
        int zval2=dctBlockSize*(dctBlockSize-1)/2;
        int i,j;
        for (int k=0;k<dctBlockSize;k++) {
          if (k%2==0) {
            i=0;
            j=k;
            while (j>-1) {
              zz[i][j]=zval;
              zval++;
              i++;
              j--;
            }
            i=dctBlockSize-1;
            j=k;
            while (j<dctBlockSize) {
              zz[i][j]=zval2;
              zval2++;
              i--;
              j++;
            }
          }
          else {
            i=k;
            j=0;
            while (i>-1) {
              zz[i][j]=zval;
              zval++;
              j++;
              i--;
            }
            i=k;
            j=dctBlockSize-1;
            while (i<dctBlockSize) {
              zz[i][j]=zval2;
              zval2++;
              i++;
              j--;
            }
          }
        }
        return zz;
    }
	
    public void initCoefficients() {
        coefficient = new double[dctBlockSize][dctBlockSize];

        for (int i=1;i<dctBlockSize;i++) {
        	for (int j=1;j<dctBlockSize;j++) {
        		coefficient[i][j]=1;
        	}
        }

        for (int i=0;i<dctBlockSize;i++) {
                coefficient[i][0]=1/Math.sqrt(2.0);
        	coefficient[0][i]=1/Math.sqrt(2.0);
        }
        coefficient[0][0]=0.5;
    }

    public double[] forwardDCT(double[][] input) {
        double[][] output = new double[dctBlockSize][dctBlockSize];
        
        for (int u=0;u<dctBlockSize;u++) {
          for (int v=0;v<dctBlockSize;v++) {
            double sum = 0.0;
            for (int x=0;x<dctBlockSize;x++) {
              for (int y=0;y<dctBlockSize;y++) {
                sum+=input[x][y]*cosXY[u][x]*cosXY[v][y];
              }
            }
            sum*=coefficient[u][v]/4.0;
            output[u][v]=sum;
          }
        }
        double result[] = zigZag(output);
        return result;
    }

    public double[][] inverseDCT(double[] data,int length) {
    	
       double[][] output = new double[dctBlockSize][dctBlockSize];
       double[] input = Arrays.copyOf(data, data.length);
       for(int i = length; i< dctBlockSize*dctBlockSize;i++) input[i] = 0;
       double temp[][] =  unZigZag(input);
       
       for (int x=0;x<dctBlockSize;x++) {
        for (int y=0;y<dctBlockSize;y++) {
          double sum = 0.0;
          for (int u=0;u<dctBlockSize;u++) {
        	  for (int v=0;v<dctBlockSize;v++) {
        		  sum+=coefficient[u][v]*temp[u][v]*cosXY[u][x]*cosXY[v][y];
        	  }
          }
          sum/=4.0;
          if(sum > 255)
        	  sum=255;
          else if(sum<0)
        	  sum = 0;
          
          output[x][y]=sum;
        }
       }
       return output;
    }

    /* write dct coefficient matrix into 1D array in zig zag order */
    public double[] zigZag(double[][] m) {
    	double[] zz = new double[dctBlockSize*dctBlockSize];
    	for (int i=0;i<dctBlockSize;i++) {
    		for (int j=0;j<dctBlockSize;j++) zz[zigzag[i][j]]=m[i][j];
    	}
    	return zz;
    }

    /* write zig zag ordered coefficients into matrix */
    public double[][] unZigZag(double[] zz) {
      double[][] m = new double[dctBlockSize][dctBlockSize];
      for (int i=0;i<dctBlockSize;i++) {
        for (int j=0;j<dctBlockSize;j++) {
          m[i][j]=zz[zigzag[i][j]];
        }
      }
      return m;
    }
    
    public double[] quantizeBlock(final double inputData[][], final int code) {
        double outputData[] = new double[this.dctBlockSize * this.dctBlockSize];
        double temp[][] = new double[this.dctBlockSize][this.dctBlockSize];
        int i, j;
        int index;
        index = 0;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                temp[i][j] =  Math.round(inputData[i][j] / ((int[]) this.quantum[code])[index]);
                index++;
            }
        }
        outputData = zigZag(temp);
        return outputData;
    }
    
    public double[][] dequantizeBlock(final double inputData[], final int code, int length) {
        final double outputData[][] = new double[this.dctBlockSize][this.dctBlockSize];
        for(int i = length; i< dctBlockSize*dctBlockSize;i++) inputData[i] = 0;
        double temp[][] = unZigZag(inputData);
        int i, j;
        int index;
        index = 0;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                outputData[i][j] = Math.round(temp[i][j] * ((int[]) this.quantum[code])[index]);
                index++;
            }
        }

        return outputData;
    }
}
