package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Server {

	public interface ClientHandler{
		 void communication(InputStream in,OutputStream out);
	}

	volatile boolean stop;
	public Server() {
		stop=false;
	}
	
	
	private void startServer(int port, ClientHandler ch) {
		try {
			ServerSocket server=new ServerSocket(port);
			server.setSoTimeout(1000);
			while(!stop)
			{
				try {
					Socket current_cli=server.accept();
					ch.communication(current_cli.getInputStream(),current_cli.getOutputStream());
					current_cli.close();
				}
				catch(SocketTimeoutException STE){}
			}
			server.close();
		}
		catch(IOException IOE){
			IOE.printStackTrace();
		}
	}
	
	// runs the server in its own thread
	public void start(int port, ClientHandler ch) {
		new Thread(()->startServer(port,ch)).start();
	}
	
	public void stop() {
		stop=true;
	}
}
