package collabware.model.graph;


public interface ModifyableNode extends Node {

	ModifyableGraph getGraph();

	ModifyableAttributes getAttributes();
	
	ModifyableUnaryReferences getUnaryReferences();

	void detach() throws GraphException;
	
	void remove();

	ModifyableNaryReferences getNaryReferences();
}
