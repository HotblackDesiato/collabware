package collabware.web.client;

import java.util.ArrayList;
import java.util.List;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.ClientParticipant;
import collabware.collaboration.client.ParticipantImpl;
import collabware.collaboration.client.Session;
import collabware.collaboration.internal.client.ClientImpl;
import collabware.model.Model;
import collabware.model.internal.ops.transformation.GraphTransformationMatrix;
import collabware.transformer.internal.TransformationProviderImpl;
import collabware.web.client.js.JSDocument;
import collabware.web.client.replay.ReplayWrapper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
@ExportPackage("collabware")
@Export("Session")
public class SessionWrapper implements Exportable {

	private Session client;
	private JSDocument document;
	private Callback onConnectedCallback;
	private String id;
	private ReplayWrapper replayWrapper;
	
	
	public SessionWrapper() {
		this(new CometdClientEndpoint());
	}
	public SessionWrapper(ClientEndpoint endPoint) {
		TransformationProviderImpl transformationProviderImpl = new TransformationProviderImpl();
		//This session is now tied to graph based documents. 
		transformationProviderImpl.setPartiallyInclusiveOperationalTransformation(new GraphTransformationMatrix());
		client = new ClientImpl(endPoint, new TransformationProviderImpl());
		document = new JSDocument((Model) client.getDocument(), client);
		replayWrapper = new ReplayWrapper(client.replay());
	}

	@Export
	public void connect(String id) {
		this.id = id;
		client.join(id);
		if (onConnectedCallback != null) onConnectedCallback.call();
	}

	@Export
	public JSDocument getDocument() {
		return document;
	}
	
	@Export
	public LocalParticipantWrapper getLocalParticipant() {
		return new LocalParticipantWrapper(this, client.getLocalParticipant());
	}
	
	@Export
	public ParticipantWrapper[] getAllParticipants() {
		return wrap(client.getParticipants());
	}
	
	private ParticipantWrapper[] wrap(List<ClientParticipant> participants) {
		ParticipantWrapper[] wrappers = new ParticipantWrapper[participants.size()];
		for (int i = 0; i < participants.size(); i++) {
			wrappers[i] = new ParticipantWrapper(participants.get(i));
		}
		return wrappers;
	}
	
	@Export
	public void disconnect() {
		client.disconnect();
	}
	
	@Export
	public void onConnected(Callback c) {
		this.onConnectedCallback = c;
	}
	
	@Export
	public void addSessionListener(JavaScriptObject l) {
		client.addSessionListener(new SessionListenerWrapper(l));
	}
	
	@Export
	public  void addParticipant(ParticipantWrapper participant) {
		addParticipant(participant.getId());
	}
	
	@Export
	public native void addParticipant(String participantId) /*-{
		var id = this.@collabware.web.client.SessionWrapper::id;
		$wnd.jQuery.post("/collabware/rest/documents/"+id+"/participants", {"userName":participantId}, function (data) {console.log(data);})
	}-*/;
	
	@Export
	public ParticipantWrapper[] getContacts() {
		String json = fetchContacts();
		JSONArray contacts = JSONParser.parseStrict(json).isArray();
		List<ClientParticipant> participants = new ArrayList<ClientParticipant>();
		for (int i = 0; i < contacts.size(); i++) {
			JSONObject jsonValue = contacts.get(i).isObject();
			participants.add(new ParticipantImpl(getString(jsonValue, "id"), getString(jsonValue, "imageUrl"), getString(jsonValue, "displayName")));
		}
		return wrap(participants);
	}
	
	private String getString(JSONObject jsonValue, String key) {
		return jsonValue.get(key).isString().stringValue();
	}
	
	private native String fetchContacts() /*-{
		var serializedContacts = "[]";
		$wnd.jQuery.ajax({url:"/collabware/rest/contacts", success:function (data) {serializedContacts = data;}, async:false});
		return serializedContacts;
	}-*/;
	
	@Export
	public ParticipantWrapper createParticipant(String id, String displayName, String imageUrl) {
		return new ParticipantWrapper(new ParticipantImpl(id, imageUrl, displayName));
	}
	
	@Export
	public ReplayWrapper replay() {
		return replayWrapper;
	}
	
	private native void log(String string) /*-{
		$wnd.console.log(string);
	}-*/;
}
