package collabware.transformer.internal;

import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.transform.CollisionException;

/**
 * Transforms two {@link ContextualizedComplexOperation}s against each other.
 * 
 */
public interface ComplexOperationTransformer {

	/**
	 * Transforms two {@link ContextualizedComplexOperation}s against each other
	 * such that applying {@code first} and then {@code secondTransformed} is
	 * equivalent to applying {@code second} and then {@code firstTransformed}.
	 * 
	 * @param first
	 * @param second
	 * @return the transformed version of {@code first} and {@code second}.
	 * @throws CollisionException
	 *             if {@code first} cannot be transformed against {@code second}.
	 */
	TransformationResult transform(ContextualizedComplexOperation first, ContextualizedComplexOperation second) throws CollisionException;
}
