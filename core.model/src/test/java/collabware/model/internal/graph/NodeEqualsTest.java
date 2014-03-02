package collabware.model.internal.graph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;

public class NodeEqualsTest extends AbstractNodeTest {
	
	@Test
	public void nodeEqualToItself() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
	
		assertTrue(node.equals(node));		
	}
	
	@Test
	public void nodeNotEqualToNoneNode() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		
		assertFalse(node.equals(new Object()));		
	}
	
	@Test
	public void nodeEqualsNodeWithSameId() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		ModifyableNode otherNode = createNodeWith(SOME_ID);
		
		assertTrue(node.equals(otherNode));
		assertTrue(otherNode.equals(node));
	}
	
	@Test
	public void nodeNotEqualsNodeWithDifferentId() throws Exception {
		ModifyableNode node = createNodeWith("TheNode");
		ModifyableNode otherNode = createNodeWith("TheOtherNode");
		
		assertFalse(node.equals(otherNode));
		assertFalse(otherNode.equals(node));
	}
	
	@Test
	public void nodeEqualsNodeWithSameIdAndSameAttributeValues() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		ModifyableNode otherNode = createNodeWith(SOME_ID);

		node.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("attr", 42);
		
		assertTrue(node.equals(otherNode));
		assertTrue(otherNode.equals(node));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentAttributeValues() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		ModifyableNode otherNode = createNodeWith(SOME_ID);
		
		node.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("attr", "foo");
		
		assertFalse(node.equals(otherNode));
		assertFalse(otherNode.equals(node));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentAttributes() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		ModifyableNode otherNode = createNodeWith(SOME_ID);
		
		node.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("otherAttr", "foo");
		
		assertFalse(node.equals(otherNode));
		assertFalse(otherNode.equals(node));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndMoreAttributes() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		ModifyableNode otherNode = createNodeWith(SOME_ID);
		
		node.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("attr", 42);
		otherNode.getAttributes().set("otherAttr", "foo");
		
		assertFalse(node.equals(otherNode));
		assertFalse(otherNode.equals(node));
	}

	@Test
	public void nodeEqualsNodeWithSameIdAndSameReferenceTargetIds() throws Exception {
		ModifyableGraph graph = new GraphImpl();
		ModifyableGraph otherGraph = new GraphImpl();
		
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getUnaryReferences().set("ref", targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("targetNode");
		otherNode.getUnaryReferences().set("ref", otherTargetNode);
		

		assertTrue(node.equals(otherNode));
		assertTrue(otherNode.equals(node));
	}

	@Test
	public void nodeEqualsNodeWithSameIdAndSameNaryReferenceTargetIds() throws Exception {
		ModifyableGraph graph = new GraphImpl();
		ModifyableGraph otherGraph = new GraphImpl();
		
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getNaryReferences().add("ref", 0, targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("targetNode");
		otherNode.getNaryReferences().add("ref", 0, otherTargetNode);
		
		
		assertTrue(node.equals(otherNode));
		assertTrue(otherNode.equals(node));
	}

	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentReferenceTargetIds() throws Exception {
		ModifyableGraph graph = new GraphImpl();
		ModifyableGraph otherGraph = new GraphImpl();
		
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getUnaryReferences().set("ref", targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("someOtherTargetNode");
		otherNode.getUnaryReferences().set("ref", otherTargetNode);
		
		
		assertFalse(node.equals(otherNode));
		assertFalse(otherNode.equals(node));
	}
	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentNaryReferenceTargetIds() throws Exception {
		ModifyableGraph graph = new GraphImpl();
		ModifyableGraph otherGraph = new GraphImpl();
		
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getNaryReferences().add("ref", 0, targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("someOtherTargetNode");
		otherNode.getNaryReferences().add("ref", 0, otherTargetNode);
		
		
		assertFalse(node.equals(otherNode));
		assertFalse(otherNode.equals(node));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndDifferentReferences() throws Exception {
		ModifyableGraph graph = new GraphImpl();
		ModifyableGraph otherGraph = new GraphImpl();
		
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getUnaryReferences().set("ref", targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("targetNode");
		otherNode.getUnaryReferences().set("otherRef", otherTargetNode);
		
		assertFalse(node.equals(otherNode));
		assertFalse(otherNode.equals(node));
	}
	
	@Test
	public void nodeNotEqualsNodeWithSameIdAndMoreReferences() throws Exception {
		ModifyableGraph graph = new GraphImpl();
		ModifyableGraph otherGraph = new GraphImpl();
		
		ModifyableNode node = graph.addNode("theNode");
		ModifyableNode targetNode = graph.addNode("targetNode");
		node.getUnaryReferences().set("ref", targetNode);
		
		ModifyableNode otherNode = otherGraph.addNode("theNode");
		ModifyableNode otherTargetNode = otherGraph.addNode("targetNode");
		ModifyableNode someOtherTargetNode = otherGraph.addNode("anotherTargetNode");
		otherNode.getUnaryReferences().set("ref", otherTargetNode);
		otherNode.getUnaryReferences().set("otherRef", someOtherTargetNode);
		
		assertFalse(node.equals(otherNode));
		assertFalse(otherNode.equals(node));
	}
}
