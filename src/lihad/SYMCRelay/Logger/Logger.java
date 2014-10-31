package lihad.SYMCRelay.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	BufferedWriter writer;
	SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	boolean enabled = true;
	
	public Logger(File file){ file.getParentFile().mkdirs(); loadLog(file);}
	public void loadLog(File file){
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			System.out.println("failed to start logger.  invalid file: "+file.getPath());
			error(e.toString(),e.getStackTrace());
		}
	}
	public void toggle_enabled(boolean t){ warning("[logging has been changed = "+t+"]"); enabled = t; }
	
	public void buff(int x){
		if(enabled) try {for(int i = 0;i<x;i++){writer.newLine();} writer.flush();} catch (IOException e){error(e.toString(),e.getStackTrace());}
	}
	public void noformat(String string){
		if(enabled) try { writer.write(string); writer.newLine(); writer.flush(); System.out.println(string);} catch (IOException e){error(e.toString(),e.getStackTrace());}
	}
	public void info(String string){
		if(enabled) try { String s = (dateformat.format(Calendar.getInstance().getTime())+" [info] "+string); writer.write(s); writer.newLine();  writer.flush();
		System.out.println(s);} catch (IOException e){error(e.toString(),e.getStackTrace());}
	}
	public void warning(String string){
		try { String s = (dateformat.format(Calendar.getInstance().getTime())+" [warning] "+string); writer.write(s); writer.newLine();  writer.flush();
		System.out.println(s);} catch (IOException e){error(e.toString(),e.getStackTrace());}
	}
	public void severe(String string){
		try { String s = (dateformat.format(Calendar.getInstance().getTime())+" [severe] "+string); writer.write(s); writer.newLine();  writer.flush();
		System.out.println(s);} catch (IOException e){error(e.toString(),e.getStackTrace());}
	}
	public void error(String s, StackTraceElement[] t){
		severe(s);
		for(StackTraceElement t_a : t)
			try {writer.write(t_a.toString()); writer.newLine();  writer.flush(); System.out.println(t_a.toString());} catch (IOException e){e.printStackTrace();}
	}
	public void debug(String string){
		try { String s = (dateformat.format(Calendar.getInstance().getTime())+" [debug] "+string); writer.write(s); writer.newLine();  writer.flush();
		System.out.println(s);} catch (IOException e){e.printStackTrace();}
	}
}
