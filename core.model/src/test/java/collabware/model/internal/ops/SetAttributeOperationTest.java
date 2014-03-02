package collabware.model.internal.ops;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import collabware.api.operations.OperationApplicationException;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AttributeValueMismatchException;
import collabware.model.internal.ops.SetAttributeOperation;

public class SetAttributeOperationTest {
	
	private final ModelProvider modelProvider = new ModelProviderImpl();
	
	@Test
	public void createWithOldAndNewValue() throws Exception {
		SetAttributeOperation setAttribute = new SetAttributeOperation("node1", "attr", "old", "new");
		
		assertEquals("node1", setAttribute.getNodeId());
		assertEquals("attr", setAttribute.getAttributeName());
		assertEquals("old", setAttribute.getOldValue());
		assertEquals("new", setAttribute.getNewValue());
	}
	
	@Test
	public void createWithNoNewValue() throws Exception {
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "attr2", "oldValue", null);
		
		assertEquals("node2", setAttribute.getNodeId());
		assertEquals("attr2", setAttribute.getAttributeName());
		assertEquals("oldValue", setAttribute.getOldValue());
		assertNull(setAttribute.getNewValue());
	}
	
	@Test
	public void createWithNoOldNewValue() throws Exception {
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "attr2", null, "theNewValue");
		
		assertEquals("node2", setAttribute.getNodeId());
		assertEquals("attr2", setAttribute.getAttributeName());
		assertNull(setAttribute.getOldValue());
		assertEquals("theNewValue", setAttribute.getNewValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void attibuteNameMustNotBeNull() throws Exception {
		new SetAttributeOperation("node2", null, "theOldValue", "theNewValue");
	}

	@Test
	public void applyToSetUndefinedAttribute() throws Exception {
		ModifyableModel model = givenModel("n1");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("n1", "attr", null, "theNewValue");
		setAttribute.apply(model);
		
		assertThat((String)model.getGraph().getNode("n1").getAttributes().get("attr"), is("theNewValue"));
	}

	private ModifyableModel givenModel(String literal) {
		return (ModifyableModel) modelProvider.createModelFromLiteral("", literal);
	}

	@Test
	public void applyToSetAttribute() throws Exception {
		ModifyableModel model = givenModel("node2, node2.foo='bar'");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", "bar", "baz");
		setAttribute.apply(model);
		
		assertThat((String)model.getGraph().getNode("node2").getAttributes().get("foo"), is("baz"));
	}

	@Test
	public void applyToSetAttributeToNumber() throws Exception {
		ModifyableModel model = givenModel("node2, node2.foo=23");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", 23, 42);
		setAttribute.apply(model);
		
		assertThat((Integer)model.getGraph().getNode("node2").getAttributes().get("foo"), is(42));
	}
	
	@Test
	public void applyToSetAttributeToBoolean() throws Exception {
		ModifyableModel model = givenModel("node2, node2.foo=false");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", false, true);
		setAttribute.apply(model);
		
		assertThat((Boolean)model.getGraph().getNode("node2").getAttributes().get("foo"), is(true));
	}

	@Test
	public void applyToSetAttributeToUndefined() throws Exception {
		ModifyableModel model = givenModel("node2, node2.foo='bar'");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", "bar", null);
		setAttribute.apply(model);
		
		assertThat(model.getGraph().getNode("node2").getAttributes().get("foo"), is(nullValue()));
	}

	@Test(expected=AttributeValueMismatchException.class)
	public void applyWithMismatchingAttributeValues() throws Exception {
		ModifyableModel model = givenModel("node2, node2.foo='xxx'");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", "bar", "blah");
		setAttribute.apply(model);
	}

	@Test(expected=AttributeValueMismatchException.class)
	public void applyWithMismatchingAttributeValuesWithNull() throws Exception {
		ModifyableModel model = givenModel("node2");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", "bar", "Blah");
		setAttribute.apply(model);
	}
	
	@Test(expected=AttributeValueMismatchException.class)
	public void applyWithMismatchingAttributeValuesWithNull2() throws Exception {
		ModifyableModel model = givenModel("node2, node2.foo='bar'");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", null, "Blah");
		setAttribute.apply(model);
	}

	@Test(expected=OperationApplicationException.class)
	public void failDueToMissingNode() throws Exception {
		ModifyableModel model = givenModel("someOtherNode");
		
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", null, "Blah");
		setAttribute.apply(model);
	}

	@Test
	public void inverse() throws Exception {
		SetAttributeOperation setAttribute = new SetAttributeOperation("someNode", "someAttr", "theOldValue", "theNewValue");
		SetAttributeOperation inverse = setAttribute.inverse();
		
		assertEquals(setAttribute.getNodeId(), inverse.getNodeId());
		assertEquals(setAttribute.getAttributeName(), inverse.getAttributeName());
		assertEquals(setAttribute.getNewValue(), inverse.getOldValue());
		assertEquals(setAttribute.getOldValue(), inverse.getNewValue());
	}
	
	@Test
	public void serialize() {
		SetAttributeOperation setAttribute = new SetAttributeOperation("node2", "foo", 23, 42);
		Map<String, Object> serialized = setAttribute.serialize();
		
		assertThat(serialized, hasEntry("t", (Object)"sa"));
		assertThat(serialized, hasEntry("n", (Object)setAttribute.getNodeId()));
		assertThat(serialized, hasEntry("a", (Object)setAttribute.getAttributeName()));
		assertThat(serialized, hasEntry("nv", (Object)setAttribute.getNewValue()));
		assertThat(serialized, hasEntry("ov", (Object)setAttribute.getOldValue()));
	}
}
