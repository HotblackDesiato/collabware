package collabware.model.graph;


public interface Node {

	String getId();

	Graph getGraph();

	Attributes getAttributes();
	
	UnaryReferences getUnaryReferences();
	
	NaryReferences getNaryReferences();
}
