package collabware.web.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import collabware.collaboration.client.ClientParticipant;

@Export
public class LocalParticipantWrapper extends ParticipantWrapper implements Exportable {

	private SessionWrapper session;

	public LocalParticipantWrapper(SessionWrapper session, ClientParticipant localParticipant) {
		super(localParticipant);
		this.session = session;
	}
	
	@Export
	public ParticipantWrapper[] getContacts() {
		return session.getContacts();
	}

}
