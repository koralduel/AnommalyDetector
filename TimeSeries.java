package test;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.nio.file.Paths;
import java.util.ArrayList; 
import java.util.List;




public class TimeSeries {
//---------------------------help-class----------------------------------//	
	public class PropertyNames{

		String propertyNames;
		ArrayList<Float> dataOfProperty=new ArrayList<>();
		String corelated;
		
		//constructor
		public PropertyNames(String propertyNames) {
			this.propertyNames = propertyNames;
			this.dataOfProperty=new ArrayList<Float>();
			
		}
		//getter&setter
		public String getPropertyNames() {
			return propertyNames;
		}
		public void setPropertyNames(String propertyNames) {
			this.propertyNames = propertyNames;
		}
		public String getCorelated() {
			return corelated;
		}
		public void setCorelated(String corelated) {
			this.corelated = corelated;
		}
	}
//--------------------------------------------------------------------------------------			
	String csvFileName_;
	Path pathOfFile;
	ArrayList<PropertyNames> table=new ArrayList<>();
	int NumOfLines=0;
	int NumOfCol=0;
	double corelation=0.9;
	
public double getCorelation() {
		return corelation;
	}
	public void setCorelation(double corelation) {
		this.corelation = corelation;
	}
	//constructor	
	public TimeSeries(String csvFileName) {
		this.csvFileName_=csvFileName;
		this.pathOfFile=Paths.get(csvFileName);
		try {
			BufferedReader br = Files.newBufferedReader(pathOfFile,
			        StandardCharsets.US_ASCII);
		String help=br.readLine();
		String [] FirstLine=help.split(",");
		
		//Initializes the name of the property for each member in the list
		for(int i=0;i<FirstLine.length;i++)
		{
			PropertyNames help2=new PropertyNames(FirstLine[i]);
			this.table.add(help2);
			this.NumOfCol++;	
		}
		//Initializes for each member of the list its list of numerical data
		help=br.readLine();
		while(help!=null)
		{
			FirstLine=help.split(",");
			for(int j=0;j<this.NumOfCol;j++)
			{
				Float f=Float.valueOf(FirstLine[j]).floatValue();
				this.table.get(j).dataOfProperty.add(f);
			 
			}
			help=br.readLine();
		}
		br.close();
		}
	 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	}
	public ArrayList<Float> getPropertyByName(String name)
	{
		for(int i=0;i<this.NumOfCol;i++)
		{
			if(this.table.get(i).propertyNames.equals(name))
				return this.table.get(i).dataOfProperty;
		}
		return null;
	}


//-----------------------------------------getters&setters-----------------------------------
	public String getCsvFileName_() {
		return csvFileName_;
	}


	public void setCsvFileName_(String csvFileName_) {
		this.csvFileName_ = csvFileName_;
	}


	public Path getPathOfFile() {
		return pathOfFile;
	}


	public void setPathOfFile(Path pathOfFile) {
		this.pathOfFile = pathOfFile;
	}


	public int getNumOfLines() {
		return NumOfLines;
	}


	public void setNumOfLines(int numOfLines) {
		NumOfLines = numOfLines;
	}


	public int getNumOfCol() {
		return NumOfCol;
	}


	public void setNumOfCol(int numOfCol) {
		NumOfCol = numOfCol;
	}
//-------------------------------------------------------------------------------------------	
	
	
}
