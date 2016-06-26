package structure;

import java.util.ArrayList;
import java.util.Iterator;


//TODO delete and handle outEdges inside the "State"
public class Element {
	//attributes
	private ArrayList<Edge> outEdges;
	
	
	//constructors
	public Element()
	{
		this.outEdges = new ArrayList<Edge>();
	}
	
	
	//functions
	public Iterator<Edge> getOutEdgeIt() 
	{
		return this.outEdges.iterator();
	}
		
	public void addOutEdge(Edge outEdge)
	{
		outEdges.add(outEdge);
	}
}
