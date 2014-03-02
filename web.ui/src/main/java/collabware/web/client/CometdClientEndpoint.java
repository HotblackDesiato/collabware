package collabware.web.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.ClientEndpointListener;
import collabware.collaboration.client.ParticipantImpl;
import collabware.model.internal.ModelProviderImpl;
import collabware.web.client.cometd.InitMessage;
import collabware.web.client.cometd.UpdateMessage;
import collabware.web.shared.JsonProvider;
import collabware.web.shared.JsonizationException;
import collabware.web.shared.OperationDejsonizer;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class CometdClientEndpoint implements ClientEndpoint {

	private static Logger logger = Logger.getLogger("collabware.web.client.CometdClientEndpoint");
	
	private ClientEndpointListener listener;
	private String id;
	
	
	public void addClientEndpointListener(ClientEndpointListener listener) {
		this.listener = listener;
	}

	public native void setup(String id) /*-{
		var _connected = false;
		var that = this;
		function onConnect(message) {
			if ($wnd.jQuery.cometd.isDisconnected()) {
		        return;
		    }
		    var wasConnected = _connected;
		    _connected = message.successful;
		    if (!wasConnected && _connected) {
				connected();
		    } else if (wasConnected && !_connected) {
				disconnected();
		    }
		}
		
		function connected() {
			$wnd.console.info("Connected to server.");
			$wnd.console.info("Joining collaboration "+ id);
			$wnd.jQuery.cometd.subscribe("/initialize", onInitialize);
			$wnd.jQuery.cometd.publish("/service/join/"+id, {});
			$wnd.jQuery.cometd.subscribe("/update", onUpdate)
			$wnd.jQuery.cometd.subscribe("/participantAdded/"+id, onParticipantAdded)
		}
		
		function onUpdate(message) {
			try{
				that.@collabware.web.client.CometdClientEndpoint::receiveUpdate(Ljava/lang/String;)(message.data);
			} catch(e) {
				$wnd.console.error("Error during receiving update: ", e);
				$wnd.console.error(e.stack);
			}
		}
		
		function onInitialize(message) {
			var payload = $wnd.JSON.parse(message.data);
			if (payload.successful) {
				try{
					that.@collabware.web.client.CometdClientEndpoint::initialize(Ljava/lang/String;)(message.data);
				} catch(e) {
					$wnd.console.error("Error during initialization: ", e);
					$wnd.console.error(e.stack);
				}
			} else {
				$wnd.console.error("Initialization unsuccessful.");
			}
		}
		
		function onParticipantAdded(message) {
			that.@collabware.web.client.CometdClientEndpoint::onParticipantAdded(Ljava/lang/String;)(message.data);
			$wnd.console.log("participant added:", message);
		}
		
		function onDisconnect() {
			disconnected();
		} 
		
		function disconnected() {
			that.@collabware.web.client.CometdClientEndpoint::onDisconnected()();
			$wnd.console.info("Disconnected from server.");
		}
		
		$wnd.jQuery.cometd.addListener('/meta/connect', onConnect);
		$wnd.jQuery.cometd.addListener('/meta/disconnect', onDisconnect);
		
	}-*/;

	public void initialize(String jsonPayload) {
		try {
			log("parsing message " + jsonPayload);
			InitMessage init = new InitMessage(jsonPayload);
			log("Initializing with initSequence: " + init.getInitSequence());
			listener.initialize(init.getInitSequence() , init.getParticipants() , init.getLocalParticipant());
		} catch (JsonizationException e) {
			log("Exception while parsing init message: " + e.getMessage());
		}
	}
	
	public void receiveUpdate(String message) throws JsonizationException {
		log("receiving update: " + message);
		UpdateMessage update = new UpdateMessage(message);
		this.listener.update(update.getChange(), update.getClientSequenceNumber(), update.getServerSequenceNumber());
	}
	
	private native void log(Object o) /*-{
		$wnd.console.log(o);
	}-*/;

	

	public void join(String id) {
		this.id = id;
		setup(id);
		init(id);
	}
	
	public native void init(String id) /*-{
		$wnd.console.info("Preparing to join collaboration "+ id);
		$wnd.jQuery.cometd.init($wnd.location.protocol +"//" + $wnd.location.host + '/collabware/cometd');
	}-*/;

	public void sendUpdate(ComplexOperation complexOperation, int clientSequenceNumber, int serverSequenceNumber) {
		try {
			UpdateMessage update = new UpdateMessage(complexOperation, clientSequenceNumber, serverSequenceNumber);
			log("sending update: " + update.toString());
			publish("/service/clientChange/"+ id, update.toJson());
		} catch (JsonizationException e) {
			log(e);
		}
	}

	private native void publish(String channel, JavaScriptObject json) /*-{
		$wnd.jQuery.cometd.publish(channel, json);
	}-*/;

	public void onParticipantAdded(String message) {
		JSONObject value = JSONParser.parseStrict(message).isObject();
		listener.participantAdded(new ParticipantImpl(value.get("id").isString().stringValue(), value.get("imageUrl").isString().stringValue(), value.get("displayName").isString().stringValue()));
	}
	
	public String getClientId() {
		// TODO Auto-generated method stub
		return null;
	}

	public native void disconnect()   /*-{
		$wnd.jQuery.cometd.disconnect();
	}-*/;

	private void onDisconnected() {
		listener.disconnected();
	}
	
	@Override
	public native void fetchReplaySequence()  /*-{
		var that = this;
		var id = that.@collabware.web.client.CometdClientEndpoint::id;
		$wnd.jQuery.get('/collabware/rest/documents/'+id+'/replay', function(data, textStatus, jqXHR) {
			that.@collabware.web.client.CometdClientEndpoint::initReplay(Ljava/lang/String;)(data);
		});
	}-*/;
	
	public void initReplay(String json) {
		JSONObject replay = JSONParser.parseStrict(json).isObject();
		GwtJsonProvider gwtJsonProvider = new GwtJsonProvider();
		ComplexOperation init = deserializeOperation(replay.get("init").isObject(), gwtJsonProvider);
		List<ComplexOperation> sequence = new ArrayList<ComplexOperation>();
		JSONArray array = replay.get("replaySequence").isArray();
		for (int i = 0; i < array.size(); i++) {
			sequence.add(deserializeOperation(array.get(i).isObject(), gwtJsonProvider));
		}
		try {
			logger.info("Starting debug with init: "+init+" and sequence: " +sequence);
			listener.initReplay(init, sequence);
			logger.info("Now in replay mode.");
		} catch (Exception e) {
			logger.severe("Starting replay failed because: " + e.getMessage());
		}
	}
	
	private <OT,AT> ComplexOperation deserializeOperation(OT payload, JsonProvider<OT, AT> jsonProvider) {
		try {
			OperationDejsonizer<OT, AT> dejsonizer = new OperationDejsonizer<OT,AT>(new ModelProviderImpl(), jsonProvider);
			return (ComplexOperation) dejsonizer.dejsonize(payload);
		} catch (JsonizationException e) {
			throw new RuntimeException(e);
		}
	}

}
