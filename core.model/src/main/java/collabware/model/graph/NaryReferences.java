package collabware.model.graph;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface NaryReferences extends Iterable<String>{
	
	Collection<String> getAll();

	List<? extends Node> get(String string);
	
	Set<String> getReverseReferences();
	
	Set<? extends Node> getSourcesOf(String string);

}
