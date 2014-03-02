package collabware.model.internal.ops;

import static collabware.model.operations.SerializationConstants.ADD_REFERENCE_OPERATION;
import static collabware.model.operations.SerializationConstants.NEW_TARGET_ID;
import static collabware.model.operations.SerializationConstants.TYPE;

import java.util.Map;

import collabware.api.operations.OperationApplicationException;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;

public class AddReferenceOperation extends NaryReferenceOperation {

	public AddReferenceOperation(String nodeId, String refName,	int position, String targetId) {
		super(nodeId, refName, position, targetId);
	}

	public AddReferenceOperation(Map<String, Object> map) {
		super(map, (String) map.get(NEW_TARGET_ID));
	}

	@Override
	public RemoveReferenceOperation inverse() {
		return new RemoveReferenceOperation(nodeId, refName, position, targetId);
	}

	protected void apply(ModifyableGraph graph) throws OperationApplicationException {
		try  {
			ModifyableNode source = graph.getNode(nodeId);
			ModifyableNode target = graph.getNode(targetId);
			source.getNaryReferences().add(refName, position, target);
		} catch (RuntimeException e){
			throw new OperationApplicationException(this, e);
		}
	}

	@Override
	public String toString() {
		return "addRef(nodeId='"+getNodeId()+"', referenceName='"+getReferenceName()+"', position="+getPosition()+", targetId='"+getTargetId()+"')";
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> m = super.serialize();
		m.put(TYPE, ADD_REFERENCE_OPERATION);
		m.put(NEW_TARGET_ID, 	this.targetId);
		return m;
	}
}