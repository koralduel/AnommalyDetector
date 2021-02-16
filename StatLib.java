package test;

public class StatLib {

	

	// simple average
	public static float avg(float[] x){
		int size=x.length;
		float sum=0;
		for(int i=0;i<size;i++)
			sum=sum+x[i];
		return (sum/size);
	}

	// returns the variance of X and Y
	public static float var(float[] x){
		int size=x.length;
		float u=(float)Math.pow(avg(x), 2);
		float sum=0;
		for(int i=0;i<size;i++)
			sum=sum+(float)Math.pow(x[i],2);
		sum=sum/size;
		return (sum-u);
		
	}
	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		int size=x.length;
		float Ex=avg(x);
		float Ey=avg(y);
		float sum=0;
		for(int i=0;i<size;i++)
			sum=sum+(x[i]-Ex)*(y[i]-Ey);
		return sum/size;
	}

	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		return (float) (cov(x,y)/(Math.sqrt(var(x)*var(y))));
	}
	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		int size=points.length;
		float[]x=new float[size];
		float[]y=new float[size];
		for(int i=0;i<size;i++)
		{
			x[i]=points[i].x;
			y[i]=points[i].y;
		}
		float a=cov(x,y)/var(x);
		float b=avg(y)-a*avg(x);
		Line Y=new Line(a,b);
		return Y;
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		Line Y=linear_reg(points);
		float y=Y.f(p.x);
		return Math.abs(y-p.y);
	}
	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		float y=l.f(p.x);
		return Math.abs(y-p.y);
	}
	
}
