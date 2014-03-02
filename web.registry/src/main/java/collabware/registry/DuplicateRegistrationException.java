package collabware.registry;


public class DuplicateRegistrationException extends Exception {
	private static final String EDITOR_ALREADY_REGISTERED = "An editor has already been registered for Model id '%s'";

	public DuplicateRegistrationException(EditorReference modelType) {
		super(String.format(EDITOR_ALREADY_REGISTERED, modelType.getContentType()));
	}

	private static final long serialVersionUID = 8399506375474201497L;

}
