package collabware.model.internal.graph;

import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;

public class AbstractNodeTest {

	protected static final String SOME_ID = "someId";
	protected static final String SOME_VALUE = "someValue";
	protected static final String SOME_ATTR = "someAttr";

	protected ModifyableNode createNodeWith(String id) {
		ModifyableGraph graph = new GraphImpl();
		ModifyableNode node = graph.addNode(id);
		return node;
	}

}