package collabware.web.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.ClientEndpointListener;
import collabware.collaboration.client.ClientParticipant;
import collabware.collaboration.client.ParticipantImpl;

@ExportPackage("collabware")
@Export(value="LocalSession",all=true)
public class LocalClient extends SessionWrapper implements Exportable {

	private static class MockClientEndpoint implements ClientEndpoint {

		private ClientEndpointListener listener;

		public void addClientEndpointListener(ClientEndpointListener listener) {
			this.listener = listener;
		}

		public void join(String id) {
			ComplexOperation initSequence = new ComplexOperationImpl("", Arrays.asList((PrimitiveOperation)NoOperation.NOP));
			List<ClientParticipant> participants = Collections.emptyList();
			ClientParticipant localParticipant = new ParticipantImpl("42", "", "Justin Case");
			listener.initialize(initSequence, participants , localParticipant);
		}

		public void sendUpdate(ComplexOperation complexOperation, int clientSequenceNumber, int serverSequenceNumber) {
			// TODO Auto-generated method stub

		}

		public String getClientId() {
			return "0815";
		}

		public void disconnect() {
			listener.disconnected();
		}

		@Override
		public void fetchReplaySequence() {
			// TODO Auto-generated method stub
			
		}

	}

	public LocalClient() {
		super(new MockClientEndpoint());
	}
	
}
