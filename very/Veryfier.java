package very;

import java.util.ArrayList;
import java.util.Iterator;
import enumeration.Log;
import enumeration.LogType;
import enumeration.VeryType;
import structure.Actor;
import structure.Edge;
import structure.State;
import structure.Message;

public class Veryfier 
{
//attributes
	private ArrayList<String> problems;
	

//constructors
	public Veryfier ()
	{
		this.problems = new ArrayList<String>();
	}


//functions
	public boolean verifyImplementation(ArrayList<Actor> actors) 
	{
		Iterator<Actor> actorsIt = actors.iterator();
		while(actorsIt.hasNext())
		{
			Actor actor = actorsIt.next();
			if (!actor.getIsAbstract())
			{
				Iterator<Actor> abstractActorIt = actor.getImplementedAbstractActorsIt();
				while (abstractActorIt.hasNext()) 
				{
					//this means the intersection of rules of all implemented abstract actors must be fulfilled (stronger statement than the combined set!)
					Actor abstractActor = abstractActorIt.next();
					Log.println("Initializing semi parallel traversion: " + actor.getComponentID() + " <> " + abstractActor.getComponentID(), LogType.ALGO_RECURSION);
					if (!this.recursiveTraversion(actor.getInitialState(), abstractActor.getInitialState()))//TODO maybe the "problem" could be bit more specific?
					{
						problems.add("Could not match actor " + actor.getComponentID() + " with abstract actor " + abstractActor.getComponentID() + " in the semi parallel traversion.");
					}
				}
				//TODO more for implemented actors?
			}
			else
			//actor is abstract
			{
				this.implementationVery(actor, actors);
				//TODO more for abstract actors?
			}
			//TODO something to check for any (no special) kind of actor?
		}		
		//TODO check general information like the communication
		if (this.problems.isEmpty())
		{
			Log.println("Implementation veryfied.", LogType.ALGO_PROBLEM);
			return true;
		}
		else
		{
			Log.println("Implementation falsified:", LogType.ALGO_PROBLEM);
			Iterator<String> it = this.problems.iterator();
			while (it.hasNext())
			{
				Log.println(it.next(), LogType.ALGO_PROBLEM);
			}
			return false;
		}
	}
	
	public void addVeryType (VeryType veryType)
	{
		//TODO fill a list
	}
	
	public Iterator<String> getProblemIt()
	{
		return this.problems.iterator();
	}
	
//private functions
	private boolean recursiveTraversion(State impState, State abstractState)
	{
		assert (impState != null && !impState.getIsAbstract());
		assert (abstractState == null || abstractState.getIsAbstract());
		
		if (impState == null)
		{
			Log.println(">>warning<< The semi parallel traversion was called with an implemented State that does not exist!", LogType.ALGO_PROBLEM);
			return false;
		}
		boolean recursiveAbstract = true;
		boolean recursiveImp = true;
		boolean checkEdgesAbstract = true;
		boolean checkEdgesImp = true;
		boolean stateCompRes = this.compareState(impState, abstractState);
		if (!impState.getIsEndstate()) //TODO or already visited - to avoid getting stuck loops
		{
			//try to find the correct implementation for each abstract edge - and the other way round. If found, go deeper recursively
			Iterator<Edge> impEdgesIt = impState.getEdgeIt();
			while (impEdgesIt.hasNext())
			//for each edge of imp
			{
				Edge impEdge = impEdgesIt.next();
				boolean checkEdgesInnerLoop = false;
				Iterator<Edge> abstractEdgesIt = abstractState.getEdgeIt();
				while (abstractEdgesIt.hasNext())
				//for each edge of abstract
				{
					Edge abstractEdge = abstractEdgesIt.next();
					if (this.compareEdges(impEdge, abstractEdge))
					{
						checkEdgesInnerLoop = true;
						recursiveImp &= this.recursiveTraversion(impEdge.getTarget(), abstractEdge.getTarget());
					}
				}
				checkEdgesImp &= checkEdgesInnerLoop;
			}
			
			Iterator<Edge> abstractEdgesIt = abstractState.getEdgeIt();
			while (abstractEdgesIt.hasNext()) 
			{
				Edge abstractEdge = abstractEdgesIt.next();
				boolean checkEdgesInnerLoop = false;
				Iterator<Edge> ImpEdgesIt = impState.getEdgeIt();
				while (ImpEdgesIt.hasNext()) 
				{ 
					Edge impEdge = ImpEdgesIt.next();
					if (this.compareEdges(impEdge, abstractEdge))
					{
						checkEdgesInnerLoop = true;
						recursiveAbstract &= this.recursiveTraversion(impEdge.getTarget(), abstractEdge.getTarget());
					}	
				}
				checkEdgesAbstract &= checkEdgesInnerLoop;
			}
		}
		
		Log.println("veryfication of " + impState.getComponentID() + ", stateComp: " + stateCompRes + ", recAbs: " 
				+ recursiveAbstract + ", recImp: " + recursiveImp  + ", edges: " + (checkEdgesAbstract && checkEdgesImp), LogType.ALGO_RECURSION);
		
		return (stateCompRes && recursiveAbstract && recursiveImp && checkEdgesAbstract && checkEdgesImp);
	}
	
	private void implementationVery(Actor actor, ArrayList<Actor> actors)
	{
		assert(actor.getIsAbstract());
		//TODO
	}
	
	private boolean compareState(State impState, State abstractState)
	{
		if (abstractState == null)
		{
			Log.println("State implementation true: " 
					+ impState.getComponentID() + " may inherit from a Placeholder.", LogType.ALGO_DEEP_STATE);
			return true;
		}
		else
		{
			boolean checkState = false;
			Iterator<State> abstractStatesIt = impState.getImplementedAbstractStatesIt(); //FIXME nullpointerException if there is an edge without a target (within the implemented actor)
			while (abstractStatesIt.hasNext()) 
			{
				State state = abstractStatesIt.next();
				if (abstractState.equals(state))
				{
					checkState = true;
				}
			}
			Log.println("Edge comparison " + checkState + ": " 
					+ impState.getComponentID() + " < " + abstractState.getComponentID(), LogType.ALGO_DEEP_STATE);
			return checkState;
		}
	}
	
	private boolean compareEdges(Edge impEdge, Edge abstractEdge)
	{
		if (!this.compareState(impEdge.getTarget(), abstractEdge.getTarget()))
		{
			Log.println("Edge comparison false (targets not matching): " 
					+ impEdge.getComponentID() + " < " + abstractEdge.getComponentID(), LogType.ALGO_DEEP_EDGE);
			return false;
		}
		Iterator<Message> impMessagesIt = impEdge.getMessageIt();
		while (impMessagesIt.hasNext()) 
		//for each message of imp
		{
			Message message = impMessagesIt.next();
			boolean findMatchingAbstractMessage = false;
			Iterator<Message> abstractMessagesIt = abstractEdge.getMessageIt();
			while (abstractMessagesIt.hasNext()) 
			//for each message of abstract
			{
				Message absMessage = abstractMessagesIt.next();
				Iterator<Message> implementsIt = message.getImplementedMessagesIt();
				while (implementsIt.hasNext()) 
				{
					if (implementsIt.next().equals(absMessage))
					{
						findMatchingAbstractMessage = true;
					}
				}
			}
			if (!findMatchingAbstractMessage)
			{
				Log.println("Edge comparison false (some abstract message is not implemented): " 
						+ impEdge.getComponentID() + " < " + abstractEdge.getComponentID(), LogType.ALGO_DEEP_EDGE);
				return false;
			}
		}
		
		//TODO other way round
		
		Log.println("Edge comparison true: " + impEdge.getComponentID() + " < " + abstractEdge.getComponentID(), LogType.ALGO_DEEP_EDGE);
		return true;
	}
}
