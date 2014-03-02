package collabware.taskboard;


import collabware.registry.DuplicateRegistrationException;
import collabware.registry.EditorReference;
import collabware.registry.EditorRegistry;
// TODO We could factor out the mechanics and read the details form a config file.
public class TaskboardEditor {
	private static final String MODEL_TYPE_ID = "collabware.taskboard";
	private static final String MODEL_TYPE_NAME = "Taskboard";
	private static final String DESCRIPTION = "A collaborative taskboard";
	
	private EditorRegistry editorRegistry;
	
	private EditorReference modelType = new EditorReference(MODEL_TYPE_ID, MODEL_TYPE_NAME, DESCRIPTION, TaskboardEditor.class.getClassLoader());

	public TaskboardEditor() {
	}
	
	public void setEditorRegistry(EditorRegistry editorRegistry) {
		this.editorRegistry = editorRegistry;
	}

	public void startup() throws DuplicateRegistrationException {
		editorRegistry.register(modelType);
	}
	
	public void shutdown() {
		editorRegistry.unregister(modelType);
	}
	
}
