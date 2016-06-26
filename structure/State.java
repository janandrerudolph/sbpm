package structure;

import java.util.ArrayList;
import java.util.Iterator;
import enumeration.Log;
import enumeration.StateType;
import enumeration.LogType;


//TODO expand functionality: GeneralAbstractState, PlaceHolder
public class State extends Element 
{
//attributes
	private String subjectName;
	private ArrayList<String> labels;
	private String componentID;
	private StateType type;
	private boolean isAbstract;
	private boolean isInitial;
	private boolean isEndstate;
	//TODO fabian maybe you need a getter and setter for the general abstract state?
	
//RAW attributes (they store the Strings while the Interpreter has not yet created the linked States or Edges)
	private ArrayList<String> edgesRaw;
	private ArrayList<String> implementedAbstractStatesRaw;

//NON-RAW attributes
	private ArrayList<Edge> edges;
	private ArrayList<State> implementedAbstractStates;
	
	
//constructors
	/**
	 * @param name the "hasModelComponentLable"
	 * @param type a String, whether it represents a Function-/Send-/RecieveState
	 */
	public State (String subjectName, String componentID)
	{
		super();
		this.subjectName = subjectName;
		this.labels = new ArrayList<String>();
		this.componentID = componentID;
		this.isAbstract = false;
		this.isInitial = false;
		this.isEndstate = false;
		this.edges = new ArrayList<Edge>();
		this.edgesRaw = new ArrayList<String>();
		this.implementedAbstractStates = new ArrayList<State>();
		this.implementedAbstractStatesRaw = new ArrayList<String>();
		
		Log.println("State created: " + componentID, LogType.DATA_CREATION);
	}
	
	
//functions
	
	public String getSubjectName()
	{
		return this.subjectName;
	}
	
	public void setAbstract()
	{
		this.isAbstract = true;
	}
	
	public boolean getIsAbstract()
	{
		return this.isAbstract;
	}
	
	public void addImplementedAbstractStatesRaw(String state)
	{
		this.implementedAbstractStatesRaw.add(state);
	}
	
	public Iterator<String> getImplementedAbstractStatesRawIt() 
	{
		return this.implementedAbstractStatesRaw.iterator();
	}
	
	public void addImplementedAbstractState(State state)
	{
		this.implementedAbstractStates.add(state);
	}
	
	public Iterator<State> getImplementedAbstractStatesIt() 
	{
		return this.implementedAbstractStates.iterator();
	}
	
	public void addLabel(String label)
	{
		this.labels.add(label);
	}
	
	public Iterator<String> getLabelsIt() 
	{
		return this.labels.iterator();
	}
	
	public String getComponentID() 
	{
		return this.componentID;
	}
	
	/**this function should be called early, since other classes might be assuming, that every State has a type
	 * @param type the type of the State
	 */
	public void setType(StateType type)
	{
		this.type = type;
	}
	
	/**
	 * @return the Type of the State. Attention: can return null, if there is none set
	 */
	public StateType getType()
	{
		return this.type;
	}
	
	public void addEdgeRaw(String edge)
	{
		this.edgesRaw.add(edge);
	}
	
	public Iterator<String> getEdgesRawIt() 
	{
		return this.edgesRaw.iterator();
	}
	
	public void addEdge(Edge edge)
	{
		this.edges.add(edge);
	}
	
	public Iterator<Edge> getEdgeIt()
	{
		return this.edges.iterator();
	}

	public boolean getIsInitial() 
	{
		return isInitial;
	}

	public void setIsInitial() 
	{
		this.isInitial = true;
	}

	public boolean getIsEndstate() 
	{
		return isEndstate;
	}

	public void setIsEndstate() 
	{
		this.isEndstate = true;
	}
}
