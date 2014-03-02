package collabware.collaboration.internal.context;

import static collabware.collaboration.internal.context.OperationTestUtils.createOperationWith;
import static collabware.collaboration.internal.context.OperationTestUtils.emptyContextForClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import collabware.api.operations.context.BackedContextDifference;
import collabware.api.operations.context.BackedOperationsSet;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextDifference;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.api.operations.context.NoSuchOperationException;


public class ContextDifferenceTest {
	@Test
	public void contextDiffEmpty() throws Exception {
		BackedOperationsSet backedOperationsSet = new BackedOperationsSetImpl();
		Context context1 = backedOperationsSet.asContextForClient(1);
		Context context2 = backedOperationsSet.asContextForClient(2);
		
		ContextDifference diff = context1.minus(context2);
		BackedContextDifference backedDiff = diff.backedBy(backedOperationsSet);

		assertEquals(0, backedDiff.size());
	}
	
	@Test
	public void contextDiffNonEmpty() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		ContextualizedOperation o1 = createOperationWith(backedOpSet.asContextForClient(0));
		backedOpSet.add(o1);
		ContextualizedOperation o2 = createOperationWith(backedOpSet.asContextForClient(2));
		backedOpSet.add(o2);
		
		ContextDifference diff = o2.getContext().minus(o1.getContext());
		BackedContextDifference backedDiff = diff.backedBy(backedOpSet);
		
		assertEquals(1, backedDiff.size());
		
		assertTrue(diff.contains(o1));
		assertFalse(diff.contains(o2));
	}
	
	@Test
	public void nonTrivialContextDiffEmpty() throws Exception {
		ContextualizedOperation o1 = createOperationWith(emptyContextForClient(0));
		ContextualizedOperation o2 = createOperationWith(o1.getContext().including(o1));
		
		ContextualizedOperation o3 = createOperationWith(emptyContextForClient(1).including(o1));
		ContextualizedOperation o4 = createOperationWith(o3.getContext().including(o3));
		
		ContextDifference diff = o3.getContext().minus(o2.getContext());
		
		assertEquals(0, diff.size());
		assertFalse(diff.contains(o1));
		assertFalse(diff.contains(o2));
		assertFalse(diff.contains(o3));
		assertFalse(diff.contains(o4));
	}
	
	
	@Test
	public void nonTrivialContextDiff() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		
		ContextualizedOperation o1 = createOperationWith( emptyContextForClient(0));
		backedOpSet.add(o1);
		ContextualizedOperation o2 = createOperationWith( contextOf(o1) .including(o1));		
		backedOpSet.add(o2);
		ContextualizedOperation o5 = createOperationWith( contextOf(o2) .including(o2));
		backedOpSet.add(o5);
		ContextualizedOperation o6 = createOperationWith( contextOf(o5) .including(o5));
		backedOpSet.add(o6);
		ContextualizedOperation o3 = createOperationWith( emptyContextForClient(1).including(o1).including(o2));
		backedOpSet.add(o3);
		ContextualizedOperation o4 = createOperationWith( contextOf(o3) .including(o3));
		backedOpSet.add(o4);
		
		ContextDifference diff = contextOf(o6) .minus (contextOf(o4));
		BackedContextDifference backedDiff = diff.backedBy(backedOpSet);
		ContextualizedOperation selected = backedDiff.selectAndRemoveWithSubContextOf(o6);
		
		assertSame(o5, selected);
		assertEquals(0, backedDiff.size());
	}
	@Test
	public void nonTrivialContextDiff2() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		
		ContextualizedOperation o1 = createOperationWith( emptyContextForClient(0));
		backedOpSet.add(o1);
		ContextualizedOperation o2 = createOperationWith( contextOf(o1) .including(o1));		
		backedOpSet.add(o2);
		ContextualizedOperation o5 = createOperationWith( contextOf(o2) .including(o2));
		backedOpSet.add(o5);
		ContextualizedOperation o6 = createOperationWith( contextOf(o5) .including(o5));
		backedOpSet.add(o6);
		ContextualizedOperation o3 = createOperationWith( emptyContextForClient(1).including(o1).including(o2));
		backedOpSet.add(o3);
		ContextualizedOperation o4 = createOperationWith( contextOf(o3) .including(o3));
		backedOpSet.add(o4);
		
		ContextDifference diff = contextOf(o4) .minus (contextOf(o2));
		BackedContextDifference backedDiff = diff.backedBy(backedOpSet);
			
		assertSame(o2, backedDiff.selectAndRemoveWithSubContextOf(o4));
		assertSame(o3, backedDiff.selectAndRemoveWithSubContextOf(o4));
		assertEquals(0, backedDiff.size());
	}
	
	@Test
	public void selectAndRemove() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		ContextualizedOperation o1 = createOperationWith(backedOpSet.asContextForClient(0));
		backedOpSet.add(o1);
		ContextualizedOperation o2 = createOperationWith(backedOpSet.asContextForClient(2));
		backedOpSet.add(o2);
		
		ContextDifference diff = contextOf(o2) .minus (contextOf(o1));
		BackedContextDifference backedDiff = diff.backedBy(backedOpSet);

		ContextualizedOperation selected = backedDiff.selectAndRemoveWithSubContextOf(o2);
		
		assertSame(o1, selected);
		assertEquals(0, backedDiff.size());
	}
	
	@Test
	public void selectAndRemove2() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		ContextualizedOperation o1 = createOperationWith(backedOpSet.asContextForClient(0));
		backedOpSet.add(o1);
		ContextualizedOperation o2 = createOperationWith(backedOpSet.asContextForClient(2));
		backedOpSet.add(o2);
		
		ContextDifference diff = contextOf(o2) .minus (contextOf(o1));
		BackedContextDifference backedDiff = diff.backedBy(backedOpSet);
		
		ContextualizedOperation selected = backedDiff.selectAndRemoveWithSubContextOf(o1);
		assertEquals(o1, selected);
	}

	@Test
	public void selectAndRemove3() throws Exception {
		BackedOperationsSet modelState = new BackedOperationsSetImpl();
		ContextualizedOperation o1 = createOperationWith(modelState.asContextForClient(0));
		ContextualizedOperation o2 = createOperationWith(modelState.asContextForClient(1));
		modelState.add(o1);
		
		BackedContextDifference minus = modelState.minus(o2.getContext());
		ContextualizedOperation o = minus.selectAndRemoveWithSubContextOf(o2);
		
		assertEquals(o1, o);
		
	}

	
	@Test(expected=NoSuchOperationException.class)
	public void selectAndRemoveNoSuchOperation() throws Exception {
		BackedOperationsSet backedOpSet = new BackedOperationsSetImpl();
		ContextualizedOperation o1 = createOperationWith(backedOpSet.asContextForClient(0));
		backedOpSet.add(o1);
		ContextualizedOperation o2 = createOperationWith(backedOpSet.asContextForClient(2));
		backedOpSet.add(o2);
		
		ContextDifference diff = contextOf(o1) .minus (contextOf(o2));
		BackedContextDifference backedDiff = diff.backedBy(backedOpSet);
		
		backedDiff.selectAndRemoveWithSubContextOf(o1);
	}

	private Context contextOf(ContextualizedOperation o) {
		return o.getContext();
	}
	
}
