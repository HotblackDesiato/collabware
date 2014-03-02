package collabware.model.internal.ops;

import static collabware.model.operations.SerializationConstants.REMOVE_NODE_OPERATION;
import static collabware.model.operations.SerializationConstants.TYPE;

import java.util.Map;

import collabware.api.operations.OperationApplicationException;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;

public class RemoveNodeOperation extends NodeOperation {

	public RemoveNodeOperation(String nodeId) {
		super(nodeId);
	}

	public RemoveNodeOperation(Map<String, Object> map) {
		super(map);
	}

	protected void apply(ModifyableGraph graph) throws OperationApplicationException {
		try {
			ModifyableNode n = graph.getNode(nodeId);
			graph.detach(n);
		} catch (Exception e) {
			throw new OperationApplicationException(this, e);
		}
	}
	
	public AddNodeOperation inverse() {
		return new AddNodeOperation(getNodeId());
	}

	@Override
	public String toString() {
		return "removeNode(nodeId='"+getNodeId()+"')";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoveNodeOperation other = (RemoveNodeOperation) obj;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> m = super.serialize();
		m.put(TYPE, REMOVE_NODE_OPERATION);
		return m;
	}

}
