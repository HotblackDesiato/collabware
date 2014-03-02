package collabware.model.graph;

import java.util.Collection;

public interface ModifyableAttributes extends Attributes {

	public abstract void set(String attr, String value);

	public abstract Object get(String attr);

	public abstract void set(String attr, Number n);

	public abstract Collection<String> getAll();

	public abstract void set(String attr, Boolean b);

}