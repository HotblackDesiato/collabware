package collabware.web.client.js;

import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.api.document.change.ComplexChangeEnded;
import collabware.model.change.AttributeSet;
import collabware.model.change.NodeAdded;
import collabware.model.change.NodeRemoved;
import collabware.model.change.ReferenceAdded;
import collabware.model.change.ReferenceRemoved;
import collabware.model.change.ReferenceSet;

import com.google.gwt.core.client.JavaScriptObject;

final class JSChangeListener implements ChangeListener {
	private JavaScriptObject jsListener;

	public JSChangeListener(JavaScriptObject l) {
		this.jsListener = l;
	}

	public void notifyChange(Change change) {
		if (change instanceof NodeAdded) {
			NodeAdded nodeAdded = (NodeAdded) change;
			notifyNodeAdded(nodeAdded.getNodeId());
			
		} else if (change instanceof NodeRemoved) {
			NodeRemoved nodeRemoved = (NodeRemoved) change;
			notifyNodeRemoved(nodeRemoved.getNodeId());
		
		} else if (change instanceof ReferenceAdded) {
			ReferenceAdded refAdded = (ReferenceAdded)change;
			notifyReferenceAdded(refAdded.getNodeId(), refAdded.getReferenceName(), refAdded.getPosition(), refAdded.getTargetId());
		
		} else if (change instanceof ReferenceRemoved) {
			ReferenceRemoved refRem= (ReferenceRemoved)change;
			notifyReferenceRemoved(refRem.getNodeId(), refRem.getReferenceName(), refRem.getPosition(), refRem.getTargetId());
		
		} else if (change instanceof ReferenceSet) {
			ReferenceSet setRef = (ReferenceSet)change;
			notifyReferenceSet(setRef.getNodeId(), setRef.getReferenceName(), setRef.getNewTargetId());
			
		} else if (change instanceof AttributeSet) {
			AttributeSet setAttr = (AttributeSet)change;
			if (setAttr.getNewValue() instanceof String) {
				notifyAttributeSet(setAttr.getNodeId(), setAttr.getAttributeName(), (String)setAttr.getNewValue());
			} else if (setAttr.getNewValue() instanceof Double) {
				notifyAttributeSet(setAttr.getNodeId(), setAttr.getAttributeName(), (Double)setAttr.getNewValue());
			} else if (setAttr.getNewValue() instanceof Boolean) {
				notifyAttributeSet(setAttr.getNodeId(), setAttr.getAttributeName(), (Boolean)setAttr.getNewValue());
			} else {
				notifyAttributeSet(setAttr.getNodeId(), setAttr.getAttributeName(), (String)null);
			}
		} else if (change instanceof ComplexChangeEnded) {
			notifyComplexChangeEnded(((ComplexChangeEnded) change).getDescription());
		}
	}

	private native void notifyComplexChangeEnded(String description)   /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.complexChangeEnded && typeof(listener.complexChangeEnded) === 'function') {
			try {
				listener.complexChangeEnded(description);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
	}-*/;

	private native void notifyAttributeSet(String nodeId, String attributeName, boolean b)  /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.attributeSet && typeof(listener.attributeSet) === 'function') {
			try {
				listener.attributeSet(nodeId, attributeName, b);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
		
	}-*/;

	private native void notifyAttributeSet(String nodeId, String attributeName, Double d) /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.attributeSet && typeof(listener.attributeSet) === 'function') {
			try {
				listener.attributeSet(nodeId, attributeName, d);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
		
	}-*/;

	private native void notifyAttributeSet(String nodeId, String attributeName, String s) /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.attributeSet && typeof(listener.attributeSet) === 'function') {
			try {
				listener.attributeSet(nodeId, attributeName, s);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
		
	}-*/;

	private native void notifyReferenceSet(String nodeId, String referenceName, String newTargetId) /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.unaryReferenceSet && typeof(listener.unaryReferenceSet) === 'function') {
			try {
				listener.unaryReferenceSet(nodeId, referenceName, newTargetId||undefined);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
		
	}-*/;

	private native void notifyReferenceRemoved(String nodeId, String referenceName, int i, String targetId) /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.referenceRemoved && typeof(listener.referenceRemoved) === 'function') {
			try {
				listener.referenceRemoved(nodeId, referenceName, i, targetId);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
		
	}-*/;

	private native void notifyReferenceAdded(String nodeId, String referenceName, int i, String targetId) /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.referenceAdded && typeof(listener.referenceAdded) === 'function') {
			try {
				listener.referenceAdded(nodeId, referenceName, i, targetId);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
	}-*/;

	private native void notifyNodeRemoved(String nodeId) /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.nodeRemoved && typeof(listener.nodeRemoved) === 'function') {
			try {
				listener.nodeRemoved(nodeId);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
	}-*/;

	private native void notifyNodeAdded(String nodeId) /*-{
		var listener = this.@collabware.web.client.js.JSChangeListener::jsListener;
		if (listener.nodeAdded && typeof(listener.nodeAdded) === 'function') {
			try {
				listener.nodeAdded(nodeId);
			} catch (e){
				if ($wnd.console && typeof ($wnd.console.error) === 'function') {
					console.error(e);
				}
			}
		}
	}-*/;
}