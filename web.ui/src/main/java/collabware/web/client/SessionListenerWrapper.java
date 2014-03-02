package collabware.web.client;

import collabware.collaboration.client.ClientParticipant;
import collabware.collaboration.client.SessionListener;

import com.google.gwt.core.client.JavaScriptObject;

public class SessionListenerWrapper implements SessionListener {

	private final JavaScriptObject jsListener;

	public SessionListenerWrapper(JavaScriptObject jsListener) {
		this.jsListener = jsListener;
	}
	
	public native void joined()  /*-{
		var listener = this.@collabware.web.client.SessionListenerWrapper::jsListener;
		if (listener.joined && typeof(listener.joined) === 'function') {
			try {
				listener.joined();
			} catch (e){
				// TODO log exception
			}
		}
		
	}-*/;

	public native void disconnected()/*-{
		var listener = this.@collabware.web.client.SessionListenerWrapper::jsListener;
		if (listener.disconnected && typeof(listener.disconnected) === 'function') {
			try {
				listener.disconnected();
			} catch (e){
				// TODO log exception
			}
		}
	}-*/;

	@Override
	public native void participantAdded(ClientParticipant participant) /*-{
		var listener = this.@collabware.web.client.SessionListenerWrapper::jsListener;
		if (listener.participantAdded && typeof(listener.participantAdded) === 'function') {
			try {
				listener.participantAdded(participant);
			} catch (e){
				// TODO log exception
			}
		}
	}-*/;

}
