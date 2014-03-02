package collabware.api.operations;

import java.util.HashMap;
import java.util.Map;

import collabware.api.document.ModifyableDocument;

/**
 * This operation does nothing. 
 *
 */
public class NoOperation implements PrimitiveOperation {

	/**
	 * The one instance of the NoOperation.
	 */
	public static final NoOperation NOP = new NoOperation();
	
	NoOperation() {
		super();
	}

	@Override
	public void apply(ModifyableDocument graph) throws OperationApplicationException {
		// the NoOperation does exactly nothing :)
	}

	@Override
	public NoOperation inverse() {
		return NOP;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NoOperation)
			return true;
		else 
			return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "id()";
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>(){{
			put("t", "no");
		}};
		return map;
	}
	
}
