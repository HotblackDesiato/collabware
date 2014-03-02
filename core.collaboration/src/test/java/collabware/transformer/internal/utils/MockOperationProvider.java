package collabware.transformer.internal.utils;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.collaboration.internal.context.VectorBasedContext;

public class MockOperationProvider {
	
	private MockOperationProvider() {}
	
	public static ContextualizedOperation anOperationWithContext(VectorBasedContext ctx) {
		return aComplexOperation(ctx,1);
	}

	public static VectorBasedContext ctx(int clientNumber, int[] sequenceNumbers) {
		return new VectorBasedContext(clientNumber, sequenceNumbers);
	}

	public static PrimitiveOperation aPrimitiveOperation() {
		PrimitiveOperation op = createMock(PrimitiveOperation.class);
		replay(op);
		return op;
	}
	
	public static ContextualizedComplexOperation aComplexOperation(Context ctx, int numberOfPrimitiveOperations) {
		List <PrimitiveOperation> ops = new ArrayList<PrimitiveOperation>(numberOfPrimitiveOperations);
		for (int j = 0; j < numberOfPrimitiveOperations; j++) {
			ops.add(aPrimitiveOperation());
		}
		
		ComplexOperation complexOperation = new ComplexOperationImpl("", ops);
		return new ContextualizedComplexOperationImpl(ctx, complexOperation );
	}
}
