package collabware.model.internal.ops.transformation;

import org.junit.Test;

public class AddReferenceTransformationTest extends AbstractTransformationTest {

	@Test
	public void addRefVsAddRef() throws Exception {
		transform( addRef("Node1", "ref", 0, "Node2") ).against( addRef("Node1", "ref", 0, "Node2") ).equals( id() );
		transform( addRef("Node1", "ref", 0, "Node2") ).against( addRef("Node1", "ref", 1, "Node2") ).collides();
		transform( addRef("Node1", "ref", 0, "Node2") ).against( addRef("Node1", "someOtherRef", 0, "Node2") ).equals( addRef("Node1", "ref", 0, "Node2") );
		transform( addRef("Node1", "ref", 0, "Node2") ).against( addRef("SomeOtherNode", "ref", 0, "Node2") ).equals( addRef("Node1", "ref", 0, "Node2") );
		
		transform( addRef("Node1", "ref", 0, "Node2") ).against( addRef("Node1", "ref", 1, "Node3") ).equals( addRef("Node1", "ref", 0, "Node2") );
		transform( addRef("Node1", "ref", 1, "Node2") ).against( addRef("Node1", "ref", 0, "Node3") ).equals( addRef("Node1", "ref", 2, "Node2") );
		transform( addRef("Node1", "ref", 1, "Node2") ).against( addRef("Node1", "ref", 1, "Node3") ).equals( addRef("Node1", "ref", 2, "Node2") );
		transform( addRef("Node1", "ref", 1, "Node3") ).against( addRef("Node1", "ref", 1, "Node2") ).equals( addRef("Node1", "ref", 1, "Node3") );
	}
	
	@Test
	public void addRefVsRemRef() throws Exception {
		transform( addRef("Node1", "ref", 0, "Node2") ).against( remRef("OtherNode", "ref", 0, "Node3") ).equals( addRef("Node1", "ref", 0, "Node2") );
		transform( addRef("Node1", "ref", 0, "Node2") ).against( remRef("Node1", "otherRef", 0, "Node3") ).equals( addRef("Node1", "ref", 0, "Node2") );
		
		transform( addRef("Node1", "ref", 0, "Node2") ).against( remRef("Node1", "ref", 1, "Node3") ).equals( addRef("Node1", "ref", 0, "Node2") );
		transform( addRef("Node1", "ref", 1, "Node2") ).against( remRef("Node1", "ref", 0, "Node3") ).equals( addRef("Node1", "ref", 0, "Node2") );
		transform( addRef("Node1", "ref", 1, "Node2") ).against( remRef("Node1", "ref", 1, "Node3") ).equals( addRef("Node1", "ref", 1, "Node2") );
	}
	
	@Test
	public void addRefVsRemNode() {
		transform( addRef("Node1", "ref", 0, "Node2") ).against( remNode("OtherNode") ).equals( addRef("Node1", "ref", 0, "Node2") );
		transform( addRef("Node1", "ref", 0, "Node2") ).against( remNode("Node1") ).collides();
		transform( addRef("Node1", "ref", 0, "Node2") ).against( remNode("Node2") ).collides();
	}
	
	@Test
	public void addRefVsNop() {
		transform( addRef("Node1", "ref", 0, "Node2") ).against( id() ).equals( addRef("Node1", "ref", 0, "Node2") );
	}
	
}
