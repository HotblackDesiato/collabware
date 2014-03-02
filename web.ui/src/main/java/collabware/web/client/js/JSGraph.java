package collabware.web.client.js;

import java.util.Collection;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.graph.exception.NoSuchNodeException;

@Export
public class JSGraph implements Exportable{
	private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+#"; 
	private static String createId(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(alphabet.charAt((int) Math.floor(Math.random() * alphabet.length())));
		}
		return sb.toString();
	}

	private ModifyableGraph graph;
	private final String graphId;
	
	public JSGraph(ModifyableGraph graph) {
		this.graph = graph;
		this.graphId = createId(3);
	}

	/**
	 * Returns a node by ID.
	 * @param id
	 * @return the node for a given Id or undefined
	 */
	@Export
	public JSNode node(String id) {
		try {
			return wrap(graph.getNode(id));
		} catch (NoSuchNodeException e) {
			return null;
		}
	}

	/**
	 * Returns the root node of the graph.
	 * @return the root node of the graph.
	 */
	@Export
	public JSNode root() {
		return wrap(graph.getRootNode());
	}

	private JSNode wrap(ModifyableNode node) {
		return new JSNode(node);
	}
	
	/**
	 * Adds a node with a given ID.
	 * @param id
	 * @return the node.
	 */
	@Export
	public JSNode addNode(String id) {
		return wrap(graph.addNode(id));
	}
	
	/**
	 * Adds a node with a random ID.
	 * @return the node.
	 */
	@Export
	public JSNode addNode() {
		return wrap(graph.addNode(graphId +"_"+createId(6)));
	}
	
	/**
	 * Removes a node.
	 * @param n the node to be removed.
	 */
	@Export
	public void remove(JSNode n) {
		graph.remove(n.getWrappedNode());
	}
	
	/**
	 * Return all nodes in the graph
	 * @return all nodes in the graph.
	 */
	@Export
	public JSNode[] nodes() {
		Collection<ModifyableNode> nodes = graph.getNodes();
		JSNode[] nodeWrappers = new JSNode[nodes.size()];
		int i = 0;
		for (ModifyableNode node : nodes) {
			nodeWrappers[i] = wrap(node);
			i++;
		}
		return nodeWrappers;
	}
	
}
