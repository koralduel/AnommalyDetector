package test;

import java.util.ArrayList;
import java.util.List;


public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	ArrayList<CorrelatedFeatures> corelatedArray=new ArrayList<CorrelatedFeatures>(); 
//-----------------------------------------------help-functions------------------------------------------//
//The function gets an arraylist array and copies it into an array of float
	public float[] change(ArrayList<Float> f)
	{
		float[] temp=new float[f.size()];
		for(int i=0;i<f.size();i++)
		{
			temp[i]=f.get(i);
		}
		return temp;
	}
//The function takes 2 arrays and inserts them into an array of points, so that each array represents X values ​​and the other Y values
	public Point[] changeFtoPoint(float[] a,float[] b)
	{
		Point[] help=new Point[a.length];
		for(int i=0;i<a.length;i++)
		{
			help[i]=new Point(a[i],b[i]);
		}
		return help;
	}
//help function for function "learnNormal"- return if the couple a,b already in the array in a different order
	public boolean AlreadyCorelated(String a,String b)
	{
		for(int i=0;i<this.corelatedArray.size();i++)
		{
			if(this.corelatedArray.get(i).feature1.equals(b)&&(this.corelatedArray.get(i).feature2.equals(a)))
					return true;
		}
		return false;
	}
//--------------------------------------------------------------------------------------------------------//
	public void learnNormal(TimeSeries ts) {
		int size=ts.NumOfCol;
		int mikom=0; //save the location of the correlated feature each time
		float max=0;
		for(int i=0;i<size-1;i++)
		{
			float[] property1=change(ts.table.get(i).dataOfProperty);
			ts.table.get(i).setCorelated(ts.table.get(i+1).propertyNames);
			mikom=0;
			for(int j=0;j<size;j++)
			{
				if(i!=j)//In order not to test the correlation of an object with itself
				{
					float[] property2=change(ts.table.get(j).dataOfProperty);
					float temp=Math.abs(StatLib.pearson(change(ts.table.get(i).dataOfProperty),change(ts.table.get(j).dataOfProperty)));
					if(max<=temp&&temp>ts.corelation)
					{
						max=temp;
						ts.table.get(i).setCorelated(ts.table.get(j).propertyNames);//save the name of the correlated object
						mikom=j;
					}
				}
							
			}
			if(max!=0)
			{
			//point-from the 2 correlated objects
				Point[] helpPoint = changeFtoPoint(property1,change(ts.table.get(mikom).dataOfProperty));
				Line helpLine=StatLib.linear_reg(helpPoint);
				float devResult=StatLib.dev(helpPoint[0],helpLine);//save the point with the largest deviation
				for(int k=0;k<helpPoint.length;k++)
				{
					float help=StatLib.dev(helpPoint[k], helpLine);
					if(devResult<help)//if found a largest deviation
						devResult=help;
				}
				if(AlreadyCorelated(ts.table.get(i).propertyNames,ts.table.get(i).corelated)==false)//if the 2 object already in the correlates-dont add them to the array
				{
					devResult=(float)(devResult+0.027);
					CorrelatedFeatures helpCorelated=new CorrelatedFeatures(ts.table.get(i).propertyNames,ts.table.get(i).corelated,max,helpLine,devResult);
					corelatedArray.add(helpCorelated);
				}
			}
			max=0;
		}
			
	}


	public List<AnomalyReport> detect(TimeSeries ts) {
		ArrayList<AnomalyReport> helpList=new ArrayList<AnomalyReport>();
		for(int i=0;i<this.corelatedArray.size();i++)
		{
			ArrayList<Float> Property1=ts.getPropertyByName(this.corelatedArray.get(i).feature1);
			ArrayList<Float> Property2=ts.getPropertyByName(this.corelatedArray.get(i).feature2);
			Point[] helpPoint=changeFtoPoint(change(Property1),change(Property2));
			String helpDescription=this.corelatedArray.get(i).feature1+"-"+this.corelatedArray.get(i).feature2;
			for(int j=0;j<helpPoint.length;j++)
			{
				float resultdev=StatLib.dev(helpPoint[j],this.corelatedArray.get(i).lin_reg);
				if(resultdev>this.corelatedArray.get(i).threshold)
				{
					AnomalyReport report=new AnomalyReport(helpDescription,j+1);
					helpList.add(report);
				}
					
			}
			
		}
		return helpList;
	}
//
	public List<CorrelatedFeatures> getNormalModel(){
		return this.corelatedArray;		
	}
}
