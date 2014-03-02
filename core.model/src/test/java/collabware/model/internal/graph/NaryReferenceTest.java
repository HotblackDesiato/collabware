package collabware.model.internal.graph;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.graph.exception.NodeRemovedException;

public class NaryReferenceTest {
	
	private ModifyableGraph g = new GraphImpl();
	
	@Test
	public void addReference() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		
		source.getNaryReferences().add("ref", 0, target);
		
		assertTrue(source.getNaryReferences().getAll().contains("ref"));
		assertThat(source.getNaryReferences().get("ref").get(0), is(target));
	}

	@Test(expected = NodeRemovedException.class)
	public void cannotAddReferenceToNodeFromOtherGraph() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = new GraphImpl().addNode("n2");
		
		source.getNaryReferences().add("ref", 0, target);
	}

	@Test
	public void addReferenceSetsReferencePointingBack() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		
		source.getNaryReferences().add("ref", 0, target);
		
		assertTrue(target.getNaryReferences().getReverseReferences().contains("ref"));
		assertTrue(target.getNaryReferences().getSourcesOf("ref").contains(source));
	}

	@Test
	public void addReferenceAfterExistingReference() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target1 = g.addNode("n2");
		ModifyableNode target2 = g.addNode("n3");
		source.getNaryReferences().add("ref", 0, target1);
		
		source.getNaryReferences().add("ref", 1, target2);
		
		assertTrue(source.getNaryReferences().getAll().contains("ref"));
		assertThat(source.getNaryReferences().get("ref").get(0), is(target1));
		assertThat(source.getNaryReferences().get("ref").get(1), is(target2));
	}
	
	@Test
	public void addReferenceBeforeExistingReference() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target1 = g.addNode("n2");
		ModifyableNode target2 = g.addNode("n3");
		source.getNaryReferences().add("ref", 0, target1);
		
		source.getNaryReferences().add("ref", 0, target2);
		
		assertTrue(source.getNaryReferences().getAll().contains("ref"));
		assertThat(source.getNaryReferences().get("ref").get(0), is(target2));
		assertThat(source.getNaryReferences().get("ref").get(1), is(target1));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void addingReferenceOutOfBoundsThrowsException() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		
		source.getNaryReferences().add("ref", 1, target);
	}
	
	@Test
	public void removingLastTargetNodeAlsoRemovesReference() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getNaryReferences().add("ref", 0, target);
		
		source.getNaryReferences().remove("ref", 0);
		
		assertTrue(!source.getNaryReferences().getAll().contains("ref"));
		assertTrue(source.getNaryReferences().get("ref").isEmpty());
	}

	@Test
	public void removingTargetNode() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target1 = g.addNode("n2");
		ModifyableNode target2 = g.addNode("n3");
		source.getNaryReferences().add("ref", 0, target1);
		source.getNaryReferences().add("ref", 1, target2);
		
		source.getNaryReferences().remove("ref", 0);
		
		assertTrue(source.getNaryReferences().getAll().contains("ref"));
		assertThat(source.getNaryReferences().get("ref").size(), is(1));
		assertThat(source.getNaryReferences().get("ref").get(0), is(target2));
	}
	@Test
	public void removingTargetNodeRemovesReverseReference() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getNaryReferences().add("ref", 0, target);
		
		source.getNaryReferences().remove("ref", 0);
		
		assertTrue(!target.getNaryReferences().getReverseReferences().contains("ref"));
		assertThat(target.getNaryReferences().getSourcesOf("ref").size(), is(0));
	}
	
	@Test
	public void removeNodeRemovesReferences() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target = g.addNode("n2");
		source.getNaryReferences().add("ref", 0, target);
		
		source.remove();
		
		assertThat(source.getNaryReferences().get("ref"), not(hasItem(target)));
		assertThat(target.getNaryReferences().getSourcesOf("ref"), not(hasItem(source)));
	}

	@Test
	public void removeNodeRemovesReverseReferences() throws Exception {
		ModifyableNode source = g.addNode("n1");
		ModifyableNode target1 = g.addNode("n2");
		ModifyableNode target2 = g.addNode("n3");
		source.getNaryReferences().add("ref", 0, target1);
		source.getNaryReferences().add("ref", 1, target2);
		
		target1.remove();
		
		assertThat(source.getNaryReferences().get("ref"), not(hasItem(target1)));
		assertThat(source.getNaryReferences().get("ref"), hasItem(target2));
		assertThat(target1.getNaryReferences().getSourcesOf("ref"), not(hasItem(source)));
	}
}


