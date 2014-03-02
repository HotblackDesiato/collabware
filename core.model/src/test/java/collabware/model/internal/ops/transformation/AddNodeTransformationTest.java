package collabware.model.internal.ops.transformation;

import org.junit.Test;

public class AddNodeTransformationTest extends AbstractTransformationTest {
	
	@Test
	public void addNodeVsAddNode() throws Exception {
		transform( addNode("Node1") ).against( addNode("Node2") ).equals( addNode("Node1") );
		transform( addNode("Node2") ).against( addNode("Node3") ).equals( addNode("Node2") );
		transform( addNode("Node3") ).against( addNode("Node3") ).equals( id() );
		transform( addNode("Node1") ).against( addNode("Node1") ).equals( id() );
	}

	@Test
	public void addNodeVsRemNode() throws Exception {
		transform( addNode("Node4") ).against( remNode("Node5") ).equals( addNode("Node4") );
		transform( addNode("Node2") ).against( remNode("Node2") ).collides();
	}
	
	@Test
	public void addNodeVsSetAttribute() throws Exception {
		transform( addNode("someNode") ).against( setAttr("otherNode", "attr", "foo", "bar") ).equals( addNode("someNode") );
		transform( addNode("theNode")  ).against( setAttr("theNode", "attr", "foo", "bar")   ).equals( addNode("theNode") );
	}
	
	@Test
	public void addNodeVsSetUnaryReference() throws Exception {
		transform( addNode("someNode") ).against( setRef("otherNode", "ref", "node42", "node23") ).equals( addNode("someNode") );
		transform( addNode("theNode")  ).against( setRef("theNode", "ref", "node42", "node23")   ).equals( addNode("theNode") );
	}
	@Test
	public void addNodeVsNoOp() throws Exception {
		transform( addNode("someNode") ).against( id() ).equals( addNode("someNode") );
	}


}
