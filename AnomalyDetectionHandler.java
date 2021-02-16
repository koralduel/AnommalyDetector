package test;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import test.Commands.DefaultIO;
import test.Server.ClientHandler;

public class AnomalyDetectionHandler implements ClientHandler{
	Scanner in;
	PrintWriter out;
	
	public void communication(InputStream input,OutputStream output) {
		this.in=new Scanner(input);
		this.out=new PrintWriter(output);
		SocketIO SIO=new SocketIO();
		CLI cli=new CLI(SIO);
		cli.start();
		out.println("bye");
		out.close();
		in.close();
	}

	public class SocketIO implements DefaultIO{
		public String readText() {
			return in.nextLine();
		}

		@Override
		public void write(String text) {
			out.print(text);
		}

		@Override
		public float readVal() {
			return in.nextFloat();
		}

		@Override
		public void write(float val) {
			out.print(val);
		}

	}


}
