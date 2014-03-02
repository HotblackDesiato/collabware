package collabware.model.graph;

import java.util.Collection;
import java.util.Set;

public interface UnaryReferences extends Iterable<String>{
	Node get(String ref);
	
	Collection<?extends Node> sourcesOf(String ref);

	Set<String> getAll();

	Set<String> getReverseUnaryReferences();
}
