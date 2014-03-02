package collabware.model.graph;

import java.util.Collection;
import java.util.Set;


public interface ModifyableUnaryReferences extends UnaryReferences {
	
	void set(String reference, Node target) throws GraphException;

	ModifyableNode get(String reference);

	Collection<ModifyableNode> sourcesOf(String reference);

	Set<String> getAll();

	Set<String> getReverseUnaryReferences();
}
