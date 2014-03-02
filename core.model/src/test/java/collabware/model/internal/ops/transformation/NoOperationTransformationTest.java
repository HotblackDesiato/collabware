package collabware.model.internal.ops.transformation;

import org.junit.Test;

public class NoOperationTransformationTest extends AbstractTransformationTest {
		
	@Test
	public void NoOpVsNoOp() throws Exception {
		transform( id() ).against( id() ).equals( id() );
	}

	@Test
	public void NoOpVsAddNode() throws Exception {
		transform( id() ).against( addNode("someNode") ).equals( id() );
	}
	@Test
	public void NoOpVsRemNode() throws Exception {
		transform( id() ).against( remNode("someNode") ).equals( id() );
	}
	@Test
	public void NoOpVsSetAttr() throws Exception {
		transform( id() ).against( setAttr("someNode", "attr", null, "FooBar") ).equals( id() );
	}
	@Test
	public void NoOpVsSetRef() throws Exception {
		transform( id() ).against( setRef("someNode", "ref", null, "target") ).equals( id() );
	}
}
