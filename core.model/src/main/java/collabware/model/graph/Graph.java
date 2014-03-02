package collabware.model.graph;

import java.util.Collection;


public interface Graph {

	boolean contains(Node n);

	public Node getNode(String string);
	
	public abstract Collection<? extends Node> getNodes();
	
	public abstract Node getRootNode();
}
