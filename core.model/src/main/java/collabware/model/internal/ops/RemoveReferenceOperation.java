package collabware.model.internal.ops;

import static collabware.model.operations.SerializationConstants.OLD_TARGET_ID;
import static collabware.model.operations.SerializationConstants.REMOVE_REFERENCE_OPERATION;
import static collabware.model.operations.SerializationConstants.TYPE;

import java.util.Map;

import collabware.api.operations.OperationApplicationException;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;

public class RemoveReferenceOperation extends NaryReferenceOperation  {

	public RemoveReferenceOperation(String nodeId, String refName,	int position, String targetId) {
		super(nodeId, refName, position, targetId);
	}

	public RemoveReferenceOperation(Map<String, Object> map) {
		super(map, (String) map.get(OLD_TARGET_ID));
	}

	@Override
	public AddReferenceOperation inverse() {
		return new AddReferenceOperation(nodeId, refName, position, targetId);
	}

	protected void apply(ModifyableGraph graph) throws OperationApplicationException {
		try  {
			ModifyableNode source = graph.getNode(nodeId);
			ModifyableNode actualTarget = source.getNaryReferences().get(refName).get(position);
			if (actualTarget.getId().equals(targetId)) {
				source.getNaryReferences().remove(refName, position);				
			} else {
				throw new OperationApplicationException("Application of operation '"+this+"' failed because:\nExpected targetId '"+this.targetId+"' does not match actual targetId '"+actualTarget.getId()+"'");
			}
		} catch (RuntimeException e){
			throw new OperationApplicationException(this, e);
		}
	}
	
	@Override
	public String toString() {
		return "remRef(nodeId='"+getNodeId()+"', referenceName='"+getReferenceName()+"', position="+getPosition()+", targetId='"+getTargetId()+"')";

	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> m = super.serialize();
		m.put(TYPE, REMOVE_REFERENCE_OPERATION);
		m.put(OLD_TARGET_ID, 	this.targetId);
		return m;
	}
}