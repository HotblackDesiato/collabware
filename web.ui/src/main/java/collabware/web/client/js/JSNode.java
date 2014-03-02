package collabware.web.client.js;

import java.util.List;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import collabware.model.graph.ModifyableNode;

import com.google.gwt.core.client.JavaScriptObject;

@Export
@ExportPackage("collabware")
public class JSNode implements Exportable {
	private final ModifyableNode wrappedNode;

	public JSNode(ModifyableNode node) {
		this.wrappedNode = node;
	}

	/**
	 * 
	 * @return the id of this node.
	 */
	@Export
	public String getId() {
		return wrappedNode.getId();
	}
	
	/**
	 * Sets an attribute to a given value. Values can be of type boolean, string, number or undefined.
	 * @param attr
	 * @param value
	 * @return the node for chaining.
	 */
	@Export
	public JSNode attr(String attr, Object value) {
		if (value instanceof Boolean)
			wrappedNode.getAttributes().set(attr, (Boolean) value);
		else if (value instanceof String)
			wrappedNode.getAttributes().set(attr, (String) value);
		else if (value instanceof Number)
			wrappedNode.getAttributes().set(attr, (Number) value);
		else 
			wrappedNode.getAttributes().set(attr, (String)null);
		return this;
	}
	
	private static boolean isNumber(Object o) {return o instanceof Double;}
	private static double toNumber(Object o) {return (Double)o;}
	private static boolean isString(Object o) {return o instanceof String;}
	private static String toString(Object o) {return (String)o;}
	private static boolean isBoolean(Object o) {return o instanceof Boolean;}
	private static boolean toBoolean(Object o) {return (Boolean) o;}
	
	/**
	 * Returns the value of an attribute.
	 * @param attr the name of the attribute
	 * @return the value associated with the attribute name.
	 */
	@Export
	public native JavaScriptObject attr(String attr) /*-{
		// _attr(String) returns a Object which does not get auto unboxed.
		// The unboxing is done explicitly by the code below.
		var boxedValue = this.@collabware.web.client.js.JSNode::_attr(Ljava/lang/String;)(attr);
		if (@collabware.web.client.js.JSNode::isBoolean(Ljava/lang/Object;)(boxedValue)) {
			return @collabware.web.client.js.JSNode::toBoolean(Ljava/lang/Object;)(boxedValue);
		} else if (@collabware.web.client.js.JSNode::isNumber(Ljava/lang/Object;)(boxedValue)) {
			return @collabware.web.client.js.JSNode::toNumber(Ljava/lang/Object;)(boxedValue);
		} else if (@collabware.web.client.js.JSNode::isString(Ljava/lang/Object;)(boxedValue)) {
			return @collabware.web.client.js.JSNode::toString(Ljava/lang/Object;)(boxedValue);
		} else {
			return undefined;			
		}
	}-*/;
	
	private Object _attr(String attr) {
		return getWrappedNode().getAttributes().get(attr);
	}

	/**
	 * Sets the target of a unary reference
	 * @param ref the name of the reference.
	 * @param target the target node or undefined
	 * @return this node for method chaining.
	 */
	@Export
	public JSNode ref(String ref, JSNode target) {
		if (target != null) {
			wrappedNode.getUnaryReferences().set(ref, target.wrappedNode);
		} else {
			wrappedNode.getUnaryReferences().set(ref, null);
		}
		return this;
	}
	
	/**
	 * Returns the target of a reference.
	 * @param ref the name of the reference
	 * @return the target node or undefined.
	 */
	@Export
	public JSNode ref(String ref) {
		ModifyableNode node = getWrappedNode().getUnaryReferences().get(ref);
		if (node != null) {
			return new JSNode(node);
		} else {
			return null;
		}
	}

	/**
	 * Returns all target nodes of an ordered reference.
	 * @param ref the name of the ordered reference.
	 * @return an array containing all targets of the ordered reference.
	 */
	@Export
	public native JavaScriptObject orderedRef(String ref) /*-{
		console.log("get ordered references");
		return this.@collabware.web.client.js.JSNode::orderedRefs(Ljava/lang/String;)(ref);
	}-*/;
	
	private JSNode[] orderedRefs(String ref) {
		return wrapNodes(getWrappedNode().getNaryReferences().get(ref));
	}

	/**
	 * Returns the node at {@code index} of reference {@code ref} 
	 * @param ref the name of the reference
	 * @param index the index of the referenced node.
	 * @return the referenced node. 
	 */
	@Export
	public native JavaScriptObject orderedRef(String ref, int index) /*-{
		console.log("get ordered reference at position " + i);
		return this.@collabware.web.client.js.JSNode::getOrderedRef(Ljava/lang/String;I)(ref, i);
	}-*/;

	private JSNode getOrderedRef(String ref, int i) {
		return wrapNode(getWrappedNode().getNaryReferences().get(ref).get(i));
	}
	
	@Export
	public native JavaScriptObject orderedRef(String ref, int i, JSNode n) /*-{
		console.log("set ordered reference at position " + i + " to " + n);
		return this.@collabware.web.client.js.JSNode::addOrderedRef(Ljava/lang/String;ILcollabware/web/client/js/JSNode;)(ref, i, n);
	}-*/;

	/**
	 * Adds a node at the end of an ordered reference.
	 * @param ref the name of the reference
	 * @param target the node to be added.
	 * @return this for method chaining
	 */
	@Export
	public native JavaScriptObject orderedRef(String ref, JSNode target) /*-{
		console.log("set ordered reference at last position to " + target);
		return this.@collabware.web.client.js.JSNode::addOrderedRef(Ljava/lang/String;Lcollabware/web/client/js/JSNode;)(ref,target);
	}-*/;

	private JSNode addOrderedRef(String ref, int i, JSNode target) {
		getWrappedNode().getNaryReferences().add(ref, i, target.wrappedNode);
		return this;
	}
	
	private JSNode addOrderedRef(String ref, JSNode target) {
		int lastIndex = getWrappedNode().getNaryReferences().get(ref).size();
		getWrappedNode().getNaryReferences().add(ref, lastIndex, target.wrappedNode);
		return this;
	}

	private JSNode[] wrapNodes(List<ModifyableNode> nodes) {
		JSNode[] wrappers = new JSNode[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			wrappers[i] = wrapNode(nodes.get(i));
		}
		return wrappers;
	}

	private JSNode wrapNode(ModifyableNode modifyableNode) {
		if (modifyableNode != null) {
			return new JSNode(modifyableNode);
		} else {
			return null;
		}
	}

	ModifyableNode getWrappedNode() {
		return wrappedNode;
	}
	
	/** 
	 * removes this node from the graph.
	 * 
	 */
	@Export
	public void detach() {
		wrappedNode.detach();
	}
	
}
