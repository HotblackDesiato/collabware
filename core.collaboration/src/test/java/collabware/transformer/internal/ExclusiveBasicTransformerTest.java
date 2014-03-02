package collabware.transformer.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import collabware.api.document.ModifyableDocument;
import collabware.api.document.PrimitiveOperationTransformer;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.Operation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.api.transform.CollisionException;
import collabware.collaboration.internal.context.VectorBasedContext;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.transformer.PrecedenceRule;

public class ExclusiveBasicTransformerTest {

	private static final List<PrimitiveOperation> NO_OPERATION = Arrays.asList((PrimitiveOperation)NoOperation.NOP);
	private ComplexOperationTransformer exclusiveTransformer;
	private Operation preferedOperation;
	
	public class ServerTakesPrecedenceRule implements PrecedenceRule {

		@Override
		public boolean hasPrecedence(ContextualizedComplexOperation first, ContextualizedComplexOperation second) {
			return first == preferedOperation;
		}

	}

	private static class AllConflictsMatrix implements PrimitiveOperationTransformer {
		@Override
		public PrimitiveOperation transform(PrimitiveOperation base, PrimitiveOperation against) throws CollisionException {
			throw new CollisionException(against, base);
		}
		
	}
	
	@Before
	public void setup() {
		exclusiveTransformer = new ExclusiveComplexTransformer(new AllConflictsMatrix(), new ServerTakesPrecedenceRule());
	}
	
	@Test
	public void conflictResolution() throws Exception {
		ContextualizedComplexOperation offender = givenComplexOperation(ctx(0, sqn(1,2,3)));
		ContextualizedComplexOperation offended = givenComplexOperation(ctx(1, sqn(1,2,3)));
		givenPrecedenceTo(offended);
		
		TransformationResult transformed = exclusiveTransformer.transform(offended, offender);

		assertThat(primitivesOf(transformed.getFirstTransformed()), equalTo(concat(inverseOf(offender), primitivesOf(offended))));
		assertTrue(transformed.getFirstTransformed().getContext().contains(offender));
		
		assertThat(primitivesOf(transformed.getSecondTransformed()), equalTo(NO_OPERATION));
		assertTrue(transformed.getSecondTransformed().getContext().contains(offended));
	}

	@Test
	public void conflictResolution_Transposed() throws Exception {
		ContextualizedComplexOperation offender = givenComplexOperation(ctx(0, sqn(1,2,3)));
		ContextualizedComplexOperation offended = givenComplexOperation(ctx(1, sqn(1,2,3)));
		givenPrecedenceTo(offended);
		
		TransformationResult transformed = exclusiveTransformer.transform(offender, offended);
	
		assertThat(primitivesOf(transformed.getFirstTransformed()), equalTo(NO_OPERATION));
		assertTrue(transformed.getFirstTransformed().getContext().contains(offended));

		assertThat(primitivesOf(transformed.getSecondTransformed()), equalTo(concat(inverseOf(offender), primitivesOf(offended))));
		assertTrue(transformed.getSecondTransformed().getContext().contains(offender));
	}

	private void givenPrecedenceTo(ContextualizedComplexOperation precedence) {
		this.preferedOperation = precedence;
	}

	private Object concat(List<PrimitiveOperation> inverseOf, List<PrimitiveOperation> primitivesOf) {
		inverseOf.addAll(primitivesOf);
		return inverseOf;
	}

	@SuppressWarnings("unchecked")
	private List<PrimitiveOperation> primitivesOf(ContextualizedComplexOperation offended) {
		return (List<PrimitiveOperation>) offended.getDecoratedOperation().getPrimitiveOperations();
	}

	private List<PrimitiveOperation> inverseOf(ContextualizedComplexOperation offender) {
		return new ArrayList<PrimitiveOperation>(offender.getDecoratedOperation().inverse().getPrimitiveOperations());
	}

	private ContextualizedComplexOperation givenComplexOperation(Context c) {
		return complex(c, "", somePrimitiveOperations());
	}


	private PrimitiveOperation[] somePrimitiveOperations() {
		PrimitiveOperation[]  somePrimitiveOperations = new PrimitiveOperation[3];
		for (int i = 0; i < somePrimitiveOperations.length; i++) {
			PrimitiveOperation op = createMock(PrimitiveOperation.class);
			PrimitiveOperation inverse = createMock(PrimitiveOperation.class);
			replay(inverse);
			expect(op.inverse()).andStubReturn(inverse);
			replay(op);
			somePrimitiveOperations[i]=op;
		}
		return somePrimitiveOperations;
	}


	@Test
	public void test() throws CollisionException {
		
		ModelProviderImpl modelProviderImpl = new ModelProviderImpl();
		String initialModelLiteral = 
				"n1, n2, n3, n4, n5, n6," +
				"n1.ref[0]->n2," +
				"n1.ref[1]->n3";
		String expectedModelLiteral = 
				"n1, n2, n3, n4, n5, n6," +
				"n1.ref[0]->n4," +
				"n1.ref[1]->n5," +
				"n1.ref[2]->n6";
		
		ModifyableDocument m1 = modelProviderImpl.createModelFromLiteral("", initialModelLiteral);
		ModifyableDocument m2 = modelProviderImpl.createModelFromLiteral("", initialModelLiteral);
		ModifyableDocument expectedModel = modelProviderImpl.createModelFromLiteral("", expectedModelLiteral);
		
		ComplexOperationTransformer t = new CrossProductComplexOperationTransformer(modelProviderImpl);
		ContextualizedComplexOperation o1 = complex(ctx(0, sqn()), "blah", addRef("n1", "ref", 0, "n4"), remRef("n1", "ref", 2, "n3"), remRef("n1", "ref", 1, "n2"));
		ContextualizedComplexOperation o2 = complex(ctx(1, sqn()), "blah", addRef("n1", "ref", 0, "n4"), addRef("n1", "ref", 3, "n5"), addRef("n1", "ref", 4, "n6"));
		
		TransformationResult r = t.transform(o1, o2);
		
		ContextualizedComplexOperation o1Trans = r.getFirstTransformed();
		ContextualizedComplexOperation o2Trans = r.getSecondTransformed();
		
		m1.apply(o1);
		m1.apply(o2Trans);
		
		m2.apply(o2);
		m2.apply(o1Trans);
		
		assertEquals(m1,m2);
		assertEquals(m2,expectedModel);		
	}

	private ContextualizedComplexOperation complex(Context context, String description, PrimitiveOperation ...ops) {
		ComplexOperation complex = new ComplexOperationImpl(description, Arrays.asList(ops));
		return new ContextualizedComplexOperationImpl(context, complex);
	}

	private PrimitiveOperation remRef(String nodeId, String ref, int i, String targetId) {
		return new RemoveReferenceOperation(nodeId, ref, i, targetId);
	}

	private PrimitiveOperation addRef(String node, String ref, int i, String target) {
		return new AddReferenceOperation(node, ref, i, target);
	}

	private Context ctx(int i, int[] sqn) {
		return new VectorBasedContext(i, sqn);
	}

	private int[] sqn(int ...sequenceNumbers) {
		return sequenceNumbers;
	}

}
