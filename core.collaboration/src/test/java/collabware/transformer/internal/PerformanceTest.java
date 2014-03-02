package collabware.transformer.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import collabware.api.document.PrimitiveOperationTransformer;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.api.transform.CollisionException;
import collabware.collaboration.internal.context.ModelStateImpl;
import collabware.collaboration.internal.context.VectorBasedContext;
import collabware.transformer.internal.cache.Cache;
import collabware.transformer.internal.cache.UnboundedCache;
import collabware.transformer.internal.utils.IdentityTransformationMatrix;

public class PerformanceTest {
	private Cache theCache = new UnboundedCache();
	private PrimitiveOperationTransformer matrix = new IdentityTransformationMatrix();
	
	private ContextBasedOperationTransformer transformer = new ContextBasedOperationTransformer(new CrossProductComplexOperationTransformer(matrix));
	private ModelStateImpl modelState;
	private long startTimeInMillis;
	
	
	@Before
	public void setup() {
		modelState = new ModelStateImpl();
		transformer.setCache(theCache);
		startTimeInMillis = System.currentTimeMillis();
	}
	
	@After
	public void printResult() {
		long endTimeInMillis = System.currentTimeMillis();
		System.out.print(theCache.toString());
		long timeElapsedInMillis = endTimeInMillis - startTimeInMillis;
		System.out.println("Time: " + timeElapsedInMillis + " ms");
	}
	
	@Test
	public void test0() throws Exception {
		int numberOfPrimitiveOperations = 10;
		transformAndApply(aComplexOperation(ctx(0, v()), "1", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(1, v()), "2", numberOfPrimitiveOperations));
	}
	
	@Test
	public void test1() throws Exception {
		int numberOfPrimitiveOperations = 20;
		transformAndApply(aComplexOperation(ctx(0, v()), "1", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(1, v()), "2", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(2, v()), "3", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(1, v(0,1)), "4", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(0, v(1)), "5", numberOfPrimitiveOperations));
	}

	private int[] v(int ...is){
		return is;
	}
	
	@Test
	public void test2() throws Exception {
		int numberOfPrimitiveOperations = 100;		
		transformAndApply(aComplexOperation(ctx(0, v()), "1", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(1, v()), "2", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(2, v()), "3", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(1, v(0,1)), "4", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(0, v(1)), "5", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(2, v(1,0,1)), "6", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(2, v(1,0,2)), "7", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(1, v(1,1,0)), "8", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(0, v(2,0,1)), "9", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(3, v()), "10", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(3, v(0,0,0,1)), "11", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(3, v(0,0,0,2)), "12", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(3, v(0,0,0,3)), "13", numberOfPrimitiveOperations));
	}

	@Test
	public void test3() throws Exception {
		int numberOfPrimitiveOperations = 30;		
		transformAndApply(aComplexOperation(ctx(0, v()), "1", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(1, v()), "2", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(2, v()), "3", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(3, v()), "4", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(4, v()), "5", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(5, v()), "6", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(6, v()), "7", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(7, v()), "8", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(8, v()), "9", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(9, v()), "10", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(10, v()), "11", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(11, v()), "12", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(12, v()), "13", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(13, v()), "14", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(14, v()), "15", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(15, v()), "16", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(16, v()), "17", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(17, v()), "18", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(18, v()), "19", numberOfPrimitiveOperations));
		transformAndApply(aComplexOperation(ctx(19, v()), "20", numberOfPrimitiveOperations));
	}

	private void transformAndApply(ContextualizedComplexOperation complex) throws CollisionException{
		transformer.transform(complex, modelState);
		complex.addTo(modelState);
	}
	
	private ContextualizedComplexOperation aComplexOperation(Context ctx, String description, int numberOfPrimitiveOperations) {
		List <PrimitiveOperation> ops = new ArrayList<PrimitiveOperation>(numberOfPrimitiveOperations);
		for (int j = 0; j < numberOfPrimitiveOperations; j++) {
			ops.add(aPrimitiveOperation());
		}
		
		ComplexOperation complexOperation = new ComplexOperationImpl(description, ops);
		return new ContextualizedComplexOperationImpl(ctx, complexOperation );
	}

	private PrimitiveOperation aPrimitiveOperation() {
		PrimitiveOperation op = createMock(PrimitiveOperation.class);
		replay(op);
		return op;
	}

	private Context ctx(int i, int[] js) {
		return new VectorBasedContext(i, js);
	}
	
}
