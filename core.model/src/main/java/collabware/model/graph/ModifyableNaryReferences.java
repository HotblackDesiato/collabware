package collabware.model.graph;

import java.util.List;
import java.util.Set;

public interface ModifyableNaryReferences extends NaryReferences {

	void add(String string, int position, ModifyableNode target);

	List<ModifyableNode> get(String string);

	void remove(String refName, int position);

	Set<ModifyableNode> getSourcesOf(String string);

}
