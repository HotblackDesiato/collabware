package collabware.collaboration.operations;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.OperationApplicationException;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.collaboration.internal.context.VectorBasedContext;


public class ContextualizedComplexOperationTest {
	@Test
	public void getContextReturnsTheContextThatWassPassedIntoConstructor() throws Exception {
		Context context = someContext();
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(context, someComplexOperation());
		
		assertSame(context, complex.getContext());
	}

	private ComplexOperation someComplexOperation() {
		return new ComplexOperationImpl("The Description", createNPrimitiveOperations(3));
	}
	
	@Test
	public void getSelfincludingContextReturnsNextContext() throws Exception {
		Context context = new VectorBasedContext(0, new int[] {1,2,3,4});
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(context, someComplexOperation());
		
		assertEquals(context.next(), complex.getSelfIncludingContext());
	}
	
	@Test
	public void getDecoratedOperationReturnsComplexOperation() throws Exception {
		ComplexOperation decoratedOperation = someComplexOperation();
		
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(someContext(), decoratedOperation);
		
		assertSame(decoratedOperation, complex.getDecoratedOperation());	
	}

	private VectorBasedContext someContext() {
		return new VectorBasedContext(2, new int[] {1,2,3,4});
	}
	
	@Test
	public void getDescriptionReturnsDescription() throws Exception {
		ComplexOperation decoratedOperation = new ComplexOperationImpl("My Complex Operation", createNPrimitiveOperations(5));
		
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(someContext(), decoratedOperation);
		
		assertSame("My Complex Operation", complex.getDescription());	
	}
		
	@Test(expected=UnsupportedOperationException.class)
	public void getPrimitiveOperationsIsUnmodifyable() throws Exception {
		Context context = someContext();
		ArrayList<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(context, new ComplexOperationImpl("The Description", primitives));
	
		complex.getPrimitiveOperations().remove(0);
	}
	
	@Test
	public void iteratorIteratesOverAllContextualizedPrimitiveOperations() throws Exception {
		Context context = someContext();
		ArrayList<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(context, new ComplexOperationImpl("The Description", primitives));
	
		List<PrimitiveOperation> actualPrimitives = new ArrayList<PrimitiveOperation>();
		for (PrimitiveOperation o: complex) {
			actualPrimitives.add(o);
		}
		
		assertEquals(complex.getPrimitiveOperations(), actualPrimitives);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void iteratorIsUnmodifyable() throws Exception {
		Context context = someContext();
		ArrayList<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(context, new ComplexOperationImpl("The Description", primitives));

		complex.iterator().remove();
	}
	
	@Test
	public void applyCallsThroughToDecoratedComplexOperation() throws Exception {
		ComplexOperation decoratedOperation = mockComplexOperationExpectingApply();
		
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(someContext(), decoratedOperation );
		
		complex.apply(createMock(ModifyableDocument.class));
		
		verify(decoratedOperation);
	}

	private ComplexOperation mockComplexOperationExpectingApply() throws OperationApplicationException {
		ArrayList<PrimitiveOperation> primitives = createNPrimitiveOperations(0);
		ComplexOperation decoratedOperation = createMock(ComplexOperation.class);
		expect(decoratedOperation.iterator()).andStubReturn(primitives.iterator());
		decoratedOperation.apply(anyObject(ModifyableDocument.class));
		expectLastCall().once();
		replay(decoratedOperation);
		return decoratedOperation;
	}
	
	@Test
	public void getOriginalReturnsThisIfNoOriginalIsPassed() throws Exception {
		Context context = someContext();
		ArrayList<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(context, new ComplexOperationImpl("The Description", primitives));

		assertSame(complex, complex.getOriginal());
	}
	
	@Test
	public void getOriginalReturnsOriginalIfPassed() throws Exception {
		Context context = someContext();
		ArrayList<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ContextualizedComplexOperation original = new ContextualizedComplexOperationImpl(context, new ComplexOperationImpl("The Description", primitives));
		ContextualizedComplexOperation complex = new ContextualizedComplexOperationImpl(context, new ComplexOperationImpl("The Description", primitives), original);
		
		assertSame(original, complex.getOriginal());
	}
	
	private ArrayList<PrimitiveOperation> createNPrimitiveOperations(int numberOfOperations) {
		ArrayList<PrimitiveOperation> primitives = new ArrayList<PrimitiveOperation>();
		for (int i = 0; i < numberOfOperations; i++) {
			PrimitiveOperation op = createMock(PrimitiveOperation.class);
			replay(op);
			primitives.add(op);
		}
		return primitives;
	}
	
	
}
