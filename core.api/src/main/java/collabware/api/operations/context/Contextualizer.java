package collabware.api.operations.context;

import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.PrimitiveOperation;

public interface Contextualizer {
	
	public static Contextualizer INSTANCE = new ContextualizerImpl();

	ContextualizedComplexOperation createComplexOperation(Context context, ComplexOperation transformed, ContextualizedComplexOperation original);
	ContextualizedComplexOperation createComplexOperation(Context context, String description, List<PrimitiveOperation> firstPrimitivesTransformed);
	ContextualizedComplexOperation createComplexOperation(Context context, ComplexOperation op);
}
