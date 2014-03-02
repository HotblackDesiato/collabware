package collabware.model.internal.ops;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import collabware.api.operations.OperationApplicationException;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.SetUnaryReferenceOperation;

public class SetUnaryReferenceTest {
	
	private final ModelProvider modelProvider = new ModelProviderImpl();
	
	@Test
	public void apply() throws Exception {
		ModifyableModel model = givenModel("node, oldTargetNode, newTargetNode, node.ref->oldTargetNode");
	
		SetUnaryReferenceOperation setRef = new SetUnaryReferenceOperation("node", "ref", "oldTargetNode", "newTargetNode");
		setRef.apply(model);
		
		assertThat(model.getGraph().getNode("node").getUnaryReferences().get("ref").getId(), is("newTargetNode"));
	}
	
	private ModifyableModel givenModel(String literal) {
		return (ModifyableModel) modelProvider.createModelFromLiteral("", literal);
	}

	@Test
	public void setNullReferenceToNewTarget() throws Exception {
		ModifyableModel model = givenModel("node, oldTargetNode, newTargetNode");
		
		SetUnaryReferenceOperation setRef = new SetUnaryReferenceOperation("node", "ref", null, "newTargetNode");
		setRef.apply(model);
		
		assertThat(model.getGraph().getNode("node").getUnaryReferences().get("ref").getId(), is("newTargetNode"));
	}
	
	@Test
	public void setReferenceToNull() throws Exception {
		ModifyableModel model = givenModel("node, oldTargetNode, newTargetNode, node.ref->oldTargetNode");
		
		SetUnaryReferenceOperation setRef = new SetUnaryReferenceOperation("node", "ref", "oldTargetNode", null);
		setRef.apply(model);
		
		assertThat(model.getGraph().getNode("node").getUnaryReferences().get("ref"), is(nullValue()));
	}

	@Test(expected= OperationApplicationException.class)
	public void applyFailsDueToOldTargetIdMismatch() throws Exception {
		ModifyableModel model = givenModel("node, oldTargetNode, newTargetNode, node.ref->oldTargetNode");
		
		SetUnaryReferenceOperation setRef = new SetUnaryReferenceOperation("node", "ref", "SomeOtherOldTargetNode", "newTargetNode");
		setRef.apply(model);
	}
	
	@Test(expected= OperationApplicationException.class)
	public void applyFailsDueToOldTargetIdMismatch2() throws Exception {
		ModifyableModel model = givenModel("node, oldTargetNode, newTargetNode, node.ref->oldTargetNode");
		
		SetUnaryReferenceOperation setRef = new SetUnaryReferenceOperation("node", "ref", "SomeOtherOldTargetNode", "newTargetNode");
		setRef.apply(model);
	}

	@Test(expected= OperationApplicationException.class)
	public void applyFailsDueToOldTargetIdMismatch3() throws Exception {
		ModifyableModel model = givenModel("node, oldTargetNode, newTargetNode, node.ref->oldTargetNode");
		
		SetUnaryReferenceOperation setRef = new SetUnaryReferenceOperation("node", "ref", null, "newTargetNode");
		setRef.apply(model);
	}

	@Test(expected= OperationApplicationException.class)
	public void applyFailsDueToMissingSourceNode() throws Exception {
		ModifyableModel model = givenModel("newTargetNode");
		
		SetUnaryReferenceOperation setRef = new SetUnaryReferenceOperation("noSuchNode", "ref", null, "newTargetNode");
		setRef.apply(model);
	}

	@Test(expected= OperationApplicationException.class)
	public void applyFailsDueToMissingTargetNode() throws Exception {
		ModifyableModel model = givenModel("node, oldTargetNode, newTargetNode, node.ref->oldTargetNode");
		
		SetUnaryReferenceOperation setRef = new SetUnaryReferenceOperation("node", "ref", "oldTargetNode", "noSuchNode");
		setRef.apply(model);
	}
	
	@Test
	public void inverse() throws Exception {
		SetUnaryReferenceOperation setReference = new SetUnaryReferenceOperation("someNode", "someAttr", "theOldValue", "theNewValue");
		SetUnaryReferenceOperation inverse = setReference.inverse();
		
		assertEquals(setReference.getNodeId(), inverse.getNodeId());
		assertEquals(setReference.getReferenceName(), inverse.getReferenceName());
		assertEquals(setReference.getNewTargetId(), inverse.getOldTargetId());
		assertEquals(setReference.getOldTargetId(), inverse.getNewTargetId());
	}
	
	@Test
	public void serialize() {
		SetUnaryReferenceOperation setReference = new SetUnaryReferenceOperation("someNode", "someRef", "theOldTarget", "theNewTarget");
		Map<String, Object> serialized = setReference.serialize();
		
		assertThat(serialized, hasEntry("t", (Object)"sr"));
		assertThat(serialized, hasEntry("r", (Object)setReference.getReferenceName()));
		assertThat(serialized, hasEntry("ot", (Object)setReference.getOldTargetId()));
		assertThat(serialized, hasEntry("nt", (Object)setReference.getNewTargetId()));
		
	}
}
