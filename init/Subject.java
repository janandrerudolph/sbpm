package init;

import java.util.ArrayList;
import java.util.Iterator;

import enumeration.Log;
import enumeration.LogType;


public class Subject
{
//attributes

	private ArrayList<Triple> triples;
	
	
//constructors
	/**NOT YET IMPLEMENTED: this constructor creates an empty Unit if the @param triples is not a valid unit (i.e. two different subjects)
	 * @param triples that should belong to this Unit
	 */
	public Subject (ArrayList<Triple> triples)
	{
		this.triples = triples;
		//TODO set triples = null if there are two different subjects
		
		//print on terminal, what we just created ;)
		Log.println("", LogType.DATA_SUBJECT);
		Log.println("", LogType.DATA_SUBJECT);
		Log.println("", LogType.DATA_SUBJECT);
		Log.println(this.getSubjectName(), LogType.DATA_SUBJECT);
		Log.print("________", LogType.DATA_SUBJECT);
		Iterator<String> it1 = this.getpredicates();
		while (it1.hasNext()) {
			String string = (String) it1.next();
			Log.println("", LogType.DATA_SUBJECT);
			Log.print("        | " + string + ":", LogType.DATA_SUBJECT);
			Iterator<String> it2 = this.getObjectsDependingOnPredicate(string).iterator();
			while (it2.hasNext()) {
				String string2 = (String) it2.next();
				Log.print(" <" + string2 + ">", LogType.DATA_SUBJECT);
			}
		}
	}
	
	
//functions
	//TODO does not getPredicates + getObjects..() contain the same amount of relevant information (minus irrelevant)?
	public Iterator<Triple> getTriplesIt()
	{
		return this.triples.iterator();
	}
	
	public Iterator<String> getpredicates()
	{
		ArrayList<String> predicates = new ArrayList<String>();
		Iterator<Triple> triples = this.getTriplesIt();
		while (triples.hasNext()) 
		{
			Triple triple = (Triple) triples.next();
			if (!predicates.contains(triple.getPredicate())) 
			{
				predicates.add(triple.getPredicate());
			}
		}
		return predicates.iterator();
	}
	
	public ArrayList<String> getObjectsDependingOnPredicate(String predicate)
	{
		ArrayList<String> objects = new ArrayList<String>();
		Iterator<Triple> triples = this.getTriplesIt();
		while (triples.hasNext()) 
		{
			Triple triple = (Triple) triples.next();
			if (triple.getPredicate().equals(predicate))
			{
				objects.add(triple.getObject());
			}
		}
		return objects;
	}
	
	/**
	 * @return the Subject (of all Triples) of this Unit
	 */
	public String getSubjectName()
	{
		if (this.triples.size() > 0)
		{
			return this.triples.get(0).getSubject();
		}
		else
		{
			return "";
		}
	}
}
