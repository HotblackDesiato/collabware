package collabware.ideawall;

import collabware.registry.DuplicateRegistrationException;
import collabware.registry.EditorRegistry;
import collabware.registry.EditorReference;


public class IdeaWall {
	private static final String DOCUMENT_TYPE = "collabware.ideawall";
	private static final String NAME = "Ideawall";
	private static final String DESCRIPTION = "A collaborative idea wall";

	private EditorRegistry editorRegistry;

	private EditorReference ref = new EditorReference(DOCUMENT_TYPE, NAME, DESCRIPTION, IdeaWall.class.getClassLoader());

	public IdeaWall() {
	}

	public void setEditorRegistry(EditorRegistry editorRegistry) {
		this.editorRegistry = editorRegistry;
	}

	public void startup() throws DuplicateRegistrationException {
		editorRegistry.register(ref);
	}

	public void shutdown() {
		editorRegistry.unregister(ref);
	}

}
