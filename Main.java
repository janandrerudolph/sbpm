import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.collections15.Transformer;

import javafx.embed.swing.SwingNode;
import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.samples.SimpleGraphDraw;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import enumeration.Log;
import enumeration.LogType;
import enumeration.StateType;
import enumeration.VeriType;
import init.Interpreter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import structure.Actor;
import structure.Edge;
import structure.State;
import very.Veryfier;





//TODO have a look at jgrapht.org
//TODO maybe one could use jung2 or jgrapht in stead of our structure?





/**
 * @author jan/fabian
 * 
 * Main-Class, 
 * Prototype, to "visualize" the interpreted Data
 * 
 */
@SuppressWarnings("unused")
public class Main  extends Application
{
//constructors
	public Main() {}
	
	
//attributes
	BorderPane root;
	
	Button buttonChooseLog;
	Button buttonChooseRdf;
	Button startVeri;
	
	CheckBox checkRec;
	CheckBox checkImp;
	CheckBox checkCompEdges;
	CheckBox checkCompStates;
	
	CheckBox logAlgoDeepEdge;
	CheckBox logAlgoDeepState;
	CheckBox logAlgoProblem;
	CheckBox logAlgoRecursion;
	CheckBox logDataTriple;
	CheckBox logDataSubject;
	CheckBox logDataCreation;
	CheckBox logDataConnection;
	CheckBox logDataDeepCreation;
	CheckBox logDataDeepConnection;
	
	Label label;
	ArrayList<File> rdfFiles;
	File logDirectory;
	
	
//functions	
	/**
	 * @param args not used
	 */
	public static void main(String[] args)
	{				
		//copies some jena-data for eclipse..
		try 
		{
			copyPropertyFileForJena();
		}
		catch (Exception e)
		{
			System.err.println("Could not copy log4j.properties into bin/ >>" + e.getMessage() + "<<");
		}
		
		//Java FX Application
		launch(args);		
	}
	
	/**This function copies the jena-file from the workspace into the bin/ folder (, where eclipse always deletes it...)
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void copyPropertyFileForJena() throws IOException 
	{
		
		if (new File(".classpath").isFile())
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

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		primaryStage.setTitle("Verification of implementations with abstract layered s-bpm");
		
		root = new BorderPane();
		root.setTop(addUpperHBox());
		root.setLeft(addLeftVBox());
		root.setRight(addRightVBox());
		root.setBottom(addLowerVBox());
		
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
	}

	private Node addLeftVBox() 
	{
		VBox vbox = new VBox();
	    vbox.setPadding(new Insets(10));
	    vbox.setSpacing(8);

	    buttonChooseRdf = new Button("Choose RDF File(s)");
	    buttonChooseRdf.setPrefSize(200, 20);
	    buttonChooseRdf.setOnAction(new EventHandler<ActionEvent>() 
	    {
	        @Override public void handle(ActionEvent e) 
	        {
	            openRdfFileChooser();
	        }
	    });
	    
	    Text description = new Text("Verification algorithms:");
	    description.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	    
	    checkImp = new CheckBox("implementation of actors");
	    checkImp.setSelected(true);
	    
	    checkRec = new CheckBox("traversion");
	    checkRec.setSelected(true);
	    
	    checkCompEdges = new CheckBox(">> edge implementation");
	    checkCompEdges.setSelected(true);

	    checkCompStates = new CheckBox(">> state implementation");
	    checkCompStates.setSelected(true);
		
	    vbox.getChildren().addAll(buttonChooseRdf, description, checkImp, checkRec, checkCompEdges, checkCompStates);
		return vbox;
	}
	
	private void openRdfFileChooser() 
	{
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Please choose your RDF-files (hopefully you stored them in one folder :D)");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("OWL (*.owl)", "*.owl"));
		try
		{
			rdfFiles = new ArrayList<File>(chooser.showOpenMultipleDialog(root.getScene().getWindow()));
		}
		catch (Exception e)
		{
			System.err.println("The Filechooser could not open files.");
		}
		if (!rdfFiles.isEmpty())
		{
			buttonChooseRdf.setText("Change RDF File(s)");
			startVeri.setText("Please start the verification :)");
			label.setText("Please start the verification.");
		}
	}
	
	private Node addRightVBox() 
	{
		VBox vbox = new VBox();
	    vbox.setPadding(new Insets(10));
	    vbox.setSpacing(8);

	    buttonChooseLog = new Button("Choose location for Log");
	    buttonChooseLog.setPrefSize(200, 20);
	    buttonChooseLog.setOnAction(new EventHandler<ActionEvent>() 
	    {
	        @Override public void handle(ActionEvent e) 
	        {
	            openLogFileChooser();
	        }
	    });
	    
	    Text description = new Text("Debugging:");
	    description.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	    
	    logDataTriple = new CheckBox("reading RDF-triples");
	    logDataTriple.setSelected(false);
	    
	    logDataSubject = new CheckBox("sorted by RDF-Subjects");
	    logDataSubject.setSelected(false);
	    
	    logDataCreation = new CheckBox("creating java-objects");
	    logDataCreation.setSelected(true);
	    
	    logDataDeepCreation = new CheckBox(">> deeper information");
	    logDataDeepCreation.setSelected(false);
	    
	    logDataConnection = new CheckBox("connecting java-objects");
	    logDataConnection.setSelected(true);
	    
	    logDataDeepConnection = new CheckBox(">> deeper information");
	    logDataDeepConnection.setSelected(true);
	    
	    logAlgoRecursion = new CheckBox("recursive traversion");
	    logAlgoRecursion.setSelected(true);
	    
	    logAlgoDeepEdge = new CheckBox(">> deeper information regarding Edges");
	    logAlgoDeepEdge.setSelected(false);
	    
	    logAlgoDeepState = new CheckBox(">> deeper information regarding States");
	    logAlgoDeepState.setSelected(false);
	    
	    logAlgoProblem = new CheckBox("general problems");
	    logAlgoProblem.setSelected(true);
		
	    vbox.getChildren().addAll(buttonChooseLog, description, logDataTriple, logDataSubject, logDataCreation, logDataDeepCreation, 
	    		logDataConnection, logDataDeepConnection, logAlgoRecursion, logAlgoDeepEdge, logAlgoDeepState, logAlgoProblem);
		return vbox;
	}
	

	protected void openLogFileChooser() 
	{
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Please choose the location, where we can store the logbook. Ahoi.");
		logDirectory = chooser.showDialog(root.getScene().getWindow());
		if (logDirectory.isDirectory())
		{
			buttonChooseLog.setText("Change location for Log");
		}
	}
	

	private HBox addUpperHBox() 
	{
		HBox hbox = new HBox();
	    hbox.setPadding(new Insets(15, 12, 15, 12));
	    hbox.setSpacing(10);
	    hbox.setAlignment(Pos.BASELINE_CENTER);
	    
	    Text description = new Text("This application verifies an implementation of an abstract subject-oriented business process model. \n"
	    		+ "On th left hand side, please choose your RDF-XML files and select the verification properties. \n"
	    		+ "On the right hand side, please choose the repository to save the logfile and select its properties. \n"
	    		+ "If you do not need the logfile, just don't choose a location ;)");
	    
	    hbox.getChildren().addAll(description);
		return hbox;
	}
	
	
	private VBox addLowerVBox() 
	{
		VBox hbox = new VBox();
	    hbox.setPadding(new Insets(15, 12, 15, 12));
	    hbox.setSpacing(30);
	    hbox.setAlignment(Pos.BASELINE_CENTER);
	    
	    startVeri = new Button("Provide a RDF file, please.");
	    startVeri.setPrefSize(600, 50);
	    startVeri.setFont(Font.font("Arial", FontWeight.BOLD, 32));
	    startVeri.setOnAction(new EventHandler<ActionEvent>() 
	    {
	        @Override public void handle(ActionEvent e) 
	        {
	            startVerification();
	        }
	    });
	    
	    label = new Label("Please choose a file.");
	    label.setPrefHeight(20);
	    label.setStyle("-fx-border-color: dimgray; -fx-border-radius: 3;-fx-border-width: 2; -fx-background-color: darkgray; -fx-background-radius: 3;");
	    label.setAlignment(Pos.BASELINE_CENTER);
	    label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
	    
	    hbox.getChildren().addAll(startVeri, label);
		return hbox;
	}

	private void openDiagrams(ArrayList<Actor> actors) 
	{
		Iterator<Actor> actorIt = actors.iterator();
		while (actorIt.hasNext()) 
		{
			Actor actor = actorIt.next();
			
			Graph<State, Edge> graph = new SparseMultigraph<State, Edge>();
			
			
			Iterator<State> statesIt = actor.getInternStateIt();
			while (statesIt.hasNext()) 
			{
				State state = statesIt.next();
				
				graph.addVertex(state);
				//TODO configure
				
				//FIXME HACK (only edges originating in a non-placeholder-state)
				Iterator<Edge> edgesIt = state.getEdgeIt();
				while (edgesIt.hasNext()) 
				{
					Edge edge = edgesIt.next();
					if (edge.getTarget() == null)
					{
						graph.addEdge(edge, edge.getSource(), new State(null, null), EdgeType.DIRECTED);
					}
					else
					{
						graph.addEdge(edge, edge.getSource(), edge.getTarget(), EdgeType.DIRECTED);
					}
				}
			}
			
			Layout<State, Edge> graphLayout = new FRLayout<State, Edge>(graph);
			graphLayout.setSize(new Dimension(200 + actor.size() * 100, actor.size() * 50));
			BasicVisualizationServer<State, Edge> vs = new BasicVisualizationServer<>(graphLayout);
      		
            // Setup up a new vertex to paint transformer...
      		
            Transformer<State,Paint> vertexPaint = new Transformer<State,Paint>() 
            {
				@Override
				public Paint transform(State state) 
				{
					if (state.getType() == null)
					{
						return Color.WHITE;
					}
					else
					{
						switch (state.getType())
						{
						case FUNCTION:
							return Color.BLUE;
						case GENERAL:
							return Color.GRAY;
						case RECIEVE:
							return Color.ORANGE;
						case SEND:
							return Color.GREEN;
						default:
							return Color.RED;
						}
					}
				}
            };  
            // Set up a new stroke Transformer for the edges
            float dash[] = {10.0f};
            //TODO use: new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL, 10.0f, dash, 0.0f);
            final Stroke edgeStroke = new BasicStroke();
            
            Transformer<Edge, Stroke> edgeStrokeTrans = new Transformer<Edge, Stroke>() 
            {
				@Override
				public Stroke transform(Edge arg0) 
				{
					return edgeStroke;
				}
            };
            vs.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
            vs.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTrans);
            vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<State>());
            //vs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());
            vs.getRenderer().getVertexLabelRenderer().setPosition(Position.SE);
      		vs.setPreferredSize(new Dimension(100 + 200 + actor.size() * 100, 50 + actor.size() * 50));
      		
      		final SwingNode swingNode = new SwingNode();
      		
      		javax.swing.SwingUtilities.invokeLater(new Runnable() 
      		{
                  @Override
                  public void run() 
                  {
                      swingNode.setContent(vs);
                  }
              });
    	    
      		BorderPane pane = new BorderPane();
      		pane.setTop(swingNode);
      		pane.setPrefSize(100 + 200 + actor.size() * 100, 50 + actor.size() * 50);
      		
    	    Stage diagram = new Stage();
    	    diagram.setTitle(actor.getSubjectName());
    	    diagram.setScene(new Scene(pane));
    	    diagram.show();
		}
	}
	
	protected void startVerification() 
	{
		if (rdfFiles != null)
		{
			Log.createLog();
			
			if (logAlgoDeepEdge.isSelected())
			{
				Log.addLogType(LogType.ALGO_DEEP_EDGE);
			}
			if (logAlgoDeepState.isSelected())
			{
				Log.addLogType(LogType.ALGO_DEEP_STATE);
			}
			if (logAlgoProblem.isSelected())
			{
				Log.addLogType(LogType.ALGO_PROBLEM);
			}
			if (logAlgoRecursion.isSelected())
			{
				Log.addLogType(LogType.ALGO_RECURSION);
			}
			if (logDataTriple.isSelected())
			{
				Log.addLogType(LogType.DATA_TRIPLE);
			}
			if (logDataSubject.isSelected())
			{
				Log.addLogType(LogType.DATA_SUBJECT);
			}
			if (logDataCreation.isSelected())
			{
				Log.addLogType(LogType.DATA_CREATION);
			}
			if (logDataConnection.isSelected())
			{
				Log.addLogType(LogType.DATA_CONNECTION);
			}
			if (logDataDeepCreation.isSelected())
			{
				Log.addLogType(LogType.DATA_DEEP_CREATION);
			}
			if (logDataDeepConnection.isSelected())
			{
				Log.addLogType(LogType.DATA_DEEP_CONNECTION);
			}
			
			Interpreter interpreter = new Interpreter(rdfFiles);
			Veryfier verifier = new Veryfier();
			
			if(checkRec.isSelected())
			{
				verifier.addVeryType(VeriType.VERI_RECURSIVE);
			}
			if(checkImp.isSelected())
			{
				verifier.addVeryType(VeriType.VERI_IMP);
			}
			if(checkCompEdges.isSelected())
			{
				verifier.addVeryType(VeriType.VERI_COMP_EDGES);
			}
			if(checkCompStates.isSelected())
			{
				verifier.addVeryType(VeriType.VERI_COMP_STATES);
			}
			
			try 
			{
				interpreter.calculate();
			} 
			catch (Exception e) 
			{
				System.err.println("Could not calculate >>" + e.getMessage() + "<<");
				return;
			}
			if (verifier.verifyImplementation(interpreter.getActors()))
			{
				System.out.println("Implementation veryfied.");
				label.setText("Implementation veryfied.");
				label.setStyle("-fx-border-color: green; -fx-border-radius: 3; -fx-border-width: 2; -fx-background-color: lightgreen; -fx-background-radius: 3;");
			}
			else
			{
				System.out.println("Implementation falsified.");
				label.setText("Implementation falsified.");
				label.setStyle("-fx-border-color: red; -fx-border-radius: 3; -fx-border-width: 2; -fx-background-color: lightcoral; -fx-background-radius: 3;");
			}
			
			File log = Log.finishLog(logDirectory);
			
			String logPath = "";
			try
			{
				logPath = log.getCanonicalPath();
			}
			catch (Exception e)
			{
				System.err.println("Could not show the LogFile");
			}
			
			if (log.isFile())
			{
				getHostServices().showDocument(logPath);
			}
			
      		openDiagrams(interpreter.getActors());
		}
	}
}
