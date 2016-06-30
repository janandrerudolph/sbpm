package enumeration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;



public class Log
{
//static attributes
	private static String currentLine = "";
	private static File logfile;
	private static ArrayList<LogType> filter = new ArrayList<LogType>();
	
	
//static functions	
	@SuppressWarnings("resource")
	public static void createLog()
	{
		logfile = new File("logfile");
		try 
		{
			FileWriter fileWriter = new FileWriter(logfile, true);
			new PrintWriter(fileWriter, true);
		} 
		catch (IOException e1) {
			System.err.println("Could not touch log file >>" + e1.getMessage() + "<<");
		}
	}
	
	public static File finishLog(File logDirectory)
	{
		filter = new ArrayList<LogType>();
		String logPath = "";
		try
		{
			logPath = logDirectory.getCanonicalPath();
		}
		catch (Exception e)
		{
			logfile.delete();
			System.err.println("Could not save the log");
		}
		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy_HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String name = logPath + "/" + dateFormat.format(cal.getTime()) + ".veryficationLog";
		System.out.println(name);
		File newFile = new File(logfile.getParent(), name);
		try 
		{
			Files.move(logfile.toPath(), newFile.toPath());
		} 
		catch (IOException e) {
			System.err.println("Could not rename the logfile >>" + e.getMessage() + "<<");
		}
		
		return newFile;
	}
	
	public static void print(String log, LogType type)
	{
		if (filter.contains(type))
		{
			currentLine += log;
		}
	}
	
	public static void println(String log, LogType type)
	{
		if (filter.contains(type))
		{
			log += currentLine;
			try 
			{
				@SuppressWarnings("resource")
				PrintWriter pWriter = new PrintWriter(new FileWriter("logfile", true), true);
				pWriter.println(log);
			} 
			catch (IOException e1) 
			{
				System.err.println("Could not write into logfile >>" + e1.getMessage() + "<<");
			}
			currentLine = "";
		}
	}

	public static void removeLogType(LogType logType) 
	{
		filter.remove(logType);
	}

	public static void addLogType(LogType logType) 
	{
		if (!filter.contains(logType))
		{
			System.out.println("added");
			Log.filter.add(logType);
		}
	}
}