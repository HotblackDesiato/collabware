package collabware.web.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import collabware.collaboration.client.ClientParticipant;
@ExportPackage("collabware")
@Export("Participant")
public class ParticipantWrapper implements Exportable {

	private ClientParticipant participant;

	public ParticipantWrapper(ClientParticipant localParticipant) {
		this.participant = localParticipant;
	}
	
	@Export
	public String getId() {
		return participant.getId();
	}

	@Export
	public String getImageUrl() {
		return participant.getImageUrl();
	}

	@Export
	public String getDisplayName() {
		return participant.getDisplayName();
	}

}
