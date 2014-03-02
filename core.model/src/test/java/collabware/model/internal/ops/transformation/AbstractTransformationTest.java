package collabware.model.internal.ops.transformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.ModelProvider;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.NodeOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;

public class AbstractTransformationTest {


	public class Transformation {

		private final PrimitiveOperation operation;
		private PrimitiveOperation transformed;

		public Transformation(PrimitiveOperation operation) {
			this.operation = operation;
		}

		public TransformationResult against(PrimitiveOperation against) {
			try {
				transformed = partiallyInclusiveTransformation.transform(operation, against);
				return new TransformationResult(transformed);
			} catch (CollisionException e) {
				return new TransformationResult(operation, e);
			}
		}

	}
	
	public static class TransformationResult {
	
		private final PrimitiveOperation transformed;
		private final CollisionException collisionException;
	
		public TransformationResult(PrimitiveOperation transformed) {
			this.transformed = transformed;
			this.collisionException = null;
		}
		
		public TransformationResult(PrimitiveOperation original, CollisionException e) {
			this.transformed = null;
			this.collisionException = e;
		}
	
		public void equals(PrimitiveOperation expected) {
			assertOperationsEqual(expected, transformed);
		}

	
		private void assertOperationsEqual(PrimitiveOperation expected, PrimitiveOperation actual) {
			assertNotNull("Transformation result must not be null.", actual);
			assertEquals(expected.getClass(), actual.getClass());
			
			if (expected instanceof AddNodeOperation || expected instanceof RemoveNodeOperation) {
				assertEquals(((NodeOperation) expected).getNodeId(), ((NodeOperation) actual).getNodeId());
			} else if (expected instanceof SetAttributeOperation) {
				SetAttributeOperation expectSetAttr = (SetAttributeOperation) expected;
				SetAttributeOperation actualSetAttr = (SetAttributeOperation) actual;
				assertEquals(expectSetAttr.getNodeId(), actualSetAttr.getNodeId());
				assertEquals(expectSetAttr.getAttributeName(), actualSetAttr.getAttributeName());
				assertEquals(expectSetAttr.getNewValue(), actualSetAttr.getNewValue());
				assertEquals(expectSetAttr.getOldValue(), actualSetAttr.getOldValue());
			} else if (expected instanceof SetUnaryReferenceOperation) {
				SetUnaryReferenceOperation expectSetRef = (SetUnaryReferenceOperation) expected;
				SetUnaryReferenceOperation actualSetRef = (SetUnaryReferenceOperation) actual;
				assertEquals(expectSetRef.getNodeId(), actualSetRef.getNodeId());
				assertEquals(expectSetRef.getReferenceName(), actualSetRef.getReferenceName());
				assertEquals(expectSetRef.getNewTargetId(), actualSetRef.getNewTargetId());
				assertEquals(expectSetRef.getOldTargetId(), actualSetRef.getOldTargetId());
			} else if (expected instanceof AddReferenceOperation) {
				AddReferenceOperation expectAddRef = (AddReferenceOperation) expected;
				AddReferenceOperation actualAddRef = (AddReferenceOperation) actual;
				assertEquals(expectAddRef.getReferenceName(), actualAddRef.getReferenceName());
				assertEquals(expectAddRef.getTargetId(), actualAddRef.getTargetId());
				assertEquals(expectAddRef.getPosition(), actualAddRef.getPosition());
			} else if (expected instanceof RemoveReferenceOperation) {
				RemoveReferenceOperation expectRemRef = (RemoveReferenceOperation) expected;
				RemoveReferenceOperation actualRemRef = (RemoveReferenceOperation) actual;
				assertEquals(expectRemRef.getReferenceName(), actualRemRef.getReferenceName());
				assertEquals(expectRemRef.getTargetId(), actualRemRef.getTargetId());
				assertEquals(expectRemRef.getPosition(), actualRemRef.getPosition());
			} else if (expected instanceof NoOperation) {
				assertTrue(true);
			} else {
				fail();
			}
		}
	
		public void collides() {
			assertNotNull("Exepected collision", collisionException);
		}
	
	}

	protected static NoOperation id() {
		return NoOperation.NOP;
	}

	protected static AddNodeOperation addNode(String nodeId) {
		return new AddNodeOperation(nodeId);
	}

	protected static PrimitiveOperation remNode(String nodeId) {
		return new RemoveNodeOperation(nodeId);
	}

	protected static PrimitiveOperation setAttr(String nodeId, String attributeName, String oldValue, String newValue) {
		return new SetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}

	protected static PrimitiveOperation setRef(String nodeId, String referenceName, String oldTargetId, String newTargetId) {
		return new SetUnaryReferenceOperation(nodeId, referenceName, oldTargetId, newTargetId);
	}
	
	protected static PrimitiveOperation addRef(String nodeId, String referenceName, int position, String oldTargetId) {
		return new AddReferenceOperation(nodeId, referenceName, position, oldTargetId);
	}
	
	protected static PrimitiveOperation remRef(String nodeId, String referenceName, int position, String oldTargetId) {
		return new RemoveReferenceOperation(nodeId, referenceName, position, oldTargetId);
	}

	protected ModelProvider partiallyInclusiveTransformation = new ModelProviderImpl();

	public AbstractTransformationTest() {
		super();
	}

	protected Transformation transform(PrimitiveOperation primitiveOperation) {
		return new Transformation(primitiveOperation);
	}

}