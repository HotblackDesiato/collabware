package collabware.model.graph;

import java.util.Collection;

public interface Attributes extends Iterable<String> {

	public abstract Object get(String attr);

	public abstract Collection<String> getAll();

}