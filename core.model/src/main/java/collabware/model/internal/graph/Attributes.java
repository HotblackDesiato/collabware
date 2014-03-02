package collabware.model.internal.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import collabware.model.graph.ModifyableAttributes;
import collabware.model.internal.graph.changes.AttributeSetImpl;
import collabware.model.internal.graph.exception.NodeRemovedException;

class Attributes implements ModifyableAttributes {
	private final Map<String, Object> attributes = new HashMap<String, Object>();

	private final NodeImpl node;	

	public Attributes(NodeImpl node) {
		this.node = node;
	}

	public void set(String attr, String value) {
		doSetAttribute(attr, value);
	}


	public void set(String attr, Number value) {
		doSetAttribute(attr, value);
	}

	@Override
	public void set(String attr, Boolean b) {
		doSetAttribute(attr, b);
	}

	private void doSetAttribute(String attr, Object value) {
		if (node.getGraph() == null)
			throw new NodeRemovedException(node);
		Object oldValue = attributes.get(attr);
		if (value == null)
			attributes.remove(attr);
		else
			attributes.put(attr, value);
		node.notifyChange(new AttributeSetImpl(node.getId(), attr, oldValue , value));
	}
	
	public Object get(String attr) {
		return attributes.get(attr);
	}

	public Set<String> getAll() {
		return Collections.unmodifiableSet(attributes.keySet());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Attributes)
			return attributesEqual((Attributes)obj);
		else
			return false;
	}

	private boolean attributesEqual(Attributes otherAttributes) {
		if (attributes.size() != otherAttributes.attributes.size())
			return false;
		for (Entry<String,Object> attributeValuePair: attributes.entrySet()) {
			String attributeName = attributeValuePair.getKey();
			Object value = attributeValuePair.getValue();
			if (otherAttributes.get(attributeName) == null || !otherAttributes.get(attributeName).equals(value))
				return false;
		}
		return true;
	}

	@Override
	public Iterator<String> iterator() {
		return Collections.unmodifiableSet(attributes.keySet()).iterator();
	}
	
	void clear() {
		for(String attr: this) {
			doSetAttribute(attr, null);
		}
	}
}