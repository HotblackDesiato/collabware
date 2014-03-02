package collabware.transformer.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collabware.api.document.PrimitiveOperationTransformer;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.Contextualizer;
import collabware.api.transform.CollisionException;
import collabware.transformer.PrecedenceRule;

/**
 * Provides an exclusive transformation for {@link ContextualizedComplexOperation}.
 * In case of a conflict a {@link PrecedenceRule} is used to decide which operation has
 * precedence. The transformed version of the operation that was given precedence excludes the
 * effects of the other operation by prepending its inverse. The transformed version of the operation
 * that was not given precedence is simply transformed into {@link NoOperation}. 
 *
 */
public class ExclusiveComplexTransformer extends CrossProductComplexOperationTransformer {

	private Contextualizer contextualizer = Contextualizer.INSTANCE;
	private PrecedenceRule precedenceRule;
	
	public ExclusiveComplexTransformer(PrimitiveOperationTransformer primitiveTransformer, PrecedenceRule precedenceRule) {
		super(primitiveTransformer);
		this.precedenceRule = precedenceRule;
	}

	@Override
	public TransformationResult transform(ContextualizedComplexOperation first, ContextualizedComplexOperation second) throws CollisionException {
		try {
			return super.transform(first, second);
		} catch (CollisionException e) {
			return resolveConflict(first, second);
		}
	}

	private TransformationResult resolveConflict(ContextualizedComplexOperation first, ContextualizedComplexOperation second) {
		if (precedenceRule.hasPrecedence(first, second)) {
			return new TransformationResult(exclude(second, first), ignore(second, first));
		} else {
			return new TransformationResult(ignore(first, second), exclude(first, second));
		}
	}


	private ContextualizedComplexOperation ignore(ContextualizedComplexOperation exclude, ContextualizedComplexOperation from) {
		return contextualizer.createComplexOperation(exclude.getContext().including(from), exclude.getDescription(), Arrays.asList((PrimitiveOperation)NoOperation.NOP));
	}

	private ContextualizedComplexOperation exclude(ContextualizedComplexOperation exclude, ContextualizedComplexOperation from) {
		List<PrimitiveOperation> firstPrimitivesTransformed = new ArrayList<PrimitiveOperation>(exclude.getDecoratedOperation().inverse().getPrimitiveOperations());
		firstPrimitivesTransformed.addAll(from.getDecoratedOperation().getPrimitiveOperations());
		return contextualizer.createComplexOperation(from.getContext().including(exclude), from.getDescription(), firstPrimitivesTransformed );
	}

}
