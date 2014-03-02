package collabware.model.internal.ops.transformation;

import org.junit.Test;

public class SetReferenceTransformationTest extends AbstractTransformationTest {
	
	@Test
	public void setRefVsAddNode() throws Exception {
		transform( setRef("someNode", "ref", null, "target") ).against( addNode("someOtherNode") ).equals( setRef("someNode", "ref", null, "target") );
	}
	
	@Test
	public void setRefVsRemNode() throws Exception {
		transform( setRef("someNode", "ref", null, "target")    ).against( remNode("someOtherNode") ).equals( setRef("someNode", "ref", null, "target") );
		transform( setRef("someNode", "ref", "oldTarget", null) ).against( remNode("someOtherNode") ).equals( setRef("someNode", "ref", "oldTarget", null) );
		transform( setRef("someNode", "ref", "oldTarget", null) ).against( remNode("oldTarget")     ).equals( setRef("someNode", "ref", "oldTarget", null) );
		transform( setRef("theNode", "ref", null, "target")     ).against( remNode("theNode")       ).collides();
		transform( setRef("someNode", "ref", null, "theTarget") ).against( remNode("theTarget")     ).collides();
	}
	
	@Test
	public void setRefVsSetAttr() throws Exception {
		transform( setRef("someNode", "ref", null, "target")      ).against( setAttr("someOtherNode", "attr", "foo", "bar") ).equals( setRef("someNode", "ref", null, "target") );		
		transform( setRef("theNode", "refOrAttr", null, "target") ).against( setAttr("theNode", "refOrAttr", "foo", "bar")  ).equals( setRef("theNode", "refOrAttr", null, "target") );		
	}
	
	@Test
	public void setRefVsSetRef() throws Exception {
		transform( setRef("someNode", "ref", null, "target")       ).against( setRef("someOtherNode", "otherRef", "foo", "bar")       ).equals( setRef("someNode", "ref", null, "target") );		
		transform( setRef("someNode", "ref", null, "target")       ).against( setRef("someOtherNode", "ref", "foo", "bar")            ).equals( setRef("someNode", "ref", null, "target") );		
		transform( setRef("theNode", "ref", "oldTarget", "target") ).against( setRef("theNode", "otherRef", "foo", "bar")             ).equals( setRef("theNode", "ref", "oldTarget", "target") );		
		transform( setRef("theNode", "ref", "oldTarget", "target") ).against( setRef("theNode", "ref", "oldTarget", "otherNewTarget") ).collides();		
		transform( setRef("theNode", "ref", "oldTarget", null)     ).against( setRef("theNode", "ref", "oldTarget", "otherNewTarget") ).collides();		
		transform( setRef("theNode", "ref", "oldTarget", "target") ).against( setRef("theNode", "ref", "oldTarget", null)             ).collides();		
		transform( setRef("theNode", "ref", "oldTarget", "target") ).against( setRef("theNode", "ref", "oldTarget", "target")         ).equals( id() );		
		transform( setRef("theNode", "ref", "oldTarget", null)     ).against( setRef("theNode", "ref", "oldTarget", null)             ).equals( id() );		
		transform( setRef("theNode", "ref", null, "target")        ).against( setRef("theNode", "ref", null, "target")                ).equals( id() );		
	}
	@Test
	public void setRefVsNoOp() throws Exception {
		transform( setRef("someNode", "ref", null, "target") ).against( id() ).equals( setRef("someNode", "ref", null, "target") );
	}
}
