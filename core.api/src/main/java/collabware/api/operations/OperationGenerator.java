package collabware.api.operations;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;

import collabware.api.document.change.ChangeListener;

/**
 * The OperationGenerator consumes {@link ChangeEvent}s and turns them into their corresponding {@link PrimitiveOperation}s.
 *
 */
public abstract class OperationGenerator implements ChangeListener {
	
	private List<PrimitiveOperation> generatedOperations = new ArrayList<PrimitiveOperation>();
	
	/**
	 * Adds an operation to the list of generated operations.
	 * @param op
	 */
	protected final void addGeneratedOperation(PrimitiveOperation op) {
		generatedOperations.add(op);
	}
	
	/**
	 * 
	 * @return The list of all generated PrimitiveOperations. 
	 */
	public final List<PrimitiveOperation> getGeneratedOperations() {
		return new ArrayList<PrimitiveOperation>(generatedOperations);
	}

	/**
	 * Empties the list of generated operations.
	 */
	public final void reset() {
		generatedOperations.clear();
	}

}