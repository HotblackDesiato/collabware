package collabware.collaboration.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.collaboration.internal.context.VectorBasedContext;

public class ClientStateToContextMappingTest {
	
	@Test
	public void contextInServerStateZeroReturnsInitialContext() throws Exception {
		VectorBasedContext initialContext = new VectorBasedContext(0, new int[]{4,1,5,3});
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(initialContext);
		
		Context context = mapping.getContextForServerState(0);
		
		assertEquals(initialContext, context);
	}
	
	@Test
	public void contextInServerStateZeroReturnsInitialContext2() throws Exception {
		VectorBasedContext initialContext = new VectorBasedContext(2, new int[]{7,2,6,1});
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(initialContext);
		
		Context context = mapping.getContextForServerState(0);
		
		assertEquals(initialContext, context);
	}

	@Test
	public void addingComplexClientOperationsIncreasesTheClientsSequenceNumber() throws Exception {
		VectorBasedContext initialContext = new VectorBasedContext(1, new int[]{7,2,6,1});
		
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(initialContext);
		
		ContextualizedOperation clientOps = someComplexOperationWithContextAndLength(ctx(1, v(7,2,6,1)), 4);
		mapping.addClientOperation(clientOps, 0);
		
		Context context = mapping.getContextForServerState(0);
		assertEquals(new VectorBasedContext(1, new int[]{7,3,6,1}), context);
	}

	@Test(expected=IllegalArgumentException.class)
	public void canOnlyAddClientOperationsWithTheSameClientNumber() throws Exception {
		VectorBasedContext initialContext = new VectorBasedContext(1, new int[]{});
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(initialContext);
		ContextualizedOperation clientOps = someComplexOperationWithContextAndLength(ctx(8, v(0)), 4);

		mapping.addClientOperation(clientOps, 0);
	}
	
	private ContextualizedOperation someComplexOperationWithContextAndLength(Context ctx, int numberOfPrimitiveOps) {
		PrimitiveOperation[] ops = new PrimitiveOperation[numberOfPrimitiveOps];
		for (int i = 0; i < numberOfPrimitiveOps; i++) {
			PrimitiveOperation o = createMock(PrimitiveOperation.class);
			replay(o);
			ops[i] = o;
		}
		ComplexOperation complexOperation = new ComplexOperationImpl("", Arrays.asList(ops));
		return new ContextualizedComplexOperationImpl(ctx, complexOperation );
	}

	@Test
	public void addingComplexClientOperationsIncreasesTheClientsSequenceNumber_2() throws Exception {
		VectorBasedContext ctx = new VectorBasedContext(3, new int[]{2,4,1,3});
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(ctx);
		
		ContextualizedOperation clientOp1 = someComplexOperationWithContextAndLength(ctx, 4);
		mapping.addClientOperation(clientOp1, 0);

		ContextualizedOperation clientOp2 = someComplexOperationWithContextAndLength(clientOp1.getSelfIncludingContext(), 3);
		mapping.addClientOperation(clientOp2, 0);
		
		Context context = mapping.getContextForServerState(0);
		assertEquals(new VectorBasedContext(3, new int[]{2,4,1,5}), context);
	}
	
	@Test
	public void addingNewServerState() throws Exception {
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(new VectorBasedContext(3, new int[]{2,4,1,3}));
		
		mapping.addNewServerState(new VectorBasedContext(0, new int[]{6,4,1,3}));

		Context context = mapping.getContextForServerState(1);
		assertEquals(new VectorBasedContext(3, new int[]{6,4,1,3}), context);
	}
	
	@Test
	public void addingNewServerStateDoesNotAlterPreviousServerStates() throws Exception {
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(new VectorBasedContext(2, new int[]{2,4,1,3}));
		
		mapping.addNewServerState(new VectorBasedContext(0, new int[]{6,4,1,3}));
		
		Context context = mapping.getContextForServerState(0);
		assertEquals(new VectorBasedContext(2, new int[]{2,4,1,3}), context);
	}

	@Test
	public void addingNewClientOperationsAltersServerStates() throws Exception {
		VectorBasedContext ctx = new VectorBasedContext(2, new int[]{2,4,1,3});
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(ctx);
		
		mapping.addNewServerState(new VectorBasedContext(2, new int[]{6,4,1,3}));
		ContextualizedOperation clientOp1 = someComplexOperationWithContextAndLength(ctx, 4);
		mapping.addClientOperation(clientOp1, 0);
		
		Context context = mapping.getContextForServerState(1);
		assertEquals(new VectorBasedContext(2, new int[]{6,4,2,3}), context);
	}
	
	@Test
	public void addingNewClientOperationsAltersSubsequentServerStates() throws Exception {
		VectorBasedContext ctx = new VectorBasedContext(2, new int[]{2,4,1,3});
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(ctx);
		
		mapping.addNewServerState(new VectorBasedContext(0, new int[]{3,4,1,3}));
		mapping.addNewServerState(new VectorBasedContext(1, new int[]{3,5,1,3}));
		mapping.addNewServerState(new VectorBasedContext(3, new int[]{3,5,1,4}));
		
		ContextualizedOperation clientOp1 = someComplexOperationWithContextAndLength(ctx, 4);
		mapping.addClientOperation(clientOp1, 2);
		
		Context context = mapping.getContextForServerState(3);
		assertEquals(new VectorBasedContext(2, new int[]{3,5,2,4}), context);
	}
	
	@Test
	public void addingNewClientOperationsDoesNotAltersPreviousServerStates() throws Exception {
		VectorBasedContext ctx = new VectorBasedContext(2, new int[]{2,4,1,3});
		ClientStateToContextMapping mapping = new ClientStateToContextMapping(ctx);
		
		mapping.addNewServerState(new VectorBasedContext(0, new int[]{3,4,1,3}));
		mapping.addNewServerState(new VectorBasedContext(1, new int[]{3,5,1,3}));
		mapping.addNewServerState(new VectorBasedContext(3, new int[]{3,5,1,4}));
		
		ContextualizedOperation clientOp1 = someComplexOperationWithContextAndLength(ctx, 4);
		mapping.addClientOperation(clientOp1, 2);
		
		Context context = mapping.getContextForServerState(1);
		assertEquals(new VectorBasedContext(2, new int[]{3,4,1,3}), context);
	}

	private Context ctx(int i, int[] v) {
		return new VectorBasedContext(i,v);
	}

	private int[] v(int ...vector) {
		return vector;
	}

}
