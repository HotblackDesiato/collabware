package collabware.transformer;

import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.DocumentState;
import collabware.api.transform.CollisionException;

/**
 * Transforms {@link ContextualizedComplexOperation} to make it applicable to a given {@link DocumentState}
 *
 */
public interface Transformer {
	/**
	 * Transforms {@code op} such that it can be applied to a {@link DocumentState} {@code state}.
	 * After transformation the following holds true:
	 * <pre>{@code
	 * transform(op, state).getContext().equals(state) // true
	 * }</pre>
	 * 
	 * 
	 * @param op
	 * @param state
	 * @return the transformed version of {@code op}.
	 * @throws CollisionException if {@code op} collides with any operation in {@code state}.
	 */
	ContextualizedComplexOperation transform(ContextualizedComplexOperation op, DocumentState state) throws CollisionException;

}
