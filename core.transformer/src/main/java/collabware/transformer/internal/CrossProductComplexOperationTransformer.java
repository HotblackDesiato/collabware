package collabware.transformer.internal;

import java.util.ArrayList;
import java.util.List;

import collabware.api.document.PrimitiveOperationTransformer;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.Contextualizer;
import collabware.api.transform.CollisionException;

/**
 * Transforms two {@link ContextualizedComplexOperation}s against each other by transforming 
 * all constituent {@link PrimitiveOperation} of one complex operation against all {@link PrimitiveOperation}s of
 * the other complex operation; hence the name.
 */
public class CrossProductComplexOperationTransformer implements ComplexOperationTransformer {

	private PrimitiveOperationTransformer primitiveTransformer;

	private Contextualizer contextualizer = Contextualizer.INSTANCE;
	
	public CrossProductComplexOperationTransformer(PrimitiveOperationTransformer primitiveTransformer) {
		this.primitiveTransformer = primitiveTransformer;
	}

	private static class PartialResult {

		private PrimitiveOperation primitive;
		private List<PrimitiveOperation> complex;

		public PartialResult(PrimitiveOperation primitive, List<PrimitiveOperation> complex) {
			this.primitive = primitive;
			this.complex = complex;
		}

		public PrimitiveOperation getPrimitiveTransformed() {
			return primitive;
		}

		public List<PrimitiveOperation> getComplexTransformed() {
			return complex;
		}
	}
	
	@Override
	public TransformationResult transform(ContextualizedComplexOperation first, ContextualizedComplexOperation second) throws CollisionException {
		List<PrimitiveOperation> secondPrimitivesTransformed = second.getPrimitiveOperations();
		List<PrimitiveOperation> firstPrimitivesTransformed = new ArrayList<PrimitiveOperation>(first.size());
		
		for (PrimitiveOperation o: first.getDecoratedOperation()) {
			PartialResult partialResult = transform(o, secondPrimitivesTransformed);
			secondPrimitivesTransformed = partialResult.getComplexTransformed();
			firstPrimitivesTransformed.add(partialResult.getPrimitiveTransformed());
		}
		
		ContextualizedComplexOperation firstTransformed = contextualizer.createComplexOperation(first.getContext().including(second), first.getDescription(), firstPrimitivesTransformed);
		ContextualizedComplexOperation secondTransformed = contextualizer.createComplexOperation(second.getContext().including(first), second.getDescription(), secondPrimitivesTransformed);
		
		return new TransformationResult(firstTransformed, secondTransformed);
	}

	private PartialResult transform(PrimitiveOperation firstPrimitive, List<PrimitiveOperation> secondPrimitives) throws CollisionException {
		List<PrimitiveOperation> secondPrimitivesTransformed = new ArrayList<PrimitiveOperation>(secondPrimitives.size());
		for (PrimitiveOperation o: secondPrimitives) {
			PrimitiveOperation oTransformed = this.primitiveTransformer.transform(o, firstPrimitive);
			secondPrimitivesTransformed.add(oTransformed);
			firstPrimitive = this.primitiveTransformer.transform(firstPrimitive, o);
		}
		return new PartialResult(firstPrimitive, secondPrimitivesTransformed);
	}

}
