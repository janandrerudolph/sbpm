package init;

import enumeration.Log;
import enumeration.LogType;

//TODO view org.apache.jena.graph.Triple
/**
 * @author Jan Rudolph / Fabian Falck
 * represents an RDF-Triple of Subject-Predicate-Object with Strings (probably humanreadable)
 *
 */
public class Triple 
{
	//attributes
	private String subject;
	private String predicate;
	private String object;
	private boolean isResource;
	
	
	//constructors
	/**
	 * @param object, predicate, subject contain only the information of the Name
	 * @param isResource true if the object is a resource not a literal
	 */
	public Triple(String subject, String predicate, String object, boolean isResource)
	{
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.isResource = isResource;
		
		Log.println("S||" + subject, LogType.DATA_TRIPLE);
		Log.println("P||" + predicate, LogType.DATA_TRIPLE);
		Log.println("O||" + object, LogType.DATA_TRIPLE);
		Log.println("", LogType.DATA_TRIPLE);
	}
	
	
	//functions
	
	/**getter subject
	 * @return subject of the triple
	 */
	public String getSubject()
	{
		return this.subject;
	}
	
	/**getter predicate
	 * @return predicate of the triple
	 */
	public String getPredicate()
	{
		return this.predicate;
	}
	
	/**getter object
	 * @return object of the triple
	 */
	public String getObject()
	{
		return this.object;
	}
	
	/**
	 * @return true if the object of this triple is a resource
	 */
	public boolean getIsResource()
	{
		return this.isResource;
	}
}
