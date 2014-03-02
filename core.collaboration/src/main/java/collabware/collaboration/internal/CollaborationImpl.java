package collabware.collaboration.internal;
import static collabware.utils.Asserts.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.DocumentState;
import collabware.api.transform.CollisionException;
import collabware.collaboration.Change;
import collabware.collaboration.ChangeListener;
import collabware.collaboration.Client;
import collabware.collaboration.CollaborationDetails;
import collabware.collaboration.ConflictingOperationsException;
import collabware.collaboration.ContextMismatchException;
import collabware.collaboration.Participant;
import collabware.collaboration.ReplaySequence;
import collabware.collaboration.internal.context.ModelStateImpl;
import collabware.transformer.Transformer;
import collabware.userManagement.User;
import collabware.utils.RandomId;

/**
 * 
 */
class CollaborationImpl implements ModifyableCollaboration {

	private final String id = RandomId.createId(8);
	private final String name;
	private final Participant owner;
	private final List<Participant> participants = new ArrayList<Participant>();
	private final ModifyableDocument model;
	
	private final ModelStateImpl state = new ModelStateImpl();
	private final List<ComplexOperation> history = new ArrayList<ComplexOperation>();
	
	private final Transformer transformer;
	private final ParticipantRegistry participantRegistry = new ParticipantRegistry(this);
	private final Collection<ChangeListener> listeners = new ArrayList<ChangeListener>();
	private Date createdOn;
	private Change lastChange;
	
	CollaborationImpl(ModifyableDocument model, ParticipantImpl owner, CollaborationDetails details, Transformer transformer) {
		assertNotNull("model", model); assertNotNull("owner", owner);
		assertNotNull("details", details); assertNotNull("transformer", transformer);
		
		this.createdOn = new Date(System.currentTimeMillis());
		this.name = details.getName();
		this.model = model;
		this.owner = owner;
		this.transformer = transformer;
		this.lastChange = new ChangeImpl("Created Document", this, owner.getUser());
		owner.addOwnedCollaboration(this);
		this.addParticipant(owner);
	}

	public String getId() {
		return id;
	}

	public Participant getOwner() {
		return owner;
	}

	public synchronized Client join(Participant participant) {
		assertNotNull("participant", participant);
		return participantRegistry.registerParticipant(participant);
	}

	public List<Participant> getParticipants() {
		return Collections.unmodifiableList(participants);
	}

	public synchronized void leave(Client client) {
		assertNotNull("client", client);
		participantRegistry.unregisterClient(client);
	}

	public ModifyableDocument getDocument() {
		return model;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return model.getContentType();
	}

	@Override
	public synchronized void apply(ContextualizedComplexOperation operation, User user) throws ConflictingOperationsException {
		assertNotNull("operation", operation);
		try {
			ContextualizedComplexOperation transformed = transformer.transform(operation, state);
			if (state.equals(transformed.getContext())) {
				model.apply(transformed);
				state.add(operation);
				history.add(transformed);
			} else {
				throw new ContextMismatchException(transformed, state);
			}
			broadcastChange(transformed);
			lastChange = new ChangeImpl(operation.getDescription(), this, user);
			notifyChangeListeners(lastChange);
		} catch (CollisionException e) {
			throw new ConflictingOperationsException();
		}	
	}

	private void notifyChangeListeners(Change change) {
		for (ChangeListener listener: listeners) {
			listener.changeApplied(change);
		}
	}

	@Override
	public Set<Participant> getActiveParticipants() {
		return participantRegistry.getRegisteredParticipants();
	}

	private void broadcastChange(ContextualizedComplexOperation operation) {
		for (ClientProxy c: participantRegistry.getRegisteredClients()) {
			if (clientIsNotOriginatorOfOperation(c, operation))
				c.sendToClient(operation);
		}
	}

	private boolean clientIsNotOriginatorOfOperation(Client c, ContextualizedComplexOperation operation) {
		return c.getClientNumber() != operation.getContext().getClientNumber();
	}
	
	public String toString() {
		return String.format("Collaboration {name='%s', id='$s'}", this.name, this.id);
	}

	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void addParticipant(Participant participant) {
		if (!participants.contains(participant)) {
			participants.add(participant);
			participant.addParticipatingCollaboration(this);
			for (ChangeListener listener: listeners) {
				listener.participantAdded(this, participant);
			}
		}
	}

	public DocumentState getState() {
		return state;
	}

	@Override
	public ReplaySequence getReplaySequence() {
		ComplexOperation init;
		List<ComplexOperation> subList;
		if (history.size() == 0) {
			init = new ComplexOperationImpl("", Collections.singletonList((PrimitiveOperation)NoOperation.NOP));
			subList = Collections.emptyList();
		} else {
			init = history.get(0);
			subList = history.subList(1, history.size());			
		}
		return new ReplaySequenceImpl(init, subList);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollaborationImpl other = (CollaborationImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public ComplexOperation getInitializeOperation() {
		return model.asOperation();
	}

	@Override
	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public Change getLastChange() {
		return lastChange;
	}
}