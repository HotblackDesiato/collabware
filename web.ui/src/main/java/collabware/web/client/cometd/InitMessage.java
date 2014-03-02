package collabware.web.client.cometd;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.client.ClientParticipant;
import collabware.collaboration.client.ParticipantImpl;
import collabware.model.internal.ModelProviderImpl;
import collabware.web.client.GwtJsonProvider;
import collabware.web.shared.JsonProtocolConstants;
import collabware.web.shared.JsonProvider;
import collabware.web.shared.JsonizationException;
import collabware.web.shared.OperationDejsonizer;

public class InitMessage {

	private static Logger logger = Logger.getLogger("collabware.web.client.cometd.InitMessage");
	
	private final ComplexOperation initSequence;
	private final List<ClientParticipant> participants;
	private final ClientParticipant localParticipant;
	
	public <OT,AT> InitMessage(String jsonPayload, JsonProvider<OT,AT> jsonProvider) throws JsonizationException {
		logger.info("parsing " + jsonPayload);
		OT payload = jsonProvider.parse(jsonPayload);
		initSequence = deserializeInitSequence(payload, jsonProvider);
		participants = deserializeParticipants(payload, jsonProvider);
		localParticipant = deserializeLocalParticipant(payload, jsonProvider);
	}

	public InitMessage(String jsonPayload) throws JsonizationException {
		this(jsonPayload, new GwtJsonProvider());
	}
	
	private <OT,AT> ClientParticipant deserializeLocalParticipant(OT payload, JsonProvider<OT, AT> jsonProvider) throws JsonizationException {
		OT jsonParticipant = jsonProvider.getObject(payload, JsonProtocolConstants.LOCAL_PARTICIPANT);
		return deserializeParticipant(jsonParticipant, jsonProvider);
	}

	private <OT, AT> ClientParticipant deserializeParticipant(OT jsonParticipant, JsonProvider<OT, AT> jsonProvider) throws JsonizationException {
		String displayName = jsonProvider.getString(jsonParticipant, "displayName");
		String id = jsonProvider.getString(jsonParticipant, "id");
		String imageUrl = jsonProvider.getString(jsonParticipant, "imageUrl");
		return new ParticipantImpl(id, imageUrl, displayName);
	}

	private <OT,AT> List<ClientParticipant> deserializeParticipants(OT payload,  JsonProvider<OT, AT> jsonProvider) throws JsonizationException {
		AT array = jsonProvider.getArray(payload, JsonProtocolConstants.ALL_PARTICIPANT);
		List<ClientParticipant> participants = new ArrayList<ClientParticipant>();
		for (int i = 0; i < jsonProvider.length(array); i++) {
			participants.add(deserializeParticipant(jsonProvider.getObject(array, i), jsonProvider));
		}
		return participants;
	}

	private <OT,AT> ComplexOperation deserializeInitSequence(OT payload, JsonProvider<OT, AT> jsonProvider) {
		try {
			logger.info("dejsonizing init sequence " + payload.toString());
			OperationDejsonizer<OT, AT> dejsonizer = new OperationDejsonizer<OT,AT>(new ModelProviderImpl(), jsonProvider);
			return (ComplexOperation) dejsonizer.dejsonize(jsonProvider.getObject(payload, JsonProtocolConstants.INIT_SEQUENCE));
		} catch (JsonizationException e) {
			logger.severe(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public ComplexOperation getInitSequence() {
		return initSequence;
	}
	
	public List<ClientParticipant> getParticipants() {
		return participants;
	}
	
	public ClientParticipant getLocalParticipant() {
		return localParticipant;
	}
}
