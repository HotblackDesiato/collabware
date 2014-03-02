package collabware.collaboration.internal;

import static collabware.utils.Asserts.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import collabware.api.document.DocumentProvider;
import collabware.api.document.ModifyableDocument;
import collabware.collaboration.Change;
import collabware.collaboration.ChangeListener;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationDetails;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.NoSuchCollaborationException;
import collabware.collaboration.Participant;
import collabware.transformer.TransformationProvider;
import collabware.userManagement.User;


public class CollaborationProviderImpl implements CollaborationProvider, ChangeListener {

	private final Map<String, Collaboration> knownCollaborations = new HashMap<String, Collaboration>();
	private final Map<String, Participant> knownParticipants = new HashMap<String, Participant>();

	@Autowired
	private DocumentProvider modelProvider;
	@Autowired
	private TransformationProvider transformationProvider;
	private Collection<ChangeListener> listeners = new ArrayList<ChangeListener>();
	
	public void setModelProvider(DocumentProvider modelProvider) {
		this.modelProvider = modelProvider;
	}
	
	public void setTransformationProvider(TransformationProvider transformationProvider) {
		this.transformationProvider = transformationProvider;
	}

	public void startup() {
		if (modelProvider == null || transformationProvider == null)
			throw new IllegalStateException("modelProvider and transformationProvider must be set before startup.");
	}

	public Collaboration createCollaboration(CollaborationDetails details, Participant owner) {
		assertNotNull("details", details); assertNotNull("owner", owner);
		
		ModifyableDocument model = modelProvider.createDocument(details.getType());

		CollaborationImpl collaboration = new CollaborationImpl(model, (ParticipantImpl)owner, details, transformationProvider.createTransformer());
		collaboration.addChangeListener(this);
		knownCollaborations.put(collaboration.getId(), collaboration);
		return collaboration;
	}

	private boolean collaborationExistsWith(String id) {
		return knownCollaborations.containsKey(id);
	}

	public Collaboration getCollaboration(String id) throws NoSuchCollaborationException {
		if (collaborationExistsWith(id))
			return knownCollaborations.get(id);
		else 
			throw new NoSuchCollaborationException();
	}

	public Collection<Collaboration> getAllCollaborations() {
		return Collections.unmodifiableCollection(knownCollaborations.values());
	}

	public Participant getParticipant(User user) {
		assertNotNull("user", user);
		
		if (!this.knownParticipants.containsKey(user.getUserName()))
			knownParticipants.put(user.getUserName(), new ParticipantImpl(user));
		return knownParticipants.get(user.getUserName());
	}

	@Override
	public boolean hasCollaboration(String collaborationId) {
		return knownCollaborations.containsKey(collaborationId);
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void changeApplied(Change change) {
		for (ChangeListener listener: listeners) {
			listener.changeApplied(change);
		}
	}

	@Override
	public void participantAdded(Collaboration collaboration, Participant addedParticipant) {
		for (ChangeListener listener: listeners) {
			listener.participantAdded(collaboration, addedParticipant);
		}
	}
}
