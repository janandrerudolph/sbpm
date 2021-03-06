package init;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.jena.rdf.model.*;

import enumeration.Log;
import enumeration.LogType;
import enumeration.StateType;
import enumeration.TransitionType;
import structure.Actor;
import structure.Edge;
import structure.Message;
import structure.State;



/**
 * Parses the RFD input and interprets it in a usable way, initialized in the main method
 * @author Fabian Falck / Jan Rudolph
 * 
 *
 */
public class Interpreter 
{
//attributes
	
	///Jena
	private ArrayList<File> files;
	private ArrayList<Model> models;
	
	///bridge 1
	private ArrayList<Statement> statements;
	private ArrayList<Triple> triples;
	private ArrayList<Subject> subjects;
	
	///bridge 2
	private ArrayList<State> statesRaw;
	private ArrayList<Actor> actorsRaw;
	private ArrayList<Edge> edgesRaw;
	private ArrayList<Message> messagesRaw;
	
	///data structure
	private ArrayList<State> states;
	private ArrayList<Actor> actors;
	private ArrayList<Edge> edges;
	private ArrayList<Message> messages;
	
	
//constructors
	
	/**uses Java Apache API to read the RFD file
	 * @param RFD file link which shall be read
	 */
	public Interpreter(ArrayList<File> rdfFiles)
	{
		///Jena
		this.files = rdfFiles;
		this.models = new ArrayList<Model>();
		
		///bridge 1
		this.statements = new ArrayList<Statement>();
		this.triples = new ArrayList<Triple>();
		this.subjects = new ArrayList<Subject>();
		
		///bridge 2
		this.statesRaw = new ArrayList<State>();
		this.actorsRaw = new ArrayList<Actor>();
		this.edgesRaw = new ArrayList<Edge>();
		this.messagesRaw = new ArrayList<Message>();
		
		///data structure
		this.states = new ArrayList<State>();
		this.actors = new ArrayList<Actor>();
		this.edges = new ArrayList<Edge>();
		this.messages = new ArrayList<Message>();
	}
	
	
//functions
	
	///Jena => data structure
	/**executes parsing into human readable form, then saves triples into lists sorted by prefix (e.g. transitions)
	 * and groups them by units (e.g. transitionsSub)
	 * @throws if model is for any reason not readable (e.g. incorrectly specified)
	 */
	public void calculate() throws Exception
	{
		Iterator<File> linkIt = this.files.iterator();
		while (linkIt.hasNext()) 
		{
			Model model = ModelFactory.createDefaultModel();
			String link = linkIt.next().getCanonicalPath();
			try
			{
				model.read(link);
				this.models.add(model);
				Log.println("Model added", LogType.DATA_CREATION);
			} 
			catch (Exception e) 
			{
				throw new Exception("Could not read the content with Apache Jena >>" + e.getMessage() + "<<");
			}
			
			StmtIterator iter= model.listStatements();
			while (iter.hasNext()) 
			{
			    Statement statement = iter.nextStatement();  // get next statement
			    this.statements.add(statement);
			}
		}
		
		
		this.parseTriplesWithoutPrefixes();
		Log.println("================================================================", LogType.DATA_TRIPLE);
		this.findSubjects();
		Log.println("", LogType.DATA_SUBJECT);
		Log.println("================================================================", LogType.DATA_SUBJECT);
		this.interpretAsDataStructure();
		Log.println("================================================================", LogType.DATA_CREATION);
		this.connectObjects();
		Log.println("================================================================", LogType.DATA_CONNECTION);
	}
	
	public ArrayList<Actor> getActors()
	{
		return this.actors;
	}
	
	
//private functions
	
	///Jena => bridge 1
	/**transforms the cryptic output of Java Apache into an easy to understand form without loosing information
	 * ATTENTION: this function must be called to fill the "triples"-List
	 * @throws Exception 
	 */
	private void parseTriplesWithoutPrefixes()
	{	
		Iterator<Model> modelIt = this.models.iterator();
		while (modelIt.hasNext())
		{
			Model model = modelIt.next();
			Iterator<Statement> statementIterator = this.statements.iterator();
			while (statementIterator.hasNext())
			{
				Statement statement = statementIterator.next();
				String subject = statement.getSubject().toString();
				String predicate = statement.getPredicate().toString();
				String object = statement.getObject().toString();
				
				subject = model.shortForm(subject);
				predicate = model.shortForm(predicate);
				object = model.shortForm(object);
				
				if (subject.contains(":"))
				{
					String[] parts = subject.split(":");
					subject = parts[parts.length - 1];
				}
				if (predicate.contains(":"))
				{
					String[] parts = predicate.split(":");
					predicate = parts[parts.length - 1];
				}
				if (object.contains(":"))
				{
					String[] parts = object.split(":");
					object = parts[parts.length - 1];
				}

				if (statement.getObject() instanceof Resource)
				{
					this.triples.add(new Triple(subject, predicate, object, true));
				}
				else
				{
					this.triples.add(new Triple(subject, predicate, object, false));
				}
			}
		}
	}
	
	///Jena => bridge 1
	/**filling the Units-List
	 * ATTENTION: needs the Triples-List to be filled correctly
	 * Assertion: Apache Jena does not "mix" the different Subjects
	 */
	private void findSubjects()
	{
		ArrayList<Triple> collectEqualTriples = new ArrayList<Triple>();
		Iterator<Triple> tripleIt = this.triples.iterator();
		String lastSubject= "start";
		while(tripleIt.hasNext())
		{
			Triple currentTriple = tripleIt.next();
			if(!lastSubject.equals("start") && !currentTriple.getSubject().equals(lastSubject))
			{
				this.subjects.add(new Subject(collectEqualTriples));
				collectEqualTriples = new ArrayList<Triple>();
			}
			collectEqualTriples.add(currentTriple); 
			lastSubject = currentTriple.getSubject();
		}
		this.subjects.add(new Subject(collectEqualTriples));
	}
	
	///bridge 1 => bridge 2
	/**fills the *Raw Lists with Actors, States, Edges and Messages based on the Subjects-List
	 * ATTENTION: Links to other objects are not yet set! But "they"(Actors, ...) hold the representative RDF-Strings.
	 */
	private void interpretAsDataStructure()
	{
		Iterator<Subject> subjectIt = this.subjects.iterator();
		while (subjectIt.hasNext())
		{
			Subject subject = subjectIt.next();
			ArrayList<String> typeObjects = subject.getObjectsDependingOnPredicate("type");
			if (typeObjects.contains("FunctionState") //TODO what about GeneralAbstractState?
					|| typeObjects.contains("SendState") 
					|| typeObjects.contains("ReceiveState")
					|| typeObjects.contains("AbstractFunctionState")
					|| typeObjects.contains("AbstractSendState") 
					|| typeObjects.contains("AbstractReceiveState"))
				//this Subject is a state (abstract or implemented)
			{
				this.interpretAsState(subject);
			}
			else if (typeObjects.contains("StandardTransition") //TODO what about the new abstract versions?
					|| typeObjects.contains("SendTransition") 
					|| typeObjects.contains("ReceiveTransition") 
					|| typeObjects.contains("TriggerTransition"))
				//this Subject is an edge/transition (abstract or implemented)
			{
				this.interpretAsEdge(subject);
			}
			else if (typeObjects.contains("SingleActor")
					|| typeObjects.contains("AbstractActor")) 
			{
				this.interpretAsActor(subject);
			}
			else if (typeObjects.contains("MessageExchange")) 
			{
				this.interpretAsMessage(subject); 
			}
			else
			{
				//Layer, AbstractLayer, PASSProcessModel, Behavior, Class(, MessageSpec)
			}
		}
	}
	
	///bridge 1 => bridge 2
	/**transforms transition triples into edges
	 * @param subjectIt iterator above all subjects with transition prefix
	 */
	private void interpretAsEdge(Subject subject)
	{
		Edge edge;
		if (subject.getObjectsDependingOnPredicate("hasModelComponentID").size() == 1)
		{
			edge = new Edge(subject.getSubjectName(), subject.getObjectsDependingOnPredicate("hasModelComponentID").get(0));
		}
		else
		{
			edge = new Edge(subject.getSubjectName(), "has " + subject.getObjectsDependingOnPredicate("hasModelComponentID").size()
					+ " Component IDs" + ">>warning<<");
		}
		
		if (subject.getObjectsDependingOnPredicate("hasSourceState").size() == 1)
		{
			edge.setSourceRaw(subject.getObjectsDependingOnPredicate("hasSourceState").get(0));
		}
		else
		{
			Log.println(">>warning<< " + edge.getComponentID() + " has no SourceState", LogType.DATA_CREATION);
		}
		
		if (subject.getObjectsDependingOnPredicate("hasTargetState").size() == 1)
		{
			edge.setTargetRaw(subject.getObjectsDependingOnPredicate("hasTargetState").get(0));
		}
		else
		{
			//TODO does this one point to a placeholder?
		}
		
		Iterator<String> labelObjectsIt = subject.getObjectsDependingOnPredicate("hasModelComponentLable").iterator();
		while (labelObjectsIt.hasNext())
		{
			edge.addLabel(labelObjectsIt.next());
		}
		
		Iterator<String> referenceObjectsIt = subject.getObjectsDependingOnPredicate("refersTo").iterator();
		while (referenceObjectsIt.hasNext())
		{
			edge.addMessagesRaw(referenceObjectsIt.next());
		}
		Iterator<String> reference2ObjectsIt = subject.getObjectsDependingOnPredicate("references").iterator();
		while (reference2ObjectsIt.hasNext())
		{
			edge.addMessagesRaw(reference2ObjectsIt.next());
		}
		//refersTo and references are saved in the same ArrayList
		
		Iterator<String> typeObjectsIt = subject.getObjectsDependingOnPredicate("type").iterator();
		while (typeObjectsIt.hasNext())
		{
			switch (typeObjectsIt.next())
			{
			case "NamedIndividual":
				//do nothing
				break;
			
			case "StandardTransition":
				edge.setType(TransitionType.STANDARD);
				break;
				
			case "SendTransition":
				edge.setType(TransitionType.SEND);
				break;
				
			case "ReceiveTransition":
				edge.setType(TransitionType.RECEIVE);
				break;
				
			case "TriggerTransition":
				edge.setType(TransitionType.TRIGGER);
				break;
				
			case "TriggerSendTransition":
				edge.setType(TransitionType.TRIGGER_SEND);
				break;
				
			case "TriggerReceiveTransition":
				edge.setType(TransitionType.TRIGGER_RECEIVE);
				break;
				
			case "SuccessionTransition":
				edge.setType(TransitionType.SUCCESSION);
				break;
				
			case "SuccessionSendTransition":
				edge.setType(TransitionType.SUCCESSION_SEND);
				break;
				
			case "SuccessionReceiveTransition":
				edge.setType(TransitionType.SUCCESSION_RECEIVE);
				break;
				
			default:
				//This should not happen
				assert(false);
				break;
			}
		}
		this.edgesRaw.add(edge);
	}
	
	///bridge 1 => bridge 2
	private void interpretAsState(Subject subject)
	{
		State state;
		if (subject.getObjectsDependingOnPredicate("hasModelComponentID").size() == 1)
		{
			state = new State(subject.getSubjectName(), subject.getObjectsDependingOnPredicate("hasModelComponentID").get(0));
		}
		else
		{
			state = new State(subject.getSubjectName(), "has " + subject.getObjectsDependingOnPredicate("hasModelComponentID").size()
					+ " Component IDs" + ">>warning<<");//TODO might this be a mistake? if so, we can throw an Error
		}
		
		Iterator<String> labelObjectsIt = subject.getObjectsDependingOnPredicate("hasComponentLable").iterator(); //TODO Label or Lable?
		while (labelObjectsIt.hasNext())
		{
			state.addLabel(labelObjectsIt.next());
		}
		
		Iterator<String> implementsObjectsIt = subject.getObjectsDependingOnPredicate("implements").iterator();
		while (implementsObjectsIt.hasNext())
		{
			state.addImplementedAbstractStatesRaw(implementsObjectsIt.next());
		}
		
		Iterator<String> typeObjectsIt = subject.getObjectsDependingOnPredicate("type").iterator();
		while (typeObjectsIt.hasNext())
		{
			switch (typeObjectsIt.next())
			{
			case "NamedIndividual":
				//do nothing
				break;
			
			case "GeneralAbstractState":
				state.setType(StateType.GENERAL);
				break;
				
			case "FunctionState":
				state.setType(StateType.FUNCTION);
				break;
				
			case "SendState":
				state.setType(StateType.SEND);
				break;
				
			case "ReceiveState":
				state.setType(StateType.RECIEVE);
				break;
				
			case "InitialState":
				state.setIsInitial();
				break;
				
			case "EndState":
				state.setIsEndstate();
				break;
				
			case "AbstractFunctionState":
				state.setType(StateType.FUNCTION);
				state.setAbstract();
				break;
				
			case "AbstractSendState":
				state.setType(StateType.SEND);
				state.setAbstract();
				break;
				
			case "AbstractReceiveState":
				state.setType(StateType.RECIEVE);
				state.setAbstract();
				break;
				
			//TODO fabian case general abstract state
																
			default:
				//This should not happen
				break;
			}
		}
		statesRaw.add(state);
	}
	
	///bridge 1 => bridge 2
	private void interpretAsActor(Subject subject)
	{
		Actor actor;
		
		if (subject.getObjectsDependingOnPredicate("hasModelComponentID").size() == 1)
		{
			actor = new Actor(subject.getSubjectName(), subject.getObjectsDependingOnPredicate("hasModelComponentID").get(0));
		}
		else
		{
			actor = new Actor(subject.getSubjectName(), "has " + subject.getObjectsDependingOnPredicate("hasModelComponentID").size()
					+ " Component IDs" + ">>warning<<");//TODO might this be a mistake? if so, we can throw an Error
		}
		
		if (subject.getObjectsDependingOnPredicate("hasBehavior").size() == 1)
		{
			Subject behaviour = null;
			String behaviourRaw = subject.getObjectsDependingOnPredicate("hasBehavior").get(0);
			Iterator<Subject> subjectIt = this.subjects.iterator();
			while (subjectIt.hasNext()) 
			{
				Subject potentiallyConnectedBehaviour = subjectIt.next();
				if (potentiallyConnectedBehaviour.getSubjectName().equals(behaviourRaw))
				{
					behaviour = potentiallyConnectedBehaviour;
				}
			}
			
			
			if (behaviour == null)
			{
				//TODO might this be a mistake? if so, we can throw an Error
			}
			else if (!behaviour.getObjectsDependingOnPredicate("hasState").isEmpty())
			{
				Iterator<String> containedStateIt = behaviour.getObjectsDependingOnPredicate("hasState").iterator();
				while (containedStateIt.hasNext()) 
				{
					actor.addInternStatesRaw(containedStateIt.next());
				}
			}
			else if (behaviour.getObjectsDependingOnPredicate("implements").size() == 1)
			{
				Subject abstractBehaviour = null;
				String abstractBehaviourRaw = behaviour.getObjectsDependingOnPredicate("implements").get(0);
				Iterator<Subject> subjectIt2 = this.subjects.iterator();
				while (subjectIt2.hasNext()) 
				{
					Subject potentiallyConnectedBehaviour = subjectIt2.next();
					if (potentiallyConnectedBehaviour.getSubjectName().equals(abstractBehaviourRaw))
					{
						abstractBehaviour = potentiallyConnectedBehaviour;
					}
				}
				
				
				if (abstractBehaviour == null)
				{
					//TODO might this be a mistake? if so, we can throw an Error
				}
				else 
				{
					Iterator<String> containedStateIt = abstractBehaviour.getObjectsDependingOnPredicate("hasState").iterator();
					while (containedStateIt.hasNext()) 
					{
						actor.addInheritedStatesRaw(containedStateIt.next());
					}
				}
			}
			else 
			{
				//TODO if we get here, we got a problem
			}
		}
		else
		{
			//TODO might this be a mistake? if so, we can throw an Error
		}
		
		Iterator<String> labelObjectsIt = subject.getObjectsDependingOnPredicate("hasComponentLable").iterator(); 
		while (labelObjectsIt.hasNext())
		{
			actor.addLabel(labelObjectsIt.next());
		}
		
		Iterator<String> implementsObjectsIt = subject.getObjectsDependingOnPredicate("implements").iterator();
		while (implementsObjectsIt.hasNext())
		{
			actor.addImplementedAbstractActorsRaw(implementsObjectsIt.next());
		}
		
		Iterator<String> typeObjectsIt = subject.getObjectsDependingOnPredicate("type").iterator();
		while (typeObjectsIt.hasNext())
		{
			switch (typeObjectsIt.next())
			{
			case "NamedIndividual":
				//nothing to do
				break;
			
			case "SingleActor":
				assert (actor.getIsAbstract() == false);
				break;
				
			case "AbstractActor":
				actor.setAbstract();
				break;
																
			default:
				//This should not happen
				break;
			}
		}
				
		actorsRaw.add(actor);		
	}
	
	
	///bridge 1 => bridge 2
	private void interpretAsMessage(Subject subject)
	{
		Message message;
		if (subject.getObjectsDependingOnPredicate("hasModelComponentID").size() == 1)
		{
			message = new Message(subject.getSubjectName(), subject.getObjectsDependingOnPredicate("hasModelComponentID").get(0));
		}
		else
		{
			message = new Message(subject.getSubjectName(), "has " + subject.getObjectsDependingOnPredicate("hasModelComponentID").size()
								+ " Component IDs" + ">>warning<<");//TODO might this be a mistake? if so, we can throw an Error
		}
		
		Iterator<String> labelObjectsIt = subject.getObjectsDependingOnPredicate("hasComponentLable").iterator(); //TODO Label or Lable?
		while (labelObjectsIt.hasNext())
		{
			message.addLabel(labelObjectsIt.next());
		}
		
		Iterator<String> senderObjectsIt = subject.getObjectsDependingOnPredicate("sender").iterator();
		while (senderObjectsIt.hasNext())
		{
			message.setSenderRaw(senderObjectsIt.next());
		}
		
		Iterator<String> receiverObjectsIt = subject.getObjectsDependingOnPredicate("receiver").iterator();
		while (receiverObjectsIt.hasNext())
		{
			message.setReceiverRaw(receiverObjectsIt.next());
		}
		
		Iterator<String> messageTypeObjectsIt = subject.getObjectsDependingOnPredicate("hasMessageType").iterator();
		while (messageTypeObjectsIt.hasNext())
		{
			if(messageTypeObjectsIt.next().contains("MessageSpec"))
			{
				message.setIsAbstract();
			}
		}
		
		Iterator<String> implementsObjectsIt = subject.getObjectsDependingOnPredicate("implements").iterator();
		while (implementsObjectsIt.hasNext())
		{
			message.addImplementedMessagesRaw(implementsObjectsIt.next());
		}
		this.messagesRaw.add(message);
	}
	
	///bridge 2 => data structure
	private void connectObjects()
	{
		Iterator<State> stateIt = this.statesRaw.iterator();
		while (stateIt.hasNext())
		{
			this.states.add(this.connectState(stateIt.next()));
		}
		
		Iterator<Actor> actorIt = this.actorsRaw.iterator();
		while (actorIt.hasNext())
		{
			this.actors.add(this.connectActor(actorIt.next()));
		}
		
		Iterator<Edge> edgeIt = this.edgesRaw.iterator();
		while (edgeIt.hasNext())
		{
			this.edges.add(this.connectEdge(edgeIt.next()));
		}
		
		Iterator<Message> messageIt = this.messagesRaw.iterator();
		while (messageIt.hasNext())
		{
			this.messages.add(this.connectMessage(messageIt.next()));
		}
	}
	
	///bridge 2 => data structure
	/**
	 * Transfers all raw Strings into their actual object representation
	 * @param state with raw attributes
	 * @return state with implemented attributes
	 */
	private State connectState(State state)
	{
		Log.println("connecting State: " + state.getComponentID(), LogType.DATA_DEEP_CONNECTION);
		Iterator<String> implementedAbstractStatesRawIt = state.getImplementedAbstractStatesRawIt();
		while (implementedAbstractStatesRawIt.hasNext())
		{
			String abstractStateRaw = implementedAbstractStatesRawIt.next();
			Iterator<State> statesIt = this.statesRaw.iterator();
			while (statesIt.hasNext()) {
				State potentialImplementedAbstractState = statesIt.next();
				if (potentialImplementedAbstractState.getSubjectName().equals(abstractStateRaw))
				{
					state.addImplementedAbstractState(potentialImplementedAbstractState);
					Log.println("    implements: " + potentialImplementedAbstractState.getComponentID(), LogType.DATA_DEEP_CONNECTION);
				}
			}
		}
		
		Iterator<Edge> edgesIt = edgesRaw.iterator();
		while (edgesIt.hasNext())
		{
			Edge potentiallyConnectedEdge = edgesIt.next();
			if (potentiallyConnectedEdge.getSourceRaw().equals(state.getSubjectName()))
			{
				state.addEdge(potentiallyConnectedEdge);
				Log.println("    has Edge: " + potentiallyConnectedEdge.getComponentID(), LogType.DATA_DEEP_CONNECTION);
			}
		}
		return state;
	}

	///bridge 2 => data structure
	private Actor connectActor(Actor actor)
	{
		Log.println("connecting Actor: " + actor.getComponentID(), LogType.DATA_DEEP_CONNECTION);
		Iterator<String> implementedAbstractActorsRawIt = actor.getImplementedAbstractActorsRawIt();
		while (implementedAbstractActorsRawIt.hasNext())
		{
			String implementedAbstractActor = implementedAbstractActorsRawIt.next();
			
			Iterator<Actor> potentiallyImplementedAbstractActorsIt = this.actorsRaw.iterator();
			while (potentiallyImplementedAbstractActorsIt.hasNext()){
				Actor potentiallyImplementedAbstractActor = potentiallyImplementedAbstractActorsIt.next();
				if(implementedAbstractActor.equals(potentiallyImplementedAbstractActor.getSubjectName()))
				{
					actor.addImplementedAbstractActor(potentiallyImplementedAbstractActor);
					Log.println("    implements: " + potentiallyImplementedAbstractActor.getComponentID(), LogType.DATA_DEEP_CONNECTION);
				}
			}
		}
		
		Iterator<Message> outMessageIt = this.messagesRaw.iterator();
		while(outMessageIt.hasNext())
		{
			Message potentialOutMessage = outMessageIt.next();
			if(potentialOutMessage.getSenderRaw().equals(actor.getSubjectName())) 
			{
				actor.addOutMessage(potentialOutMessage);
				Log.println("    has Message: " + potentialOutMessage.getComponentId(), LogType.DATA_DEEP_CONNECTION);
			}
		}
		
		Iterator<String> internStatesRawIt = actor.getInternStatesRawIt();
		while (internStatesRawIt.hasNext()) 
		{
			String internStateRaw = internStatesRawIt.next();
			Iterator<State> potentiallyContainedStatesIt = this.statesRaw.iterator();
			while (potentiallyContainedStatesIt.hasNext()) 
			{
				State potentiallyContainedState = potentiallyContainedStatesIt.next();
				if (potentiallyContainedState.getSubjectName().equals(internStateRaw))
				{
					actor.addInternState(potentiallyContainedState);
					Log.println("    has intern State: " + potentiallyContainedState.getComponentID(), LogType.DATA_DEEP_CONNECTION);
					if (potentiallyContainedState.getIsInitial())
					{
						actor.setInitialState(potentiallyContainedState);
						Log.println("    has initial State: " + potentiallyContainedState.getComponentID(), LogType.DATA_DEEP_CONNECTION);
					}
				}
			}
		}
		
		//FIXME ATTENTION the following is a Hack to find (at least approximate) some internStates of those Actors who inherit from an abstract actor
		Iterator<String> inheritedStatesRawIt = actor.getInheritedStatesRawIt();
		while (inheritedStatesRawIt.hasNext()) 
		{
			String inheritedStateRaw = inheritedStatesRawIt.next();
			Iterator<State> potentiallyInheritedStatesIt = this.statesRaw.iterator();
			while (potentiallyInheritedStatesIt.hasNext()) 
			{
				State potentiallyInheritedState = potentiallyInheritedStatesIt.next();
				if (potentiallyInheritedState.getSubjectName().equals(inheritedStateRaw))
				{
					Iterator<State> allStatesIt = this.statesRaw.iterator();
					while (allStatesIt.hasNext()) 
					{
						State state = allStatesIt.next();
						Iterator<String> rawReferenceToAbstractIt = state.getImplementedAbstractStatesRawIt();
						while (rawReferenceToAbstractIt.hasNext()) 
						{
							String rawReferenceToAbstract = rawReferenceToAbstractIt.next();
							if (potentiallyInheritedState.getSubjectName().equals(rawReferenceToAbstract))
							{
								actor.addInternState(state);
								Log.println("    has intern State (Hack!): " + state.getComponentID(), LogType.DATA_DEEP_CONNECTION);
								if (state.getIsInitial())
								{
									actor.setInitialState(state);
									Log.println("    has initial State (Hack!): " + state.getComponentID(), LogType.DATA_DEEP_CONNECTION);
								}
							}
						}
					}
				}
			}
		}
		return actor;
	}

	///bridge 2 => data structure
	private Edge connectEdge(Edge edge)
	{
		Log.println("connecting Edge: " + edge.getComponentID(), LogType.DATA_DEEP_CONNECTION);
		Iterator<String> messagesRawIt = edge.getMessagesRawIt();
		while (messagesRawIt.hasNext())
		{
			String messageRaw = messagesRawIt.next();
			
			Iterator<Message> messagesIt = this.messagesRaw.iterator();
			while(messagesIt.hasNext())
			{
				Message potentialMessage = messagesIt.next();
				if(potentialMessage.getSubjectName().equals(messageRaw))
				{
					edge.addMessage(potentialMessage);
					Log.println("    has Message: " + potentialMessage.getComponentId(), LogType.DATA_DEEP_CONNECTION);
				}
			}
		}
		
		Iterator<State> statesRawIt = this.statesRaw.iterator();
		while(statesRawIt.hasNext())
		{
			State potentialState = statesRawIt.next();
			
			if (edge.getTargetRaw() != null)
			{
				if(edge.getTargetRaw().equals(potentialState.getSubjectName()))
				{
					edge.setTarget(potentialState);
					Log.println("    has Target: " + potentialState.getComponentID(), LogType.DATA_DEEP_CONNECTION);
				}
			}
			
			if (edge.getSourceRaw() != null)
			{
				if(edge.getSourceRaw().equals(potentialState.getSubjectName()))
				{
					edge.setSource(potentialState);
					Log.println("    has Source: " + potentialState.getComponentID(), LogType.DATA_DEEP_CONNECTION);
				}
			}
		}
		return edge;
	}

	///bridge 2 => data structure
	private Message connectMessage(Message message)
	{
		Log.println("connecting Message: " + message.getComponentId(), LogType.DATA_DEEP_CONNECTION);
		Iterator<String> implementedMessagesRawIt = message.getImplementedMessagesRawIt();
		while(implementedMessagesRawIt.hasNext())
		{
			String implementedMessageRaw = implementedMessagesRawIt.next();
			
			Iterator<Message> messageIt = this.messagesRaw.iterator();
			while (messageIt.hasNext()) {
				Message potentialImplementedMessage = messageIt.next();
				
				if(implementedMessageRaw.equals(potentialImplementedMessage.getSubjectName()))
				{
					message.addImplementedMessages(potentialImplementedMessage);
					Log.println("    implements: " + potentialImplementedMessage.getComponentId(), LogType.DATA_DEEP_CONNECTION);
				}
			}
		}
		
		Iterator<Actor> actorsRawIt = this.actorsRaw.iterator();
		while(actorsRawIt.hasNext())
		{
			Actor potentialActor = actorsRawIt.next();
			
			if (message.getReceiverRaw() != null)
			{
				if(message.getReceiverRaw().equals(potentialActor.getSubjectName()))
				{
					message.setReceiver(potentialActor);
					Log.println("    has Receiver: " + potentialActor.getComponentID(), LogType.DATA_DEEP_CONNECTION);
				}
			}
			
			if (message.getSenderRaw() != null)
			{
				if(message.getSenderRaw().equals(potentialActor.getSubjectName()))
				{
					message.setSender(potentialActor);
					Log.println("    has Sender: " + potentialActor.getComponentID(), LogType.DATA_DEEP_CONNECTION);
				}
			}
			
		}
		return message;
	}	
}






































