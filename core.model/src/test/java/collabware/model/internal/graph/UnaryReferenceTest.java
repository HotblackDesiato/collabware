package collabware.model.internal.graph;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.graph.exception.NodeRemovedException;

public class UnaryReferenceTest {

	@Test
	public void setUnaryReference() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		
		source.getUnaryReferences().set("ref", target);
		
		assertEquals(target, source.getUnaryReferences().get("ref"));
		assertTrue(target.getUnaryReferences().sourcesOf("ref").contains(source));
	}

	@Test(expected=NodeRemovedException.class)
	public void setUnaryReferenceToRemovedNode() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode removedNode = g.addNode("n2");
		
		g.detach(removedNode);
		source.getUnaryReferences().set("ref", removedNode);
	}

	@Test(expected=NodeRemovedException.class)
	public void setUnaryReferenceOfRemovedNode() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode removedNode = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		
		removedNode.detach();		
		removedNode.getUnaryReferences().set("ref", target);
	}
	
	@Test
	public void setUnaryReferenceToNull() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getUnaryReferences().set("ref", target);
		
		source.getUnaryReferences().set("ref", null);
		
		assertNull(source.getUnaryReferences().get("ref"));
		assertFalse(target.getUnaryReferences().sourcesOf("ref").contains(source));
	}
	
	@Test
	public void getUnaryReferences() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getUnaryReferences().set("ref", target);
		source.getUnaryReferences().set("otherRef", target);
		
		Set<String> refs = source.getUnaryReferences().getAll();
		
		assertEquals(2, refs.size());
		assertTrue(refs.contains("ref"));
		assertTrue(refs.contains("otherRef"));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void getUnaryReferencesIsUnmodifyable() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode n1 = g.addNode("n1");
		ModifyableNode n2 = g.addNode("n2");
		n1.getUnaryReferences().set("ref", n2);
		n1.getUnaryReferences().set("otherRef", n2);
		
		Set<String> refs = n1.getUnaryReferences().getAll();
		refs.remove("ref");
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void reverseUnaryReferenceIsUnmodifyable() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getUnaryReferences().set("ref", target);
		
		Collection<ModifyableNode> refs = target.getUnaryReferences().sourcesOf("ref");
		refs.remove(source);
	}

	@Test
	public void getReverseUnaryReferences() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getUnaryReferences().set("ref", target);
		source.getUnaryReferences().set("otherRef", target);
		
		Set<String> reverseRefs = target.getUnaryReferences().getReverseUnaryReferences();
		
		assertEquals(2, reverseRefs.size());
		assertTrue(reverseRefs.contains("ref"));
		assertTrue(reverseRefs.contains("otherRef"));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void reverseUnaryReferencesIsUnmodifyable() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getUnaryReferences().set("ref", target);
		
		Set<String> refs = target.getUnaryReferences().getReverseUnaryReferences();
		refs.remove("ref");
	}
	
	@Test
	public void removeNodeUnsetsReferences() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getUnaryReferences().set("ref", target);
		
		source.remove();
		assertThat(source.getUnaryReferences().get("ref"), is(nullValue()));
		assertThat(target.getUnaryReferences().sourcesOf("ref"), not(hasItem(source)));
	}

	@Test
	public void removeNodeUnsetsTargetReferences() throws Exception {
		ModifyableGraph g = new GraphImpl();
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getUnaryReferences().set("ref", target);
		
		target.remove();
		assertThat(source.getUnaryReferences().get("ref"), is(nullValue()));
		assertThat(target.getUnaryReferences().sourcesOf("ref"), not(hasItem(source)));
	}
	
}
