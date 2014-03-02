package collabware.model.internal.ops.transformation;

import org.junit.Test;

public class SetAttributeTransformationTest extends AbstractTransformationTest {
	
	@Test
	public void setAttrVsAddNode() throws Exception {
		transform( setAttr("someNode", "attr", "foo", "Blah")).against(addNode("someOtherNode")).equals( setAttr("someNode", "attr", "foo", "Blah") );
		transform( setAttr("theNode", "attr", "foo", "Blah")).against(addNode("theNode")).equals( setAttr("theNode", "attr", "foo", "Blah") );
	}
	
	@Test
	public void setAttrVsRemNode() throws Exception {
		transform( setAttr("someNode", "attr", "foo", "Blah") ).against( remNode("someOtherNode") ).equals( setAttr("someNode", "attr", "foo", "Blah") );
		transform( setAttr("theNode", "attr",  "foo", "Blah") ).against( remNode("theNode")       ).collides();
	}
	
	@Test
	public void setAttrVsSetAttr() throws Exception {
		transform( setAttr("someNode", "attr", "foo", "Blah") ).against(  setAttr("someOtherNode", "attr", "42", "23")    ).equals( setAttr("someNode", "attr", "foo", "Blah") );
		transform( setAttr("theNode", "attr", "foo", "Blah")  ).against(  setAttr("theNode", "someOtherAttr", "42", "23") ).equals( setAttr("theNode", "attr", "foo", "Blah") );
		transform( setAttr("theNode", "theAttr", "foo", "Blah")).against( setAttr("theNode", "theAttr", "42", "23")       ).collides();
		transform( setAttr("theNode", "theAttr", "foo", null) ).against(  setAttr("theNode", "theAttr", "42", "23")       ).collides();
		transform( setAttr("theNode", "theAttr", "foo", "bar")).against(  setAttr("theNode", "theAttr", "42", null)       ).collides();
		transform( setAttr("theNode", "attr", "foo", "Blah")  ).against(  setAttr("theNode", "attr", "foo", "Blah")       ).equals( id() );
		transform( setAttr("theNode", "attr", "foo", null)    ).against(  setAttr("theNode", "attr", "foo", null)         ).equals( id() );
		transform( setAttr("theNode", "attr", null, "foo")    ).against(  setAttr("theNode", "attr", null, "foo")         ).equals( id() );
	}
	
	@Test
	public void setAttrVsSetRef() throws Exception {
		transform( setAttr("someNode", "attr", "foo", "Blah")   ).against( setRef("someOtherNode", "ref", "Node42", "Node23") ).equals( setAttr("someNode", "attr", "foo", "Blah") );
		transform( setAttr("theNode", "attr", "foo", "Blah")    ).against( setRef("theNode", "ref", "Node42", "Node23")       ).equals( setAttr("theNode", "attr", "foo", "Blah") );
		transform( setAttr("theNode", "theName", "foo", "Blah") ).against( setRef("theNode", "theName", "Node42", "Node23")   ).equals( setAttr("theNode", "theName", "foo", "Blah") );
	}
	
	@Test
	public void setAttrVsNoOp() throws Exception {
		transform( setAttr("someNode", "attr", "foo", "Blah")).against(id()).equals( setAttr("someNode", "attr", "foo", "Blah") );
	}
}
