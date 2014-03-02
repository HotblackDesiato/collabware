package collabware.model.internal.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import collabware.api.document.DocumentException;
import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.model.graph.Graph;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.graph.Node;
import collabware.model.internal.graph.changes.NodeAddedChangeImpl;
import collabware.model.internal.graph.changes.NodeRemovedImpl;
import collabware.model.internal.graph.exception.DuplicateNodeException;
import collabware.model.internal.graph.exception.NoSuchNodeException;

public class GraphImpl implements ModifyableGraph, ChangeListener {

	private static final String ROOT_NODE = "---ROOT_NODE";
	
	private final Map<String, ModifyableNode> nodes = new HashMap<String, ModifyableNode>();
	private final ModifyableNode root;

	private ChangeListener listener = ChangeListener.NULL_LISTENER;
	
	public GraphImpl() {
		this(ChangeListener.NULL_LISTENER);
	}

	public GraphImpl(ChangeListener listener) {
		this.root = addNodeToGraph(ROOT_NODE);
		this.listener = listener;
	}

	public ModifyableNode addNode(String id) throws DocumentException {
		assertCanAddNodeWith(id);
		return addNodeToGraph(id);
	}
	
	private ModifyableNode addNodeToGraph(String id) {
		NodeImpl node = new NodeImpl(id, this);
		node.setGraph(this);
		nodes.put(id, node);
		listener.notifyChange(new NodeAddedChangeImpl(id));
		return node;
	}

	public boolean contains(Node n) {
		return nodes.containsKey(n.getId());
	}

	public void detach(ModifyableNode n) throws DocumentException {
		n.detach();
		detachFromGraph((NodeImpl) n);
	}

	void detachFromGraph(NodeImpl n) {
		n.setGraph(null);
		ModifyableNode remove = nodes.remove(n.getId());
		if (remove != null)
			listener.notifyChange(new NodeRemovedImpl(remove.getId()));
	}

	public ModifyableNode getNode(String id) {
		if (nodes.containsKey(id))
			return nodes.get(id);
		else
			throw new NoSuchNodeException(id);
	}

	public Collection<ModifyableNode> getNodes() {
		return Collections.unmodifiableCollection(nodes.values());
	}

	public ModifyableNode getRootNode() {
		return root;
	}

	private void assertCanAddNodeWith(String id) throws DocumentException {
		if (nodes.containsKey(id)) 
			throw new DuplicateNodeException(id);
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Graph) {
			return equalsGraph((Graph)o);
		}
		return false;
	}

	private boolean equalsGraph(Graph otherGraph) {
		if (getNodes().size() != otherGraph.getNodes().size())
			return false;
		for (Node otherNode: otherGraph.getNodes()) {
			if (!containsNodeEqualTo(otherNode)) 
				return false;
		}
		return true;
	}

	private boolean containsNodeEqualTo(Node otherNode) {
		if (contains(otherNode)) {
			Node theNode = getNode(otherNode.getId());
			return theNode.equals(otherNode);
		} else {
			return false;
		}
	}

	@Override
	public void notifyChange(Change change) {
		listener.notifyChange(change);
	}

	@Override
	public void remove(ModifyableNode n) {
		n.remove();
		detachFromGraph((NodeImpl) n);
	}

	@Override
	public void clear() {
		Collection<ModifyableNode> nodes2 = new HashSet<ModifyableNode>(getNodes());
		for (ModifyableNode n: nodes2) {
			if (n != root)
				remove(n);
		}
	}
}
