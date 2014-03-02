package collabware.example;

import collabware.registry.DuplicateRegistrationException;
import collabware.registry.EditorRegistry;
import collabware.registry.EditorReference;


public class ExampleEditor {
	private static final String DOCUMENT_TYPE = "collabware.example";
	private static final String NAME = "Example Editor";
	private static final String DESCRIPTION = "Example Editor";

	private EditorRegistry editorRegistry;

	private EditorReference ref = new EditorReference(DOCUMENT_TYPE, NAME, DESCRIPTION, ExampleEditor.class.getClassLoader());

	public ExampleEditor() {
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
