package collabware.collaboration.internal.context;

import static collabware.collaboration.internal.context.OperationTestUtils.createOperationWith;
import static collabware.collaboration.internal.context.OperationTestUtils.emptyContextForClient;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.api.operations.context.NonConsecutiveContextException;
import collabware.api.operations.context.OperationsSet;

public class ContextTest {
	
	@Test
	public void testEmptyContext() throws Exception {
		Context empty = emptyContextForClient(0);
		ContextualizedOperation o = createOperationWith(empty);
		
		assertFalse(empty.contains(o));		
	}
	
	@Test
	public void addToEmptyContext() throws Exception {
		Context empty = emptyContextForClient(0);
		ContextualizedOperation o = createOperationWith(empty);
		
		OperationsSet notEmpty = empty.including(o);
		
		assertTrue(notEmpty.contains(o));
	}
	
	@Test
	public void parallelOperationsAreNotContained() throws Exception {
		ContextualizedOperation o1 = createOperationWith(emptyContextForClient(0));
		ContextualizedOperation o2 = createOperationWith(emptyContextForClient(1));

		OperationsSet withO1 = o1.getContext().including(o1);
		
		assertFalse(withO1.contains(o2));
	}

	@Test
	public void isSubContextOfTrivial() throws Exception {
		Context empty0 = emptyContextForClient(0);
		Context empty1 = emptyContextForClient(1);
		
		assertTrue(empty0.isSubsetOf(empty1));
		assertTrue(empty1.isSubsetOf(empty0));
	}

	@Test
	public void transitiveSubContexts() throws Exception {
		ContextualizedOperation o1 = createOperationWith(emptyContextForClient(0));
		ContextualizedOperation o2 = createOperationWith(o1.getContext().including(o1));
		ContextualizedOperation o3 = createOperationWith(o2.getContext().including(o2));
	
		assertTrue(o1.getContext().isSubsetOf(o3.getContext()));
		assertFalse(o3.getContext().isSubsetOf(o1.getContext()));
	}
	
	@Test
	public void multiClientSubContext() throws Exception {
		ContextualizedOperation o1 = createOperationWith(emptyContextForClient(0));
		ContextualizedOperation o2 = createOperationWith(emptyContextForClient(3).including(o1));
		ContextualizedOperation o3 = createOperationWith(o2.getContext().including(o2));
	
		assertTrue(o1.getContext().isSubsetOf(o3.getContext()));
		assertFalse(o3.getContext().isSubsetOf(o1.getContext()));
	}
	

	@Test
	public void addTwoParallelOperations() throws Exception {
		ContextualizedOperation o1 = createOperationWith(emptyContextForClient(2));
		ContextualizedOperation o2 = createOperationWith(emptyContextForClient(5));

		OperationsSet superContext = o1.getContext().including(o1).including(o2);
		
		assertTrue(superContext.contains(o1));
		assertTrue(superContext.contains(o2));
	}
	
	@Test(expected=NonConsecutiveContextException.class)
	public void addNonConsecutiveOperations() throws Exception {
		ContextualizedOperation o1 = createOperationWith(emptyContextForClient(0));
		ContextualizedOperation o2 = createOperationWith(emptyContextForClient(2));
		ContextualizedOperation o3 = createOperationWith(o2.getContext().including(o2));

		o1.getContext().including(o3);
	}
	
}
