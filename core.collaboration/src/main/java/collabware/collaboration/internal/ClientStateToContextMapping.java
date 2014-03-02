package collabware.collaboration.internal;

import java.util.ArrayList;
import java.util.List;

import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.collaboration.internal.context.VectorBasedContext;

class ClientStateToContextMapping {

	private final List<Context> serverStates = new ArrayList<Context>();
	private final int clientNumber;
	
	ClientStateToContextMapping(Context context) {
		this.serverStates.add(context);
		this.clientNumber = context.getClientNumber();
	}

	Context getContextForServerState(int i) {
		VectorBasedContext context = (VectorBasedContext) this.serverStates.get(i);
		return new VectorBasedContext(clientNumber,context.getSequenceNumbers());
	}

	void addClientOperation(ContextualizedOperation o, int serverSequenceNumber) {
		if (o.getContext().getClientNumber() != clientNumber)
			throw new IllegalArgumentException(String.format("Can only except operations by client with number %s, but was %s", clientNumber, o.getContext().getClientNumber()));
		
		for (int i = serverSequenceNumber; i < serverStates.size(); i++) {
			addOperationsToServerState(o, i);
		}
	}

	private void addOperationsToServerState(ContextualizedOperation o, int i) {
		Context vector = this.serverStates.get(i);
		vector = vector.including(o);
		this.serverStates.set(i, vector);
	}

	void addNewServerState(Context contextVector) {
		this.serverStates.add(contextVector);
	}

	int getNumberOfServerStates() {
		return serverStates.size();
	}

}
