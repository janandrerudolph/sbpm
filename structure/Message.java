package structure;

import java.util.ArrayList;
import java.util.Iterator;
import enumeration.Log;
import enumeration.LogType;

/**
 * 
 * @author FabianFalck
 *
 */

public class Message
{
//attributes
	private String subjectName;
	private ArrayList<String> labels;
	private String componentID;
	private boolean isAbstract; 
	
//RAW attributes
	private String senderRaw;
	private String receiverRaw;
	private ArrayList<String> implementedMessagesRaw;//References to MessageSpec
	
//NON-RAW attributes
	private Actor sender;
	private Actor receiver;
	private ArrayList<Message> implementedMessages;//References to MessageSpec
	
	
//constructors
	public Message(String subjectName, String componentID)
	{
		this.subjectName = subjectName;
		this.labels = new ArrayList<String>();
		this.componentID = componentID;
		this.isAbstract = false;
		
		this.implementedMessagesRaw = new ArrayList<String>();
		
		this.implementedMessages = new ArrayList<Message>();
		
		Log.println("Message created: " + componentID, LogType.DATA_CREATION);
	}
	
	
//functions
	public String getSubjectName()
	{
		return this.subjectName;
	}
	
	public String getComponentId()
	{
		return this.componentID;
	}
	
	public void setIsAbstract()
	{
		this.isAbstract = true;
	}
	
	public boolean getIsAbstract()
	{
		return this.isAbstract;
	}
	
	public void addLabel(String label)
	{
		this.labels.add(label);
	}
	
	public Iterator<String> getLabelsIt() 
	{
		return this.labels.iterator();
	}
	
	public void setSenderRaw(String sender)
	{
		this.senderRaw = sender;
	}
	
	public String getSenderRaw() 
	{
		return this.senderRaw;
	}
	
	public void setSender(Actor sender)
	{
		this.sender = sender;
	}
	
	public Actor getSender() 
	{
		return this.sender;
	}
	
	public void setReceiverRaw(String receiver)
	{
		this.receiverRaw = receiver;
	}
	
	public String getReceiverRaw() 
	{
		return this.receiverRaw;
	}
	
	public void setReceiver(Actor receiver)
	{
		this.receiver = receiver;
	}
	
	public Actor getReceiver() 
	{
		return this.receiver;
	}
	
	public void addImplementedMessagesRaw(String message)
	{
		this.implementedMessagesRaw.add(message);
	}
	
	public Iterator<String> getImplementedMessagesRawIt() 
	{
		return this.implementedMessagesRaw.iterator();
	}
	
	public void addImplementedMessages(Message message)
	{
		this.implementedMessages.add(message);
	}
	
	public Iterator<Message> getImplementedMessagesIt() 
	{
		return this.implementedMessages.iterator();
	}
	
	
	
}
