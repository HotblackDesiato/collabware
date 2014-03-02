package collabware.transformer;

import collabware.api.operations.context.ContextualizedComplexOperation;

/**
 * Decides which of two {@link ContextualizedComplexOperation}s has precedence over the other in case of a conflict.
 */
public interface PrecedenceRule {

	/**
	 * Decides which of two {@link ContextualizedComplexOperation}s has precedence over the other in case of a conflict.
	 * If {@code hasPrecedence(op1, op2)} returns {@code true}, then  {@code hasPrecedence(op2, op1)} must return {@code false}
	 * for any {@code op1}, {@code op2}.
	 * 
	 * @param first
	 * @param second
	 * @return {@code true} if and only if {@code first} has precedence over {@code second}.
	 */
	boolean hasPrecedence(ContextualizedComplexOperation first,	ContextualizedComplexOperation second);

}
