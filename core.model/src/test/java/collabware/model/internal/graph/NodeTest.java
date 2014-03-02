package collabware.model.internal.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;

public class NodeTest extends AbstractNodeTest {
	
	@Test
	public void getId() throws Exception {
		ModifyableNode node = createNodeWith(SOME_ID);
		
		assertEquals(SOME_ID, node.getId());
	}

	@Test
	public void getGraph() throws Exception {
		ModifyableGraph graph = new GraphImpl();
		ModifyableNode node = graph.addNode(SOME_ID);
		
		assertEquals(graph, node.getGraph());
	}
	

}
