package collabware.model.internal.ops;

import static collabware.model.operations.SerializationConstants.NODE_ID;

import java.util.HashMap;
import java.util.Map;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.OperationApplicationException;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableGraph;

public abstract class NodeOperation extends BasePrimitiveOperation {

	protected final String nodeId;

	public NodeOperation(String nodeId) {
		collabware.utils.Asserts.assertNotNull("nodeId", nodeId);
		this.nodeId = nodeId;
	}

	public NodeOperation(Map<String, Object> map) {
		this((String) map.get(NODE_ID));
	}

	public String getNodeId() {
		return nodeId;
	}
	
	public abstract NodeOperation inverse();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		return result;
	}

	@Override
	public void apply(ModifyableDocument graph) throws OperationApplicationException {
		this.apply(((ModifyableModel)graph).getGraph());
	}
	
	protected abstract void apply(ModifyableGraph graph) throws OperationApplicationException;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeOperation other = (NodeOperation) obj;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put(NODE_ID, this.nodeId);
		return m;
	}

	
}
