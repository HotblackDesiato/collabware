package collabware.model.internal.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import collabware.api.document.DocumentException;
import collabware.model.graph.GraphException;
import collabware.model.graph.ModifyableNode;
import collabware.model.graph.ModifyableUnaryReferences;
import collabware.model.graph.Node;
import collabware.model.internal.graph.changes.ReferenceSetImpl;
import collabware.model.internal.graph.exception.NodeRemovedException;

class ReferencesImpl implements ModifyableUnaryReferences {
	
	private final Map<String, ModifyableNode> unaryReferences = new HashMap<String, ModifyableNode>();
	private final Set<String> unmodifiableUnaryReferences = Collections.unmodifiableSet(unaryReferences.keySet());
	
	private final Map<String, Set<ModifyableNode>> reverseUnaryReferences = new HashMap<String, Set<ModifyableNode>>();
	
	private final NodeImpl node;
	
	public ReferencesImpl(NodeImpl node) {
		this.node = node;
	}
	
	public void set(String ref, Node targetNode) throws GraphException {
		if (targetNode == null)
			removeUnaryReference(ref);
		else
			doSetUnaryReference(ref, (NodeImpl) targetNode);
	}

	private void doSetUnaryReference(String ref, NodeImpl targetNode) throws DocumentException {
		assertNodeBelongsToSameGraph(targetNode);
		((ReferencesImpl) targetNode.getUnaryReferences()).setReverseUnaryReference(ref, node);
		ModifyableNode oldTarget = unaryReferences.put(ref, targetNode);
		String oldTargetId = null;
		if (oldTarget != null)
			oldTargetId = oldTarget.getId();
		
		this.node.notifyChange(new ReferenceSetImpl(node.getId(), ref, oldTargetId, targetNode.getId()));
	}

	private void assertNodeBelongsToSameGraph(ModifyableNode targetNode) throws DocumentException {
		if (targetNode.getGraph() != node.getGraph()) 
			throw new NodeRemovedException(targetNode);
	}

	private void removeUnaryReference(String ref) {
		NodeImpl oldTarget = (NodeImpl) unaryReferences.remove(ref);
		((ReferencesImpl) oldTarget.getUnaryReferences()).removeReverseReference(ref, node);
		this.node.notifyChange(new ReferenceSetImpl(node.getId(), ref, oldTarget.getId(), null));
	}

	private void removeReverseReference(String ref, NodeImpl referencingNode) {
		Set<ModifyableNode> referencingNodes = getReferencingNodes(ref);
		referencingNodes.remove(referencingNode);
		if (referencingNodes.isEmpty()) {
			reverseUnaryReferences.remove(ref);
		}
	}

	private void setReverseUnaryReference(String ref, NodeImpl referencingNode) {
		Set<ModifyableNode> referencingNodes = getReferencingNodes(ref);
		referencingNodes.add(referencingNode);
	}

	private Set<ModifyableNode> getReferencingNodes(String ref) {
		Set<ModifyableNode> referencingNodes = reverseUnaryReferences.get(ref);
		if (referencingNodes == null) {
			referencingNodes = new HashSet<ModifyableNode>();
			reverseUnaryReferences.put(ref, referencingNodes);
		}
		return referencingNodes;
	}

	public ModifyableNode get(String ref) {
		return unaryReferences.get(ref);
	}

	public Collection<ModifyableNode> sourcesOf(String ref) {
		Set<ModifyableNode> set = reverseUnaryReferences.get(ref);
		if (set != null)
			return Collections.unmodifiableCollection(set);
		else 
			return Collections.emptySet();
	}

	public Set<String> getAll() {
		return unmodifiableUnaryReferences;
	}

	public Set<String> getReverseUnaryReferences() {
		return Collections.unmodifiableSet(reverseUnaryReferences.keySet());
	}

	@Override
	public int hashCode() {
		return unaryReferences.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReferencesImpl)
			return referencesEqual((ReferencesImpl)obj);
		else
			return false;
	}

	private boolean referencesEqual(ReferencesImpl otherReferences) {
		if (unaryReferences.size() != otherReferences.unaryReferences.size())
			return false;
		for (Entry<String,ModifyableNode> attributeValuePair: unaryReferences.entrySet()) {
			String attributeName = attributeValuePair.getKey();
			ModifyableNode target = attributeValuePair.getValue();
			if (otherReferences.get(attributeName) == null || !otherReferences.get(attributeName).getId().equals(target.getId()))
				return false;
		}
		return true;
	}

	@Override
	public Iterator<String> iterator() {
		return unmodifiableUnaryReferences.iterator();
	}

	void removeAll() {
		removeOutgoingReferences();
		removeIncomingReferences();
	}

	private void removeIncomingReferences() {
		for (Entry<String, Set<ModifyableNode>> reverseReference : reverseUnaryReferences.entrySet()) {
			for (ModifyableNode referencingNode: reverseReference.getValue()) {
				((NodeImpl) referencingNode).getUnaryReferences().set(reverseReference.getKey(), null);
			}
		}
	}

	private void removeOutgoingReferences() {
		for (String ref: unaryReferences.keySet()) {
			removeUnaryReference(ref);
		}
	}
}
