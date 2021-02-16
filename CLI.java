package test;

import java.util.ArrayList;
import test.Commands.Command;
import test.Commands.DefaultIO;


public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		commands.add(c.new mainComment());
		commands.add(c.new command1());
		commands.add(c.new command2());
		commands.add(c.new command3());
		commands.add(c.new command4());
		commands.add(c.new command5());
		commands.add(c.new command6());
	
	}
	
	
	public void start() {
		
			//main 1st
			commands.get(0).execute();
			String choice= this.dio.readText();
			int client_Choice= Integer.parseInt(choice);
			while(client_Choice!=6)
			{
				commands.get(client_Choice).execute();
				choice= this.dio.readText();
				client_Choice= Integer.parseInt(choice);
				commands.get(0).execute();
			}
	
		}
	
	}

