package collabware.model.internal.ops;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import collabware.api.operations.OperationApplicationException;
import collabware.model.ModifyableModel;
import collabware.model.internal.ModelImpl;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.NodeOperation;
import collabware.model.internal.ops.RemoveNodeOperation;

public class AddNodeTest {

	@Test
	public void create() throws Exception {
		NodeOperation addNode = new AddNodeOperation("node1");
		
		assertEquals("node1", addNode.getNodeId());
	}
	
	@Test
	public void apply() throws Exception {
		AddNodeOperation addNode = new AddNodeOperation("node1");
		ModifyableModel doc = new ModelImpl("");
		
		addNode.apply(doc);
		
		assertThat(doc.getGraph().getNode("node1"), is(not(nullValue())));
	}
	

	@Test(expected=OperationApplicationException.class)
	public void applyFails() throws Exception {
		AddNodeOperation addNode = new AddNodeOperation("node1");
		ModifyableModel doc = new ModelImpl("");
		doc.getGraph().addNode("node1");
		
		addNode.apply(doc);
	}

	@Test
	public void inverse() throws Exception {
		AddNodeOperation addNode = new AddNodeOperation("node1");
		RemoveNodeOperation inverse = addNode.inverse();
		
		assertEquals("node1", inverse.getNodeId());
	}
	
	@Test
	public void serialize() {
		AddNodeOperation addNode = new AddNodeOperation("node42");
		Map<String, Object> serialized = addNode.serialize();
		
		assertThat(serialized, hasEntry("t", (Object)"an"));
		assertThat(serialized, hasEntry("n", (Object)"node42"));
		
	}
}
