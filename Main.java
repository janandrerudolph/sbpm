import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;

import enumeration.Log;
import enumeration.LogType;
import gui.Frame;
import init.Interpreter;
import very.Veryfier;


/**
 * @author jan/fabian
 * 
 * Main-Class, 
 * Prototype, to "visualize" the interpreted Data
 * 
 */
@SuppressWarnings("unused")
public class Main 
{
	
	/**
	 * @param args not used
	 */
	public static void main(String[] args) 
	{
		Log.createLog();
//		Frame frame = new Frame();
//		
//		frame.setTitle("Verifikation von S-BPM Prozessspezifikation mit abstrakten Schichten");
//		frame.setSize(500,500);
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		//copies some jena-data for eclipse..
		
		try 
		{
			copyPropertyFileForJena();
		}
		catch (Exception e)
		{
			System.err.println("Could not copy log4j.properties into bin/ >>" + e.getMessage() + "<<");
		}
		
		//TODO fabian GUI the following "addLogTypes" shall be choosed by the user via the GUI:
		Log.addLogType(LogType.ALGO_DEEP_EDGE);
		Log.addLogType(LogType.ALGO_DEEP_STATE);
		Log.addLogType(LogType.ALGO_PROBLEM);
		Log.addLogType(LogType.ALGO_RECURSION);
		Log.addLogType(LogType.DATA_TRIPLE);
		Log.addLogType(LogType.DATA_SUBJECT);
		Log.addLogType(LogType.DATA_CREATION);
		Log.addLogType(LogType.DATA_CONNECTION);
		Log.addLogType(LogType.DATA_DEEP_CREATION);
		Log.addLogType(LogType.DATA_DEEP_CONNECTION);
		
		//TODO fabian GUI the user should be able to choose veryfication-types by "addVeryType" (nothing useful available yet)
		
		//TODO fabian GUI i think it would be nice, if the gui could print the "problems" if falsified (be aware of a nullpointer if verified)
		
		
		//interpretation and analyzation of the data
		String link1 = "data/BasicPASSOntology.owl";
		String link2 = "data/abstract-layered-pass-ont.owl";
		String link3 = "data/AL-Customer-only.owl";
		String link4 = "data/AL-CustomerKingCombined.owl";
		
		Interpreter interpreter = new Interpreter(link4);
		Veryfier veryfier = new Veryfier();
		
		try 
		{
			interpreter.calculate();
		} 
		catch (Exception e) 
		{
			System.err.println("Could not calculate >>" + e.getMessage() + "<<");
			return;
		}
		if (veryfier.verifyImplementation(interpreter.getActors()))
		{
			System.out.println("Implementation veryfied.");
		}
		else
		{
			System.out.println("Implementation falsified.");
		}
		
		Log.finishLog();
	}
	
	/**This function copies the jena-file from the workspace into the bin/ folder (, where eclipse always deletes it...)
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private static void copyPropertyFileForJena() throws IOException 
	{
		//assume that eclipse is used if it is inside a bin-folder
		Boolean eclipse = false;
		try 
		{
			eclipse = new File(".").getCanonicalPath().endsWith("bin/.");
		}
		catch (IOException e) 
		{
			throw e;
		}
		
		if (eclipse)
		{
			File in = new File("log4j.properties");
	        File out = new File("bin/log4j.properties");
	        FileChannel inChannel = null;
	        FileChannel outChannel = null;
	        try 
	        {
	            inChannel = new FileInputStream(in).getChannel();
	            outChannel = new FileOutputStream(out).getChannel();
	            inChannel.transferTo(0, inChannel.size(), outChannel);
	        } 
	        catch (IOException e) 
	        {
	            throw e;
	        } 
	        finally 
	        {
	            try 
	            {
	                if (inChannel != null)
	                    inChannel.close();
	                if (outChannel != null)
	                    outChannel.close();
	            } 
	            catch (IOException e) 
	            {}
	        }
		}
    }
}
