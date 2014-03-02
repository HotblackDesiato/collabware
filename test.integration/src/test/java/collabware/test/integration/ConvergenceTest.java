package collabware.test.integration;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import collabware.api.document.PrimitiveOperationTransformer;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.GraphOperationsProviderImpl;

/**
 * This test whether a transformation of operations converges, which is the central property of OT.
 * For any concurrent operations op1 and op2, it should not matter whether op1 is applied 
 * first and then op2_transformed or vice versa. This is what is tested here for all graph operations.
 * If another data structure is added its operations must be tested in a similar way. 
 */
@RunWith(Parameterized.class)
public class ConvergenceTest {
	private static ModelProviderImpl modelProvider = new ModelProviderImpl();
	private static GraphOperationsProviderImpl operationProvider = new GraphOperationsProviderImpl();
	
	private ModifyableModel model0 = modelProvider.createDocument("test");
	private ModifyableModel model1 = modelProvider.createDocument("test");
	private PrimitiveOperation op1;
	private PrimitiveOperation op2;
	
	@Parameters
	public static Collection<Object[]> parameters() {
		
		Object[][] data = new Object[][]{
			{addNode("aNode"), addNode("anotherNode")},
			{addNode("aNode"), addNode("aNode")},
			{addNode("aNode"), remNode("removeNode")},
			{addNode("aNode"), id()},
			
			{remNode("removeNode"),   remNode("removeNode")},
			{remNode("removeNode"),   id()},
			
			{setAttr("theNode", "attr", null, "blah"), setAttr("theNode", "otherAttr", null, "foo")},
			{setAttr("theNode", "attr", null, "blah"), setAttr("theNode", "attr", null, "blah")},
			{setAttr("theNode", "attr", null, "blah"), setAttr("theNode", "attr", null, "foo")},
			{setAttr("theNode", "attr", null, "blah"), remNode("removeNode")},
			{setAttr("theNode", "attr", null, "blah"), remNode("theNode")},
			{setAttr("theNode", "attr", null, "blah"), addNode("aNode")},
			{setAttr("theNode", "attr", null, "blah"), id()},

			{setRef("theNode", "attr", null, "targetNode"), setRef("theNode", "otherAttr", null, "otherTargetNode")},
			{setRef("theNode", "attr", null, "targetNode"), setRef("theNode", "attr", null, "targetNode")},
			{setRef("theNode", "attr", null, "targetNode"), setRef("theNode", "attr", null, "otherTargetNode")},
			{setRef("theNode", "attr", null, "targetNode"), remNode("removeNode")},
			{setRef("theNode", "attr", null, "targetNode"), remNode("theNode")},
			{setRef("theNode", "attr", null, "targetNode"), remNode("targetNode")},
			{setRef("theNode", "attr", null, "targetNode"), id()},
			
			{addRef("source", "ref", 0, "targetNode"),addRef("source", "ref", 0, "targetNode")},
			{addRef("source", "ref", 0, "targetNode"),addRef("source", "ref", 3, "otherTargetNode")},
			{addRef("source", "ref", 2, "targetNode"),remRef("source", "ref", 3, "target4")},
			{addRef("source", "ref", 2, "targetNode"),remRef("source", "ref", 2, "target3")},
			{addRef("source", "ref", 0, "targetNode"),addRef("source", "otherRef", 0, "targetNode")},
			{addRef("theNode", "ref", 0, "targetNode"),remNode("theNode")},
			{addRef("source", "ref", 0, "targetNode"),remNode("targetNode")},
			{addRef("source", "ref", 0, "targetNode"),remNode("removeNode")},
			
			{id(), id()},
			
		};
		return Arrays.asList(data);
	}
	
	public ConvergenceTest(PrimitiveOperation op1, PrimitiveOperation op2) {
		this.op1 = op1;
		this.op2 = op2;

		configureGraphOf(model0);
		configureGraphOf(model1);
	}

	private void configureGraphOf(ModifyableModel modifyableModel) {
		ModifyableGraph graph = (ModifyableGraph) modifyableModel.getGraph();
		graph.addNode("removeNode");
		graph.addNode("theNode");
		ModifyableNode source = graph.addNode("source");
		graph.addNode("targetNode");
		graph.addNode("otherTargetNode");
		
		source.getNaryReferences().add("ref", 0, graph.addNode("target1"));
		source.getNaryReferences().add("ref", 1, graph.addNode("target2"));
		source.getNaryReferences().add("ref", 2, graph.addNode("target3"));
		source.getNaryReferences().add("ref", 3, graph.addNode("target4"));
	}

	@Test
	public void convergence() throws Exception {
		assertTransformationConvergesFor(op1, op2);		
	}

	private static PrimitiveOperation id() {
		return operationProvider.createNoOperation();
	}
	private static PrimitiveOperation remNode(String nodeId) {
		return operationProvider.createRemoveNodeOperation(nodeId);
	}

	private static PrimitiveOperation addNode(String nodeId) {
		return operationProvider.createAddNodeOperation(nodeId);
	}

	private static Object setAttr(String nodeId, String attributeName, String oldValue, String newValue) {
		return operationProvider.createSetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}

	private static Object setRef(String nodeId, String referenceName, String oldTargetId, String newTargetId) {
		return operationProvider.createSetUnaryReferenceOperation(nodeId, referenceName, oldTargetId, newTargetId);
	}
	private static Object addRef(String nodeId, String referenceName, int position, String targetId) {
		return operationProvider.createAddReferenceOperation(nodeId, referenceName, position, targetId);
	}
	private static Object remRef(String nodeId, String referenceName, int position, String targetId) {
		return operationProvider.createRemoveReferenceOperation(nodeId, referenceName, position, targetId);
	}

	@SuppressWarnings("unchecked")
	private void assertTransformationConvergesFor(PrimitiveOperation operation1, PrimitiveOperation operation2) {
		int numberOfCollisions = 0; 
		try {
			model0.apply(operation2);
			model0.apply(transform(operation1,	operation2));
		} catch (CollisionException e) {
			numberOfCollisions++;
		}
		try {
			model1.apply(operation1);
			model1.apply(transform(operation2,	operation1));
		} catch (CollisionException e) {
			numberOfCollisions++;
		}
		
		if (numberOfCollisions == 0) {
			assertBothModelsAreEqual();
		} else {
			assertThat("Collisions must occur symmetrically.",numberOfCollisions, anyOf(equalTo(0), equalTo(2)));
		}
	}

	private PrimitiveOperation transform(PrimitiveOperation operation1,	PrimitiveOperation operation2) throws CollisionException {
		PrimitiveOperationTransformer trans = new ModelProviderImpl();
		return trans.transform(operation1, operation2);
	}

	private void assertBothModelsAreEqual() {
		assertTrue(model0.getGraph().equals(model1.getGraph()));
	}
}
