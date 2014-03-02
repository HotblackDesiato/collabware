package collabware.model.internal.ops.transformation;

import org.junit.Test;

public class RemoveNodeTransformationTest extends AbstractTransformationTest {
	
	@Test
	public void remNodeVsAddNode() throws Exception {
		transform( remNode("Node1") ).against( addNode("Node2") ).equals( remNode("Node1") );
		transform( remNode("Node1") ).against( addNode("Node1") ).collides();
	}
	
	@Test
	public void remNodeVsRemNode() throws Exception {
		transform( remNode("Node1") ).against( remNode("Node1") ).equals( id() );
		transform( remNode("Node1") ).against( remNode("Node2") ).equals( remNode("Node1") );
	}
	
	@Test
	public void remNodeVsSetAttr() throws Exception {
		transform( remNode("someNode") ).against( setAttr("someOtherNode", "attr", "foo", "bar") ).equals( remNode("someNode") );
		transform( remNode("theNode") ).against( setAttr("theNode", "attr", "foo", "bar") ).collides();
		transform( remNode("theNode") ).against( setAttr("theNode", "attr", null, "bar") ).collides();
		transform( remNode("theNode") ).against( setAttr("theNode", "attr", "foo", null) ).equals( remNode("theNode") );
	}
	
	@Test
	public void remNodeVsSetRef() throws Exception {
		transform( remNode("someNode")).against( setRef("someOtherNode", "ref", "node42", "node23") ).equals( remNode("someNode") );		
		transform( remNode("theNode") ).against( setRef("theNode", "ref",       "node42", "node23") ).collides();
		transform( remNode("theNode") ).against( setRef("theNode", "ref",       null,  "node23") ).collides();
		transform( remNode("theNode") ).against( setRef("someNode", "ref",      null,  "theNode") ).collides();
		transform( remNode("theNode") ).against( setRef("theNode", "ref",       "node42", null) ).equals( remNode("theNode") );
	}
	
	@Test
	public void remNodeVsAddRef() throws Exception {
		transform( remNode("someNode")).against( addRef("someOtherNode", "ref", 0, "node23") ).equals( remNode("someNode") );		
		transform( remNode("theNode") ).against( addRef("theNode", "ref", 0, "node23") ).collides();
		transform( remNode("theNode") ).against( addRef("someNode", "ref", 0, "theNode") ).collides();
	}
	
	@Test
	public void remNodeVsRemRef() throws Exception {
		transform( remNode("someNode")).against( remRef("someOtherNode", "ref", 0, "node23") ).equals( remNode("someNode") );		
		transform( remNode("theNode") ).against( remRef("theNode", "ref", 0, "node23") ).collides();
		transform( remNode("theNode") ).against( remRef("someNode", "ref", 0, "theNode") ).collides();
	}
	
	@Test
	public void addNodeVsNoOp() throws Exception {
		transform( remNode("someNode") ).against( id() ).equals( remNode("someNode") );
	}
}
