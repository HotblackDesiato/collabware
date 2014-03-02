package collabware.web.client.replay;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.client.replay.ReplayListener;

import com.google.gwt.core.client.JavaScriptObject;

public class ReplayListenerWrapper implements ReplayListener {

	private JavaScriptObject jsListener;

	public ReplayListenerWrapper(JavaScriptObject listener) {
		this.jsListener = listener;
	}

	@Override
	public void replayed(ComplexOperation op) {
		this._replayed(wrap(op));
	}

	public ComplexOperationWrapper wrap(ComplexOperation op) {
		return new ComplexOperationWrapper(op);
	}

	public native void _replayed(ComplexOperation op)  /*-{
		var listener = this.@collabware.web.client.replay.ReplayListenerWrapper::jsListener;
		if (listener.replayed && typeof(listener.replayed) === 'function') {
			try {
				listener.replayed(op);
			} catch (e){
				// TODO log exception
			}
		}
	}-*/;

	@Override
	public native void started() /*-{
		var listener = this.@collabware.web.client.replay.ReplayListenerWrapper::jsListener;
		if (listener.started && typeof(listener.started) === 'function') {
			try {
				listener.started();
			} catch (e){
				// TODO log exception
			}
		}
	}-*/;

	@Override
	public native void ended()  /*-{
		var listener = this.@collabware.web.client.replay.ReplayListenerWrapper::jsListener;
		if (listener.ended && typeof(listener.ended) === 'function') {
			try {
				listener.ended();
			} catch (e){
				// TODO log exception
			}
		}
	}-*/;


}
