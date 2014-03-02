package collabware.collaboration.internal.context;

import static collabware.collaboration.internal.context.OperationTestUtils.createOperationWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import collabware.api.operations.context.BackedContextDifference;
import collabware.api.operations.context.BackedOperationsSet;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;


public class BackedOperationsSetTest {
	@Test
	public void asContextForClient() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		Context emptyContext1 = backedOpSet.asContextForClient(1);
		Context emptyContext2 = backedOpSet.asContextForClient(2);
		
		assertEquals(1, emptyContext1.getClientNumber());
		assertEquals(2, emptyContext2.getClientNumber());
	}
	
	@Test
	public void addSingleOperation() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		Context emptyContext = backedOpSet.asContextForClient(0);
		ContextualizedOperation op = createOperationWith(emptyContext);
		
		backedOpSet.add(op);
		Context nextContext = backedOpSet.asContextForClient(0);
		
		assertTrue(emptyContext.isSubsetOf(nextContext));
		assertTrue(nextContext.contains(op));
	}
	
	@Test
	public void addTwoParallelOperations() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		Context emptyContext1 = backedOpSet.asContextForClient(1);
		Context emptyContext2 = backedOpSet.asContextForClient(2);
		ContextualizedOperation op1 = createOperationWith(emptyContext1);
		ContextualizedOperation op2 = createOperationWith(emptyContext2);
		
		backedOpSet.add(op1);
		backedOpSet.add(op2);
		Context nextContext = backedOpSet.asContextForClient(0);

		assertTrue(nextContext.contains(op1));	
		assertTrue(nextContext.contains(op2));	
	}
	
	@Test
	public void equalsContext() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		assertTrue(backedOpSet.equals(backedOpSet.asContextForClient(0)));
	}
	
	@Test
	public void equalsContextAfterAdding() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		Context initialContext = backedOpSet.asContextForClient(0);
		ContextualizedOperation op = createOperationWith(initialContext);

		backedOpSet.add(op);
		Context contextWithOp = op.getContext().including(op);
		
		assertFalse(backedOpSet.equals(initialContext));
		assertTrue(backedOpSet.equals(contextWithOp));
	}
	
	@Test
	public void minus() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		Context emptyContext1 = backedOpSet.asContextForClient(1);
		Context emptyContext2 = backedOpSet.asContextForClient(2);
		ContextualizedOperation op1 = createOperationWith(emptyContext1);
		ContextualizedOperation op2 = createOperationWith(emptyContext2);
		backedOpSet.add(op1);
		
		BackedContextDifference difference = backedOpSet.minus(op2.getContext());
		
		assertTrue(difference.contains(op1));
	}
	
	@Test
	public void minus2() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		Context emptyContext1 = backedOpSet.asContextForClient(1);
		Context emptyContext2 = backedOpSet.asContextForClient(2);
		ContextualizedOperation op1 = createOperationWith(emptyContext1);
		ContextualizedOperation op2 = createOperationWith(emptyContext2);
		backedOpSet.add(op1);
		backedOpSet.add(op2);
		
		BackedContextDifference difference = backedOpSet.minus(op1.getContext());
		
		assertTrue(difference.contains(op2));
	}
	
	@Test
	public void copyEmpty() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		BackedOperationsSet copy = backedOpSet.copy();
		
		assertTrue(backedOpSet.asContextForClient(2).equals(copy.asContextForClient(2)));
	}

	@Test
	public void copyPopulated() throws Exception {
		BackedOperationsSetImpl backedOpSet = new BackedOperationsSetImpl();
		Context initialContext = backedOpSet.asContextForClient(0);
		ContextualizedOperation op = createOperationWith(initialContext);
		backedOpSet.add(op);
		BackedOperationsSetImpl copiedBackedOpSet = backedOpSet.copy();
		
		assertEquals(op, backedOpSet.getOperation(0, 1));
		assertEquals(backedOpSet.getOperation(0, 1), copiedBackedOpSet.getOperation(0, 1));
		assertTrue(backedOpSet.asContextForClient(2).equals(copiedBackedOpSet.asContextForClient(2)));
	}

	@Test
	public void copyUnchanged() throws Exception {
		BackedOperationsSetImpl backedOpSet = new BackedOperationsSetImpl();
		Context initialContext = backedOpSet.asContextForClient(0);
		ContextualizedOperation op1 = createOperationWith(initialContext);
		backedOpSet.add(op1);
		
		BackedOperationsSetImpl copiedBackedOpSet = backedOpSet.copy();

		ContextualizedOperation op2 = createOperationWith(op1.getSelfIncludingContext());
		backedOpSet.add(op2);
		
		assertEquals(op1, backedOpSet.getOperation(0, 1));
		assertEquals(op2, backedOpSet.getOperation(0, 2));
		assertEquals(backedOpSet.getOperation(0, 1), copiedBackedOpSet.getOperation(0, 1));
		
		try {
			copiedBackedOpSet.getOperation(0, 2);
			fail("getOperation must throw an exception.");
		} catch (Exception e) {
			assertTrue("Exception caught", true);
		}
		
		assertFalse(backedOpSet.asContextForClient(2).equals(copiedBackedOpSet.asContextForClient(2)));
	}
}
