package collabware.model.internal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import collabware.api.document.DocumentException;
import collabware.model.graph.ModifyableNaryReferences;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.graph.changes.ReferenceAddedImpl;
import collabware.model.internal.graph.changes.ReferenceRemovedImpl;
import collabware.model.internal.graph.exception.NodeRemovedException;

public class NaryReferencesImpl implements ModifyableNaryReferences {
	
	private final HashMap<String, List<ModifyableNode>> references = new HashMap<String, List<ModifyableNode>>();
	private final HashMap<String, Set<ModifyableNode>> reverseReferences = new HashMap<String, Set<ModifyableNode>>();
	private final NodeImpl node;

	public NaryReferencesImpl(NodeImpl nodeImpl) {
		this.node = nodeImpl;
	}
	
	@Override
	public void add(String refName, int position, ModifyableNode target) {
		assertNodeBelongsToSameGraph(target);
		List<ModifyableNode> referencedNodes = references.get(refName);
		if (referencedNodes == null) {
			references.put(refName, new ArrayList<ModifyableNode>());
			referencedNodes = references.get(refName);
		}
		referencedNodes.add(position, target);
		((NaryReferencesImpl)target.getNaryReferences()).addReverseReference(refName, node);
		node.notifyChange(new ReferenceAddedImpl(node.getId(), refName, target.getId(), position));
	}

	private void assertNodeBelongsToSameGraph(ModifyableNode targetNode) throws DocumentException {
		if (targetNode.getGraph() != node.getGraph()) 
			throw new NodeRemovedException(targetNode);
	}

	private void addReverseReference(String refName, NodeImpl source) {
		Set<ModifyableNode> referencingNodes = reverseReferences.get(refName);
		if (referencingNodes == null) {
			reverseReferences.put(refName, new HashSet<ModifyableNode>());
			referencingNodes = reverseReferences.get(refName);
		}
		referencingNodes.add(source);
	}

	@Override
	public Collection<String> getAll() {
		return Collections.unmodifiableCollection(references.keySet());
	}

	@Override
	public List<ModifyableNode> get(String refName) {
		if (references.containsKey(refName)) {
			return Collections.unmodifiableList(references.get(refName));			
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void remove(String refName, int position) {
		ModifyableNode removedTarget = references.get(refName).remove(position);
		if (references.get(refName).size() == 0) {
			references.remove(refName);
		}
		((NaryReferencesImpl)removedTarget.getNaryReferences()).removeReverseReference(refName, node);
		node.notifyChange(new ReferenceRemovedImpl(node.getId(), refName, removedTarget.getId(), position));
	}

	private void removeReverseReference(String refName, NodeImpl theSource) {
		Set<ModifyableNode> referencingNodes = reverseReferences.get(refName);
		referencingNodes.remove(theSource);
		if (referencingNodes.isEmpty()) {
			reverseReferences.remove(refName);
		}
	}

	@Override
	public Iterator<String> iterator() {
		return getAll().iterator();
	}

	@Override
	public Set<String> getReverseReferences() {
		return Collections.unmodifiableSet(reverseReferences.keySet());
	}

	@Override
	public Set<ModifyableNode> getSourcesOf(String refName) {
		if (reverseReferences.containsKey(refName)) {
			return Collections.unmodifiableSet(reverseReferences.get(refName));			
		} else {
			return Collections.emptySet();			
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NaryReferencesImpl)
			return referencesEqual((NaryReferencesImpl)obj);
		else
			return false;
	}

	private boolean referencesEqual(NaryReferencesImpl otherReferences) {
		if (references.size() != otherReferences.references.size())	return false;
		for (String referenceName: references.keySet()) {
			if (!targetsMatch(this.get(referenceName), otherReferences.get(referenceName)))
				return false;
		}
		return true;
	}

	private boolean targetsMatch(List<ModifyableNode> targets,	List<ModifyableNode> otherTargets) {
		if (targets.size() != otherTargets.size()) return false;
		for (int i = 0; i < targets.size(); i++) {
			if (!targets.get(i).getId().equals(otherTargets.get(i).getId())) 
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return references.hashCode();
	}

	void removeAll() {
		removeOutgoingReferences();
		removeIncomingReferences();
	}

	private void removeOutgoingReferences() {
		for (Entry<String, List<ModifyableNode>> reference : references.entrySet()) {
			// Iterate backwards so we do not mess up the indexes.
			for (int i = reference.getValue().size()-1; i >= 0; i--) {
				remove(reference.getKey(), i);
			}
		}
	}
	
	private void removeIncomingReferences() {
		for (Entry<String, Set<ModifyableNode>> reverseReference : reverseReferences.entrySet()) {
			for (ModifyableNode referencingNode: reverseReference.getValue()) {
				List<ModifyableNode> referencesNodes = referencingNode.getNaryReferences().get(reverseReference.getKey());
				((NodeImpl) referencingNode).getNaryReferences().remove(reverseReference.getKey(), referencesNodes.indexOf(node));
			}
		}
	}
}
