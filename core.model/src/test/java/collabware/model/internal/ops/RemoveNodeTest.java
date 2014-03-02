package collabware.model.internal.ops;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import collabware.api.operations.OperationApplicationException;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.ModelImpl;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.RemoveNodeOperation;

public class RemoveNodeTest {

	@Test
	public void create() throws Exception {
		RemoveNodeOperation removeNode = new RemoveNodeOperation("node1");
		
		assertEquals("node1", removeNode.getNodeId());
	}

	@Test
	public void apply() throws Exception {
		ModifyableModel doc = new ModelImpl("");
		ModifyableNode node1 = doc.getGraph().addNode("node1");
		
		RemoveNodeOperation removeNode = new RemoveNodeOperation("node1");
		removeNode.apply(doc);
		
		assertThat(doc.getGraph().contains(node1), is(false));
	}

	@Test(expected=OperationApplicationException.class)
	public void applyToGraphWithoutNode() throws Exception {
		ModifyableModel doc = new ModelImpl("");
		
		RemoveNodeOperation removeNode = new RemoveNodeOperation("node1");
		removeNode.apply(doc);
	}

	
	@Test
	public void inverse() throws Exception {
		RemoveNodeOperation removeNode = new RemoveNodeOperation("node1");
		AddNodeOperation inverse = removeNode.inverse();
		
		assertEquals("node1", inverse.getNodeId());
	}
	
	@Test
	public void serialize() {
		RemoveNodeOperation remNode = new RemoveNodeOperation("node23");
		Map<String, Object> serialized = remNode.serialize();
		
		assertThat(serialized, hasEntry("t", (Object)"rn"));
		assertThat(serialized, hasEntry("n", (Object)"node23"));
		
	}
}
