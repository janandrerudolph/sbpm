package structure;

import java.util.ArrayList;
import java.util.Iterator;
import enumeration.Log;
import enumeration.LogType;



public class Actor 
{
//attributes
	private String subjectName;
	private String componentID;
	private ArrayList<String> labels;
	private boolean isAbstract;
	
	
	private ArrayList<State> internStates; //ATTENTION: is actually empty for implemented actors who do not inherit from a placeholder
	private State initialState;
	private ArrayList<Message> outMessages;
	private ArrayList<Actor> implementedAbstractActors;
	
	
//raw attributes
	private ArrayList<String> implementedAbstractActorsRaw; 
	private ArrayList<String> internStatesRaw;
	private ArrayList<String> inheritedStatesRaw;
	
	
//constructors
	public Actor(String subjectName, String componentID)
	{
		this.subjectName = subjectName;
		this.componentID = componentID;
		this.isAbstract = false;
		this.labels = new ArrayList<String>();
		this.internStates = new ArrayList<State>();
		this.outMessages = new ArrayList<Message>();
		this.implementedAbstractActors = new ArrayList<Actor>();
		
		this.implementedAbstractActorsRaw = new ArrayList<String>();
		this.internStatesRaw = new ArrayList<String>();
		this.inheritedStatesRaw = new ArrayList<String>();
		
		Log.println("Actor created: " + componentID, LogType.DATA_CREATION);
	}
	
	
//functions	
	public int size()
	{
		return this.internStates.size();
	}
	
	public String getSubjectName()
	{
		return this.subjectName;
	}
	
	public String getComponentID()
	{
		return this.componentID;
	}
	
	public Iterator<State> getInternStateIt()
	{
		return internStates.iterator();		
	}
	
	public void addInternState(State state)
	{
		this.internStates.add(state);
	}
	
	public State getInitialState()
	{
		if (this.initialState != null)
		{
			return this.initialState;
		}
		else
		{
			Iterator<State> internStatesIt = this.getInternStateIt();
			while (internStatesIt.hasNext()) 
			{
				State state = internStatesIt.next();
				if (state.getIsInitial())
				{
					this.setInitialState(state);
					return state;
				}
			}
			Log.println(">>warning<< No initialState could be found for Actor " + this.componentID, LogType.DATA_CONNECTION);
			return null;
		}
	}
	
	public void setInitialState(State initialState)
	{
		this.initialState = initialState;
	}
	
	public Iterator<Message> getOutMessageIt()
	{
		return outMessages.iterator();
	}
	
	public void addOutMessage(Message outMessage){
		this.outMessages.add(outMessage);
	}
	
	public void addLabel(String label)
	{
		this.labels.add(label);
	}
	
	public Iterator<String> getLabelsIt() 
	{
		return this.labels.iterator();
	}
	
	public void addImplementedAbstractActorsRaw(String actor)
	{
		this.implementedAbstractActorsRaw.add(actor);
	}
	
	public Iterator<String> getImplementedAbstractActorsRawIt() 
	{
		return this.implementedAbstractActorsRaw.iterator();
	}
	
	public void setAbstract()
	{
		this.isAbstract = true;
	}
	
	public boolean getIsAbstract()
	{
		return this.isAbstract;
	}
	
	public void addImplementedAbstractActor (Actor implementedAbstractActor)
	{
		this.implementedAbstractActors.add(implementedAbstractActor);
	}
	
	public Iterator<Actor> getImplementedAbstractActorsIt () 
	{
		return this.implementedAbstractActors.iterator();
	}

	/**
	 * @return an iterator over the internStatesRaw
	 */
	public Iterator<String> getInternStatesRawIt() {
		return internStatesRaw.iterator();
	}

	/**
	 * @param state to add to internStatesRaw
	 */
	public void addInternStatesRaw(String internStatesRaw) {
		this.internStatesRaw.add(internStatesRaw);
	}
	
	/**
	 * @return an Iterator over the inheritedStatesRaw
	 */
	public Iterator<String> getInheritedStatesRawIt() {
		return inheritedStatesRaw.iterator();
	}

	/**
	 * @param  state to add to internStatesRaw
	 */
	public void addInheritedStatesRaw(String stateRaw) {
		this.inheritedStatesRaw.add(stateRaw);
	}
}
