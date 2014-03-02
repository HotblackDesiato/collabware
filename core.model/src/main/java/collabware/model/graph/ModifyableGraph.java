package collabware.model.graph;

import java.util.Collection;


public interface ModifyableGraph extends Graph {

	ModifyableNode addNode(String string) throws GraphException;

	void detach(ModifyableNode n) throws GraphException;

	void remove(ModifyableNode n);

	ModifyableNode getNode(String string);

	Collection<ModifyableNode> getNodes();

	ModifyableNode getRootNode();
	
	void clear();

}