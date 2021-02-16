package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class Commands {
	
	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);

	}
	
	// the default IO to be used in all commands
	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}

	// the shared state of all commands
	private class SharedState{
		// implement here whatever you need
		TimeSeries test;
		TimeSeries train;
		List<AnomalyReport> listAR;
		List<Point> ranges;
 		
		public List<AnomalyReport> getListAR() {
			return listAR;
		}
		public List<Point> getRanges() {
			return ranges;
		}
		public void setRanges(List<Point> ranges) {
			this.ranges = ranges;
		}
		public void setListAR(List<AnomalyReport> listAR) {
			this.listAR=listAR;
		}
		public TimeSeries getTest() {
			return test;
		}
		public void setTest(TimeSeries test) {
			this.test = test;
		}
		public TimeSeries getTrain() {
			return train;
		}
		public void setTrain(TimeSeries train) {
			this.train = train;
		}
		
		
		
	}
	
	private  SharedState sharedState=new SharedState();
	public  SharedState getSS()
	{
		return this.sharedState;
	}
	
	// Command abstract class
	public abstract class Command{
		protected String description;
		
		public Command(String description) {
			this.description=description;
		}
		
		public abstract void execute();
	}
//-----------------------------------------------------------------------------
	public class ExampleCommand extends Command{

		public ExampleCommand() {
			super("this is an example of command");
		}

		@Override
		public void execute() {
			dio.write(description);
		}		
	}
//------------------------------------------------------------------------------
	public class mainComment extends Command{

		public mainComment() {
			super("Welcome to the Anomaly Detection Server.\n"+
					"Please choose an option:\n"+
					"1. upload a time series csv file\n"+
					"2. algorithm settings\n"+
					"3. detect anomalies\n"+
					"4. display results\n"+
					"5. upload anomalies and analyze results\n"+
					"6. exit\n");
			
		}

		@Override
		public void execute() {
			dio.write(description);
		}		
	}
//------------------------------------------------------------------------------	
	public class command1 extends Command{

		public command1() {
			super("Please upload your local train CSV file.\n");
		}
		@Override
		public void execute()  {
			dio.write(description);
			try {
				FileWriter writer = new FileWriter("anomalyTrain.csv");
				ArrayList<String> text=new ArrayList<String>();
				String Line=dio.readText();
				while(!(Line.equals("done")))
				{
					text.add(Line);
					text.add("\n");
					Line=dio.readText();
				}
				for(int i=0;i<text.size();i++)
					writer.write(text.get(i));
				writer.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			TimeSeries ts=new TimeSeries("anomalyTrain.csv");
			SharedState ss=getSS();
			ss.setTrain(ts);
			dio.write("Upload complete.\n");
			
			dio.write("Please upload your local test CSV file.\n");
			try {
				FileWriter writer2 = new FileWriter("anomalyTest.csv");
				ArrayList<String> text2=new ArrayList<String>();
				String Line2=dio.readText();
				while(!(Line2.equals("done")))
				{
					text2.add(Line2);
					text2.add("\n");
					Line2=dio.readText();
				}
				for(int i=0;i<text2.size();i++)
					writer2.write(text2.get(i));
				writer2.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			TimeSeries ts2=new TimeSeries("anomalyTest.csv");
			SharedState ss2=getSS();
			ss2.setTest(ts2);
			dio.write("Upload complete.\n");
			
		}
	}

			
	
//------------------------------------------------------------------------------
	public class command2 extends Command{

		public command2() {
			super("The current correlation threshold is ");
				
		}

		@Override
		public void execute() {
			dio.write(description);
			SharedState ss=getSS();
			String currentCorelation=String.valueOf(ss.getTest().corelation);
			dio.write(currentCorelation);
			dio.write("\n");
			dio.write("Type a new threshold\n ");
			String clientValue=dio.readText();
			double ClientVal=Double.valueOf(clientValue);
			while(ClientVal>1 ||ClientVal<0)
			{
				dio.write("please choose a value between 0 and 1\n");
				clientValue=dio.readText();
				 ClientVal=Double.valueOf(clientValue);
			}
			ss.getTest().setCorelation(ClientVal);	
		}
	}
//------------------------------------------------------------------------------
		public class command3 extends Command{

			public command3() {
				super("anomaly detection complete.\n");
				
			}
			@Override
			public void execute() {
				SharedState ss1=getSS();
				SimpleAnomalyDetector SAD=new SimpleAnomalyDetector();
				SAD.learnNormal(ss1.getTrain());
				List<AnomalyReport> l=new ArrayList<AnomalyReport>();
				l.addAll(SAD.detect(ss1.getTest()));
				ss1.setListAR(l);
				dio.write(description);
				
			}
		
	}
//--------------------------------------------------------------------------------
		public class command4 extends Command{

			public command4() {
				super("done\n");
				
			}
			@Override
			public void execute() {
				SharedState ss=getSS();
				for(int i=0;i<ss.listAR.size();i++)
				{
					String ts=String.valueOf(ss.listAR.get(i).timeStep);
					dio.write(ts);
					dio.write("	");
					dio.write(ss.listAR.get(i).description);
					dio.write("\n");
					
				}
				dio.write(description);
				
			}
		
	}
//---------------------------------------------------------------------------------
		public class command5 extends Command{

			public command5() {
				super("Please upload your local anomalies file.\n"+"Upload complete\n");
				
			}
			@Override
			public void execute() {
				dio.write(description);
				String line=null;
				String[] temp;
				SharedState ss=getSS();
				line=dio.readText();
				ss.ranges=new ArrayList<Point>();
				while(!line.equals("done"))
				{
					temp=line.split(",");
					float x=Float.valueOf(temp[0]);
					float y=Float.valueOf(temp[1]);
					ss.ranges.add(new Point(x,y));
					line=dio.readText();
				}
				float negative=0;
				int countN=0;
				int sum=0;
				for(Point p: ss.getRanges())
					sum+=p.y-p.x+1;
				negative=ss.getTest().table.get(0).dataOfProperty.size()-sum;
				List<String> description=new ArrayList<String>();
				List<Point> newRanges=new ArrayList<Point>();
				int k=0;
				for(int i=0;i<ss.getListAR().size();i++)
				{
					AnomalyReport start=ss.getListAR().get(i);
					AnomalyReport end=null;
					int j=i+1;
					while(j<ss.getListAR().size() && start.timeStep+j-k==ss.getListAR().get(j).timeStep &&
							start.description.equals(ss.getListAR().get(j).description))
						{
							end=ss.getListAR().get(j);
							j++;
						}	
					if(end!=null)
					{
						description.add(end.description);
						newRanges.add(new Point(start.timeStep,end.timeStep));
						k=j;
					}
					else 
					{
						description.add(start.description);
						newRanges.add(new Point(start.timeStep,start.timeStep));
						k=j;
					}
					i=j-1;	
				}
			float fn=0,tn=0,fp=0,tp=0;
			int P=ss.ranges.size();
			boolean flag=false;
			List<Point> connectedNewR=new ArrayList<Point>();
			for(int i=0;i<P;i++)
			{
				for(int j=0;j<newRanges.size();j++)
				{
					float xOfR=ss.ranges.get(i).x;
					float yOfR=ss.ranges.get(i).y;
					float yOfNr=newRanges.get(j).y;
					float XOfNr=newRanges.get(j).x;
					if(xOfR<=yOfNr && yOfR>=yOfNr || xOfR>=XOfNr && yOfR>=yOfNr &&yOfNr>=xOfR ||
							XOfNr<=xOfR && yOfNr>=yOfR || XOfNr>=xOfR && yOfNr>=yOfR &&yOfR>=XOfNr)
					{
						if(!flag)
							tp+=1;
						flag=true;
						if(!connectedNewR.contains(newRanges.get(j)))
								connectedNewR.add(newRanges.get(j));
					}
					
				}
				if(!flag)//no connection with ant New Ranges
					fn+=1;
				flag=false;	
			}
			fp=newRanges.size()-connectedNewR.size();
			tn=ss.test.NumOfLines-1-tp-fn-fp;
			tp=tp/P;
			fp=fp/negative;
			DecimalFormat df=new DecimalFormat("0.0000");
			String tp_final=df.format(tp);
			String fp_final=df.format(fp);
			tp_final=tp_final.substring(0, tp_final.length()-1);
			fp_final=fp_final.substring(0, fp_final.length()-1);
			while(tp_final.charAt(tp_final.length()-1)=='0'&&tp_final.length()!=3)
				tp_final=tp_final.substring(0, tp_final.length()-1);
			while(fp_final.charAt(fp_final.length()-1)=='0'&&fp_final.length()!=3)
				fp_final=fp_final.substring(0, fp_final.length()-1);
			dio.write("True Positive Rate: "+tp_final+"\n");
			dio.write("False Positive Rate: "+fp_final+"\n");
	
			}
}
		
	
//-----------------------------------------------------------------------------------
		public class command6 extends Command{

			public command6() {
				super("");
				
			}
			@Override
			public void execute() {
				dio.write(description);
				
			}
		
	}
//------------------------------------------------------------------------------------
}
