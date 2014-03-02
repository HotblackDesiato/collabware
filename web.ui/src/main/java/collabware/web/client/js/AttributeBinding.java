package collabware.web.client.js;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;

import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.model.change.AttributeSet;

import com.google.gwt.core.client.JavaScriptObject;
@Export
public class AttributeBinding implements Exportable, ChangeListener {

	private JavaScriptObject callback;
	private final String nodeId;
	private final String attribute;

	@NoExport
	public AttributeBinding(JSNode node, String attribute) {
		this.nodeId = node.getWrappedNode().getId();
		this.attribute = attribute;
	}

	@Export
	public void onChanged(JavaScriptObject callback) {
		this.callback = callback;
		
	}

	public void notifyChange(Change change) {
		if (change instanceof AttributeSet) {
			AttributeSet attrSet = (AttributeSet) change;
			if (attrSet.getNodeId().equals(this.nodeId) && attrSet.getAttributeName().equals(attribute)) {
				notifyOnChanged(attrSet.getNewValue());
			}
		}
	}

	private void notifyOnChanged(Object newValue) {
		if (newValue instanceof String) {
			fireCallback((String)newValue);
		} else if (newValue instanceof Double) {
			fireCallback((Double)newValue);
		} else if (newValue instanceof Boolean) {
			fireCallback((Boolean)newValue);
		} else {
			fireCallback((String)null);
		}
	}

	private native void fireCallback(boolean newValue) /*-{
		var callback = this.@collabware.web.client.js.AttributeBinding::callback;
		if (callback&& typeof(callback) === 'function') {
			try {
				callback(newValue);
			} catch (e){
				// TODO log exception
			}
		}
	}-*/;
	private native void fireCallback(String newValue) /*-{
		var callback = this.@collabware.web.client.js.AttributeBinding::callback;
		if (callback&& typeof(callback) === 'function') {
			try {
				callback(newValue);
			} catch (e){
				// TODO log exception
			}
		}
	}-*/;
	private native void fireCallback(double newValue) /*-{
		var callback = this.@collabware.web.client.js.AttributeBinding::callback;
		if (callback&& typeof(callback) === 'function') {
			try {
				callback(newValue);
			} catch (e){
				// TODO log exception
			}
		}
	}-*/;

	public String getNodeId() {
		return nodeId;
	}

	public String getAttribute() {
		return attribute;
	}
}
