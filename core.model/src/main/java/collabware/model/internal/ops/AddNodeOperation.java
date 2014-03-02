package collabware.model.internal.ops;

import static collabware.model.operations.SerializationConstants.ADD_NODE_OPERATION;
import static collabware.model.operations.SerializationConstants.TYPE;

import java.util.Map;

import collabware.api.document.DocumentException;
import collabware.api.operations.OperationApplicationException;
import collabware.model.graph.ModifyableGraph;
public class AddNodeOperation extends NodeOperation {

	public AddNodeOperation(String nodeId) {
		super(nodeId);		
	}

	public AddNodeOperation(Map<String, Object> map) {
		super(map);
	}

	protected void apply(ModifyableGraph graph) throws OperationApplicationException {
		try {
			graph.addNode(nodeId);
		} catch (DocumentException e) {
			throw new OperationApplicationException(this, e);
		}
	}

	@Override
	public String toString() {
		return "addNode(nodeId='"+getNodeId()+"')";
	}

	public RemoveNodeOperation inverse() {
		return new RemoveNodeOperation(getNodeId());
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
		AddNodeOperation other = (AddNodeOperation) obj;
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
		m.put(TYPE, ADD_NODE_OPERATION);
		return m;
	}
}
