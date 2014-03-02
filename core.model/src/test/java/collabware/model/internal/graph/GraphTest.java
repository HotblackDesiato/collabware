package collabware.model.internal.graph;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import collabware.api.document.DocumentException;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.graph.Node;
import collabware.model.internal.graph.exception.NoSuchNodeException;


public class GraphTest {
	private ModifyableGraph graph = new GraphImpl();
	private ModifyableGraph otherGraph = new GraphImpl();

	@Test
	public void getRootNode() throws Exception {
		
		ModifyableNode root = graph.getRootNode();
		
		assertTrue(graph.contains(root));
	}
	
	@Test
	public void addNode() throws Exception {
		
		ModifyableNode n = graph.addNode("0815");
		
		assertTrue(graph.contains(n));
		assertEquals(graph, n.getGraph());
	}
	
	@Test(expected=DocumentException.class)
	public void addDuplicateNodeId() throws Exception {
		graph.addNode("0815");
		
		graph.addNode("0815");	
	}
	
	@Test
	public void detachNode() throws Exception {
		ModifyableNode node = graph.addNode("0815");
		
		graph.detach(node);
		
		assertFalse(graph.contains(node));
		assertNull(node.getGraph());
	}
	
	@Test(expected=DocumentException.class)
	public void cannotDetachRootNode1() throws Exception {		
		graph.detach(graph.getRootNode());
	}

	@Test(expected=DocumentException.class)
	public void cannotDetachRootNode2() throws Exception {
		graph.getRootNode().detach();
	}
	
	@Test(expected=DocumentException.class)
	public void cannotDetachNodeWithAttributes() throws Exception {
		ModifyableNode nodeWithAttributes = graph.addNode("0815");
		nodeWithAttributes.getAttributes().set("attr", 42);
		
		graph.detach(nodeWithAttributes);
	}

	@Test(expected=DocumentException.class)
	public void cannotDetachNodeWithUnaryReferences() throws Exception {
		ModifyableNode referencingNode = graph.addNode("n1");
		ModifyableNode target = graph.addNode("n2");
		referencingNode.getUnaryReferences().set("ref", target);
		
		graph.detach(referencingNode);
	}
	
	@Test(expected=DocumentException.class)
	public void cannotDetachNodeWithNaryReferences() throws Exception {
		ModifyableNode referencingNode = graph.addNode("n1");
		ModifyableNode target = graph.addNode("n2");
		referencingNode.getNaryReferences().add("ref", 0, target);
		
		graph.detach(referencingNode);
	}

	@Test(expected=DocumentException.class)
	public void cannotDetachNodeWithReverseUnaryReferences() throws Exception {
		ModifyableNode source = graph.addNode("n1");
		ModifyableNode referencedNode = graph.addNode("n2");
		source.getUnaryReferences().set("ref", referencedNode);
		
		graph.detach(referencedNode);
	}
	
	@Test(expected=DocumentException.class)
	public void cannotDetachNodeWithReverseNaryReferences() throws Exception {
		ModifyableNode source = graph.addNode("n1");
		ModifyableNode referencedNode = graph.addNode("n2");
		source.getNaryReferences().add("ref", 0, referencedNode);
		
		graph.detach(referencedNode);
	}

	@Test
	public void removeNodeWithReferencesAndAttributes() throws Exception {
		ModifyableNode source = graph.addNode("n1");
		ModifyableNode referencedNode = graph.addNode("n2");
		source.getNaryReferences().add("ref", 0, referencedNode);
		source.getUnaryReferences().set("foo", referencedNode);
		source.getAttributes().set("baz", "foo");
		
		graph.remove(source);
		assertThat(graph.getNodes(), not(hasItem(source)));
	}
	@Test
	public void clearRemovesAllNodesExceptRootNode() throws Exception {
		ModifyableNode source = graph.addNode("n1");
		ModifyableNode referencedNode = graph.addNode("n2");
		source.getNaryReferences().add("ref", 0, referencedNode);
		source.getUnaryReferences().set("foo", referencedNode);
		source.getAttributes().set("baz", "foo");
		
		graph.clear();
		
		assertThat(graph.getNodes().size(), is(1));
		assertThat(graph.getNodes(), hasItem(graph.getRootNode()));
	}
	
	@Test
	public void getNode() throws Exception {
		Node theNode = graph.addNode("0815");
		
		assertEquals(theNode, graph.getNode("0815"));
	}
	
	@Test(expected=NoSuchNodeException.class)
	public void getNonexistingNode() throws Exception {
		graph.getNode("0815");
	}
	
	@Test
	public void getNodes() throws Exception {
		int numberOfNodes = graph.getNodes().size();
		Node theNode = graph.addNode("0815");
		Node theOtherNode = graph.addNode("42");
		
		Collection<ModifyableNode> nodes = graph.getNodes();
		
		assertEquals(numberOfNodes + 2, nodes.size());
		assertTrue(nodes.contains(theNode));
		assertTrue(nodes.contains(theOtherNode));
	}
	
	@Test
	public void emptyGraphsAreEqual() throws Exception {
		
		assertTrue(graph.equals(otherGraph));
		assertTrue(otherGraph.equals(graph));
	}
	
	@Test
	public void graphIsEqualToItself() throws Exception {
		assertTrue(graph.equals(graph));
	}
	
	@Test
	public void graphNoEqualToSomeNonGraph() throws Exception {
		assertFalse(graph.equals(new Object()));
	}
	
	@Test
	public void graphEqualToGraphWithSameNodeWithSameIds() throws Exception {
		graph.addNode("theNode");
		otherGraph.addNode("theNode");

		assertTrue(graph.equals(otherGraph));
		assertTrue(otherGraph.equals(graph));
	}
	
	@Test
	public void graphNotEqualToGraphWithSomeNodeWithDifferentIds() throws Exception {
		graph.addNode("theNode");
		otherGraph.addNode("someOtherNode");
		
		assertFalse(graph.equals(otherGraph));
		assertFalse(otherGraph.equals(graph));
	}
	
	@Test
	public void graphNotEqualToGraphWithNodeWithSameIdAndOtherNodes() throws Exception {
		graph.addNode("theNode");
		otherGraph.addNode("theNode");
		otherGraph.addNode("someOtherNode");
		
		assertFalse(graph.equals(otherGraph));
		assertFalse(otherGraph.equals(graph));
	}
	
	@Test
	public void nodeEqualsNodeWithSameIdAndSameAttributeValues() throws Exception {
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode otherNode = otherGraph.addNode("theNode");

		node.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("attr", 42);
		
		assertTrue(graph.equals(otherGraph));
		assertTrue(otherGraph.equals(graph));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentAttributeValues() throws Exception {
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		
		node.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("attr", "foo");
		
		assertFalse(graph.equals(otherGraph));
		assertFalse(otherGraph.equals(graph));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentAttributes() throws Exception {
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		
		node.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("otherAttr", "foo");
		
		assertFalse(graph.equals(otherGraph));
		assertFalse(otherGraph.equals(graph));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndMoreAttributes() throws Exception {
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		
		node.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("otherAttr", "foo");
		
		assertFalse(graph.equals(otherGraph));
		assertFalse(otherGraph.equals(graph));
	}

	@Test
	public void nodeEqualsNodeWithSameIdAndSameReferenceTargetIds() throws Exception {
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getUnaryReferences().set("ref", targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("targetNode");
		otherNode.getUnaryReferences().set("ref", otherTargetNode);
		
		assertTrue(graph.equals(otherGraph));
		assertTrue(otherGraph.equals(graph));
	}

	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentReferenceTargetIds() throws Exception {
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getUnaryReferences().set("ref", targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("someOtherTargetNode");
		otherNode.getUnaryReferences().set("ref", otherTargetNode);
		
		assertFalse(graph.equals(otherGraph));
		assertFalse(otherGraph.equals(graph));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentReferences() throws Exception {
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getUnaryReferences().set("ref", targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("targetNode");
		otherNode.getUnaryReferences().set("otherRef", otherTargetNode);
		
		assertFalse(graph.equals(otherGraph));
		assertFalse(otherGraph.equals(graph));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndMoreReferences() throws Exception {
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getUnaryReferences().set("ref", targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("targetNode");
		ModifyableNode someOtherTargetNode = otherGraph.addNode("anotherTargetNode");
		otherNode.getUnaryReferences().set("ref", otherTargetNode);
		otherNode.getUnaryReferences().set("otherRef", someOtherTargetNode);
		
		assertFalse(graph.equals(otherGraph));
		assertFalse(otherGraph.equals(graph));
	}
}
