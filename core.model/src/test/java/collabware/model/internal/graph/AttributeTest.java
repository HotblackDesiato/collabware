package collabware.model.internal.graph;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import collabware.model.graph.ModifyableNode;
import collabware.model.internal.graph.exception.NodeRemovedException;

public class AttributeTest extends AbstractNodeTest {
	
	@Test
	public void setStringValuedAttribute() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		
		node.getAttributes().set(SOME_ATTR, SOME_VALUE);
		
		assertEquals(SOME_VALUE, node.getAttributes().get(SOME_ATTR));
		assertTrue(node.getAttributes().getAll().contains(SOME_ATTR));
	}
	
	@Test(expected=NodeRemovedException.class)
	public void setStringValuedAttributeOnRemovedNode() throws Exception {
		ModifyableNode removedNode = createNodeWith(SOME_ID);
		removedNode.detach();
		
		removedNode.getAttributes().set(SOME_ATTR, SOME_VALUE);
	}

	
	@Test
	public void setStringValuedAttributeToNull() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		node.getAttributes().set(SOME_ATTR, SOME_VALUE);
		
		node.getAttributes().set(SOME_ATTR, (String)null);
		
		assertNull(node.getAttributes().get(SOME_ATTR));
		assertFalse(node.getAttributes().getAll().contains(SOME_ATTR));
	}
	
	@Test
	public void setNumberValuedAttribute() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
	
		node.getAttributes().set("someNumberAttr", 42);
		
		assertEquals(42, node.getAttributes().get("someNumberAttr"));
		assertTrue(node.getAttributes().getAll().contains("someNumberAttr"));
	}
	
	@Test
	public void setNumberValuedAttributeToNull() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		node.getAttributes().set(SOME_ATTR, SOME_VALUE);
		
		node.getAttributes().set(SOME_ATTR, (Number)null);
		
		assertNull(node.getAttributes().get(SOME_ATTR));
		assertFalse(node.getAttributes().getAll().contains(SOME_ATTR));
	}
	
	@Test(expected=NodeRemovedException.class)
	public void setNumberValuedAttributeOnRemovedNode() throws Exception {
		ModifyableNode removedNode = createNodeWith(SOME_ID);
		removedNode.detach();
		
		removedNode.getAttributes().set("someNumberAttr", 42);
	}
	
	@Test
	public void setBooleanAttributeToTrue() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		node.getAttributes().set(SOME_ATTR, true);
		
		assertEquals(true, node.getAttributes().get(SOME_ATTR));
	}
	@Test
	public void setBooleanAttributeToFalse() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		node.getAttributes().set(SOME_ATTR, false);
		
		assertEquals(false, node.getAttributes().get(SOME_ATTR));
	}
	@Test
	public void setBooleanAttributeToNull() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		node.getAttributes().set(SOME_ATTR, (Boolean)null);
		
		assertNull(node.getAttributes().get(SOME_ATTR));
	}
	
	@Test
	public void getAttributes() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		node.getAttributes().set("baz", 42);
		node.getAttributes().set("foo", "bar");
		
		Collection<String> attributes = node.getAttributes().getAll();
		
		assertEquals(2, attributes.size());
		assertTrue(attributes.contains("baz"));
		assertTrue(attributes.contains("foo"));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void getAttributesIsUnmodifyable() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		node.getAttributes().set("baz", 42);
		node.getAttributes().set("foo", "bar");
		
		Collection<String> attributes = node.getAttributes().getAll();
		attributes.remove("baz");
	}
	
	@Test
	public void removeNodeUnsetsAttributes() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		node.getAttributes().set("baz", 42);
		
		node.remove();
		assertThat(node.getAttributes().get("baz"), is(nullValue()));
	}
	
}
