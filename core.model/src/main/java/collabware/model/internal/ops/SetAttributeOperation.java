package collabware.model.internal.ops;

import static collabware.utils.Asserts.assertNotNull;
import static collabware.model.operations.SerializationConstants.*;
import java.util.Map;

import collabware.api.operations.OperationApplicationException;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.graph.exception.NoSuchNodeException;

public class SetAttributeOperation extends NodeOperation {

	private final String attributeName;
	private final Object oldValue;
	private final Object newValue;

	public SetAttributeOperation(String nodeId, String attributeName, Object oldValue, Object newValue) {
		super(nodeId);
		assertNotNull("attributeName", attributeName);
		this.attributeName = attributeName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public SetAttributeOperation(Map<String, Object> map) {
		super(map);
		this.attributeName = (String) map.get(ATTRIBUTE_NAME);
		this.oldValue = map.get(OLD_VALUE);
		this.newValue= map.get(NEW_VALUE);
	}

	protected void apply(ModifyableGraph graph) throws OperationApplicationException {
		try {
			ModifyableNode node = graph.getNode(getNodeId());
			assertCurrentValueMatchesOldValue(node);
			if (newValue instanceof String)
				node.getAttributes().set(attributeName, (String)newValue);
			else if (newValue instanceof Number)
				node.getAttributes().set(attributeName, (Number)newValue);
			else if (newValue instanceof Boolean)
				node.getAttributes().set(attributeName, (Boolean)newValue);
			else if (newValue==null)
				node.getAttributes().set(attributeName, (String)null);
			else 
				throw new OperationApplicationException("Invalid new value '"+newValue+"'");
		} catch (NoSuchNodeException e) {
			throw new OperationApplicationException(this, e);
		}
	}

	private void assertCurrentValueMatchesOldValue(ModifyableNode node)	throws AttributeValueMismatchException {
		Object currentAttributeValue = node.getAttributes().get(attributeName);
		if (!oldValueMatches(currentAttributeValue))
			throw new AttributeValueMismatchException(getNodeId(), attributeName, oldValue, currentAttributeValue);
	}

	private boolean oldValueMatches(Object currentAttributeValue) {
		return oldValue == currentAttributeValue  || 
			(currentAttributeValue != null && currentAttributeValue.equals(oldValue));
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public SetAttributeOperation inverse() {
		return new SetAttributeOperation(getNodeId(), attributeName, newValue, oldValue);
	}
	
	@Override
	public String toString() {
		return "setAttr(nodeId='"+nodeId+"', attributeName='"+attributeName+"', oldValue='"+oldValue+"', newValue='"+newValue+"')";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result
				+ ((newValue == null) ? 0 : newValue.hashCode());
		result = prime * result
				+ ((oldValue == null) ? 0 : oldValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetAttributeOperation other = (SetAttributeOperation) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (newValue == null) {
			if (other.newValue != null)
				return false;
		} else if (!newValue.equals(other.newValue))
			return false;
		if (oldValue == null) {
			if (other.oldValue != null)
				return false;
		} else if (!oldValue.equals(other.oldValue))
			return false;
		return true;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> m = super.serialize();
		m.put(TYPE, SET_ATTRIBUTE_OPERATION);
		m.put(ATTRIBUTE_NAME, this.attributeName);
		m.put(OLD_VALUE, this.oldValue);
		m.put(NEW_VALUE, this.newValue);
		return m;
	}
}
