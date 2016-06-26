package structure;

import java.util.ArrayList;
import java.util.Iterator;
import enumeration.Log;
import enumeration.LogType;
import enumeration.TransitionType;



/**
 * @author jan/fabian
 * represents connections between intra-actor-states (Elements)
 *
 */ //TODO expand functionality: successionTransition
public class Edge 
{
//attributes
	private String subjectName;
	private ArrayList<String> labels;
	private String componentID;
	private ArrayList<Message> messages;
	private State source;
	private State target;
	private TransitionType type;
	//TODO fabian maybe you need some getter and setter for some new things?
	
//RAW attributes
	private String targetRaw;
	private String sourceRaw;
	private ArrayList<String> messagesRaw;
	
//NON-RAW attributes
	//TODO attributes+ corresponding getter+setter
	
//constructors
	
	public Edge(String subjectName, String componentID)
	{
		this.subjectName = subjectName;
		this.messages = new ArrayList<Message>();
		this.labels = new ArrayList<String>();
		this.messagesRaw = new ArrayList<String>();
		this.componentID = componentID;
		
		Log.println("Edge created: " + componentID, LogType.DATA_CREATION);
	}
	
	
//functions
	public String getSubjectName()
	{
		return this.subjectName;
	}
	
	public void setType(TransitionType type)
	{
		this.type = type;
	}
	
	public TransitionType getType()
	{
		return this.type;
	}
	
	public String getComponentID()
	{
		return this.componentID;
	}
	
	public Iterator<String> getLabelsIt()
	{
		return this.labels.iterator();
	}
	
	public void addLabel(String label)
	{
		this.labels.add(label);
	}
	
	public String getTargetRaw()
	{
		return this.targetRaw;
	}
	
	public State getTarget()
	{
		return this.target;
	}
	
	public String getSourceRaw()
	{
		return this.sourceRaw;
	}
	
	public State getSource(){
		return this.source;
	}
	
	public void setTargetRaw(String target)
	{
		this.targetRaw = target;
	}
	
	public void setTarget(State target)
	{
		this.target = target;
	}
	
	public void setSourceRaw(String source)
	{
		this.sourceRaw = source;
	}
	
	public void setSource(State source)
	{
		this.source = source;
	}
	
	public void addMessage(Message message)
	{
		this.messages.add(message);
	}
	
	public Iterator<Message> getMessageIt()
	{
		return messages.iterator();
	}
	
	public void addMessagesRaw(String message){
		this.messagesRaw.add(message);
	}
	
	public Iterator<String> getMessagesRawIt(){
		return this.messagesRaw.iterator();
	}
	
}
