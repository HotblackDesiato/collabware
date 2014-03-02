package collabware.model.internal.graph;

import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.model.graph.ModifyableAttributes;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNaryReferences;
import collabware.model.graph.ModifyableNode;
import collabware.model.graph.ModifyableUnaryReferences;
import collabware.model.graph.Node;
import collabware.model.internal.graph.exception.CannotRemoveRootNodeException;
import collabware.model.internal.graph.exception.NodeRemovalException;
import collabware.model.internal.graph.exception.ResidualAttributeException;
import collabware.model.internal.graph.exception.ResidualReferenceException;
import collabware.model.internal.graph.exception.ResidualReferenceTargetException;
/**
 * @author thomashettel
 *
 */
class NodeImpl implements ModifyableNode, ChangeListener {

	private final String id;
	private final Attributes attributes = new Attributes(this);
	private final ReferencesImpl unaryReferences = new ReferencesImpl(this);
	private final NaryReferencesImpl naryReferences = new NaryReferencesImpl(this);
	private final ChangeListener listener;
	
	private GraphImpl graph;

	public NodeImpl(String id, ChangeListener listener) {
		this.id = id;
		this.listener = listener;
	}
	
	public NodeImpl(String id) {
		this(id, ChangeListener.NULL_LISTENER);
	}

	public String getId() {
		return id;
	}

	public ModifyableGraph getGraph() {
		return graph;
	}
	
	void setGraph(GraphImpl graph) {
		this.graph = graph;
	}

	public void detach() {
		assertCanBeDetached();
		graph.detachFromGraph(this);
		graph = null;
	}

	public ModifyableUnaryReferences getUnaryReferences() {
		return unaryReferences;
	}

	@Override
	public ModifyableNaryReferences getNaryReferences() {
		return naryReferences;
	}

	public ModifyableAttributes getAttributes() {
		return attributes;
	}
	
	private void assertCanBeDetached() throws NodeRemovalException {
		assertHoldsNoAttributes();
		assertHoldsNoUnaryReferences();
		assertHoldsNoNaryReferences();
		assertNotTargetOfUnaryReferences();
		assertNotTargetOfNaryReferences();
		assertIsNotRootNode();
	}

	private void assertNotTargetOfUnaryReferences() throws NodeRemovalException {
		if (!unaryReferences.getReverseUnaryReferences().isEmpty())
			throw new ResidualReferenceTargetException(this);
	}
	
	private void assertHoldsNoUnaryReferences() throws NodeRemovalException {
		if (!unaryReferences.getAll().isEmpty())
			throw new ResidualReferenceException(this);
	}
	
	private void assertHoldsNoAttributes() throws NodeRemovalException {
		if (!attributes.getAll().isEmpty())
			throw new ResidualAttributeException(this);
	}

	private void assertHoldsNoNaryReferences() {
		if (!naryReferences.getAll().isEmpty())
			throw new ResidualReferenceTargetException(this);
	}
	
	private void assertNotTargetOfNaryReferences() {
		if (!naryReferences.getReverseReferences().isEmpty())
			throw new ResidualReferenceTargetException(this);
	}
	
	private void assertIsNotRootNode() throws NodeRemovalException {
		if (isRootNode()) 
			throw new CannotRemoveRootNodeException();
	}

	private boolean isRootNode() {
		return this == this.getGraph().getRootNode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			return nodeEquals((Node)o);
		}
		return false;
	}

	private boolean nodeEquals(Node otherNode) {
		return id.equals(otherNode.getId()) 
			&& getAttributes().equals(otherNode.getAttributes()) 
			&& getUnaryReferences().equals(otherNode.getUnaryReferences())
			&& getNaryReferences().equals(otherNode.getNaryReferences());
	}

	@Override
	public void notifyChange(Change change) {
		listener.notifyChange(change);
	}

	@Override
	public void remove() {
		attributes.clear();
		unaryReferences.removeAll();
		naryReferences.removeAll();
		detach();
	}
}
