package collabware.model.internal.ops.transformation;

import org.junit.Test;

public class RemRefTransformationTest extends AbstractTransformationTest {

	@Test
	public void remRefVsAddRef() throws Exception {
		transform( remRef("Node1", "ref", 0, "Node2") ).against( addRef("OtherNode", "ref", 0, "Node3") ).equals( remRef("Node1", "ref", 0, "Node2") );
		transform( remRef("Node1", "ref", 0, "Node2") ).against( addRef("Node1", "otherRef", 0, "Node3") ).equals( remRef("Node1", "ref", 0, "Node2") );
		
		transform( remRef("Node1", "ref", 0, "Node2") ).against( addRef("Node1", "ref", 1, "Node3") ).equals( remRef("Node1", "ref", 0, "Node2") );
		transform( remRef("Node1", "ref", 1, "Node2") ).against( addRef("Node1", "ref", 0, "Node3") ).equals( remRef("Node1", "ref", 2, "Node2") );
		transform( remRef("Node1", "ref", 1, "Node2") ).against( addRef("Node1", "ref", 1, "Node3") ).equals( remRef("Node1", "ref", 2, "Node2") );
	}
	
	@Test
	public void remRefVsRemRef() throws Exception {
		transform( remRef("Node1", "ref", 0, "Node2") ).against( remRef("OtherNode", "ref", 0, "Node3") ).equals( remRef("Node1", "ref", 0, "Node2") );
		transform( remRef("Node1", "ref", 0, "Node2") ).against( remRef("Node1", "otherRef", 0, "Node3") ).equals( remRef("Node1", "ref", 0, "Node2") );

		transform( remRef("Node1", "ref", 0, "Node2") ).against( remRef("Node1", "ref", 1, "Node3") ).equals( remRef("Node1", "ref", 0, "Node2") );
		transform( remRef("Node1", "ref", 1, "Node2") ).against( remRef("Node1", "ref", 0, "Node3") ).equals( remRef("Node1", "ref", 0, "Node2") );
		transform( remRef("Node1", "ref", 1, "Node2") ).against( remRef("Node1", "ref", 1, "Node3") ).collides();
	}
	
	@Test
	public void remRefVsRemNode() {
		transform( remRef("Node1", "ref", 0, "Node2") ).against( remNode("otherNode") ).equals( remRef("Node1", "ref", 0, "Node2") );
		transform( remRef("Node1", "ref", 0, "Node2") ).against( remNode("Node1") ).collides();
		transform( remRef("Node1", "ref", 0, "Node2") ).against( remNode("Node2") ).collides();
	}
	
	@Test
	public void addRefVsNop() {
		transform( remRef("Node1", "ref", 0, "Node2") ).against( id() ).equals( remRef("Node1", "ref", 0, "Node2") );
	}
	
}
