package collabware.api.operations;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import collabware.api.document.ModifyableDocument;

public class ComplexOperationTest {
	@Test
	public void getDescriptionReturnsDescription() throws Exception {
		ComplexOperation complex1 = new ComplexOperationImpl("The Description", createNPrimitiveOperations(3));
		ComplexOperation complex2 = new ComplexOperationImpl("Some other Description", createNPrimitiveOperations(3));
		
		assertEquals("The Description", complex1.getDescription());
		assertEquals("Some other Description", complex2.getDescription());
	}
	
	@Test
	public void primitiveOperationsCanBeIteratedOver() throws Exception {
		List<PrimitiveOperation> primitives = createNPrimitiveOperations(3);
		ComplexOperation complex = new ComplexOperationImpl("My Complex Op", primitives);
		int i = 0;
		for (PrimitiveOperation op: complex) {
			assertSame(primitives.get(i), op);
			i++;
		}
	}

	@Test(expected=UnsupportedOperationException.class)
	public void iteratorIsUnmodifyable() throws Exception {
		List<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ComplexOperation complex = new ComplexOperationImpl("My Complex Op", primitives);
		
		Iterator<PrimitiveOperation> iterator = complex.iterator();
		iterator.next();
		iterator.remove();
	}
	
	@Test
	public void getPrimitiveOperationsReturnsAllPrimitiveOperations() throws Exception {
		List<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ComplexOperation complex = new ComplexOperationImpl("My Complex Op", primitives);
		
		assertEquals(primitives, complex.getPrimitiveOperations());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void getPrimitiveOperationsIsUnmodifyable() throws Exception {
		List<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ComplexOperation complex = new ComplexOperationImpl("My Complex Op", primitives);
		
		complex.getPrimitiveOperations().remove(0);
	}
	
	@Test
	public void getPrimitiveOperationsMakesCopy() throws Exception {
		List<PrimitiveOperation> primitives = createNPrimitiveOperations(5);
		ComplexOperation complex = new ComplexOperationImpl("My Complex Op", primitives);
		
		PrimitiveOperation removed = primitives.remove(0);
		assertTrue(complex.getPrimitiveOperations().contains(removed));
	}
	
	@Test
	public void sizeReturnsTheNumberOfPrimitiveOperations() throws Exception {
		ComplexOperation complex1 = new ComplexOperationImpl("The Description", createNPrimitiveOperations(3));
		ComplexOperation complex2 = new ComplexOperationImpl("Some other Description", createNPrimitiveOperations(5));
		
		assertEquals(3, complex1.size());
		assertEquals(5, complex2.size());
	}
	
	@Test
	public void applyAppliesAllPrimitiveOperationsInOrder() throws Exception {
		ModifyableDocument doc = createMock(ModifyableDocument.class);
		replay(doc);
		List<PrimitiveOperation> primitives = createNPrimitiveOperationsExpectingApplyOnGraph(5, doc);
		ComplexOperation complex = new ComplexOperationImpl("My Complex Op", primitives);

		complex.apply(doc);

		verifyApplyCalledOnAllOperations(primitives);
	}

	private void verifyApplyCalledOnAllOperations(List<PrimitiveOperation> primitives) {
		for (PrimitiveOperation op: primitives) {
			verify(op);			
		}
	}
	
	private List<PrimitiveOperation> createNPrimitiveOperationsExpectingApplyOnGraph(int numberOfOperations, ModifyableDocument doc) throws Exception {
		ArrayList<PrimitiveOperation> primitives = new ArrayList<PrimitiveOperation>();
		for (int i = 0; i < numberOfOperations; i++) {
			PrimitiveOperation op = createMock(PrimitiveOperation.class);
			op.apply(doc);
			expectLastCall().once();
			replay(op);
			primitives.add(op);
		}
		return primitives;
	}

	private ArrayList<PrimitiveOperation> createNPrimitiveOperations(int numberOfOperations) {
		ArrayList<PrimitiveOperation> primitives = new ArrayList<PrimitiveOperation>();
		for (int i = 0; i < numberOfOperations; i++) {
			PrimitiveOperation op = createMock(PrimitiveOperation.class);
			PrimitiveOperation inverse = createMock(PrimitiveOperation.class);
			replay(inverse);
			expect(op.inverse()).andStubReturn(inverse);
			replay(op);
			primitives.add(op);
		}
		return primitives;
	}
	
	@Test
	public void inverseIsInverseOfPrimitivesInReverseOrder() throws Exception {
		ComplexOperation complex = new ComplexOperationImpl("The Description", createNPrimitiveOperations(2));
		ComplexOperation inverse = complex.inverse();
		
		assertThat(inverse.size(), is(complex.size()));
		for (int i=0; i < complex.size(); i++) {
			PrimitiveOperation originalInverse = complex.getPrimitiveOperations().get(i).inverse();
			PrimitiveOperation primitiveInverse = (PrimitiveOperation)inverse.getPrimitiveOperations().get(inverse.size()-1-i);
			assertThat(originalInverse, is(primitiveInverse));
		}
	}
}