package collabware.transformer.internal;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.api.transform.CollisionException;
import collabware.collaboration.internal.context.VectorBasedContext;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;

public class BasicTransformaterTest {

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
