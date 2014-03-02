package collabware.collaboration.internal.client;

import java.util.List;

import collabware.api.document.ModifyableDocument;
import collabware.api.document.change.ComplexChangeEnded;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.OperationApplicationException;
import collabware.api.operations.OperationGenerator;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.Contextualizer;
import collabware.api.transform.CollisionException;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.Command;
import collabware.collaboration.internal.context.ModelStateImpl;
import collabware.collaboration.internal.context.VectorBasedContext;
import collabware.model.internal.ModelImpl;
import collabware.model.internal.ops.GraphOperationGenerator;
import collabware.transformer.Transformer;

public class ClientState {
	
	private final ModelStateImpl documentState = new ModelStateImpl();
	private final Transformer transformer;
	private final OperationGenerator generator = new GraphOperationGenerator();
	private final ModifyableDocument document = new ModelImpl("");
	private final ClientEndpoint clientEndpoint;

	private Contextualizer contextualizer = Contextualizer.INSTANCE;
	
	ClientState(ClientEndpoint clientEndpoint, Transformer transformer) {
		this.clientEndpoint = clientEndpoint;
		this.transformer = transformer;
	}

	void applyChange(Command command) {
		generator.reset();
		command.apply(document);
		document.notifyChange(new ComplexChangeEnded(command.getDescription()));
		sendChanges(generator.getGeneratedOperations(), command.getDescription());			
	}

	private void sendChanges(List<PrimitiveOperation> generatedOperations, String description) {
		if (!generatedOperations.isEmpty()) {
			ContextualizedComplexOperation change = contextualizer.createComplexOperation(documentState.asContextForClient(ClientImpl.CLIENT), description, generatedOperations);
			documentState.add(change);
			clientEndpoint.sendUpdate(change.getDecoratedOperation(), clientSequenceNumberOf(change), serverSequenceNumberOf(change));
		}
	}
	
	private int serverSequenceNumberOf(ContextualizedComplexOperation change) {
		return (((VectorBasedContext)change.getContext()).getSequenceNumbers()).get(ClientImpl.SERVER);
	}

	private int clientSequenceNumberOf(ContextualizedComplexOperation change) {
		return (((VectorBasedContext)change.getContext()).getSequenceNumbers()).get(ClientImpl.CLIENT);
	}
	
	void join(String collaborationId) {
		document.addChangeListener(generator);			
		clientEndpoint.join(collaborationId);
	}
	
	void init(ComplexOperation initSequence) {
		document.apply(initSequence);
	}
	
	void clear() {
		document.clear();
		document.notifyChange(new ComplexChangeEnded("Clearing the model."));
	}
	
	public ModifyableDocument getDocument() {
		return document;
	}
	
	public void update(ContextualizedComplexOperation change) throws CollisionException, OperationApplicationException {
		ComplexOperation transformed = transformer.transform(change, documentState);
		document.apply(transformed);
		documentState.add(change); 
	}
	
}