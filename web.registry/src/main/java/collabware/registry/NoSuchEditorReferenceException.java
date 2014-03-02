package collabware.registry;

public class NoSuchEditorReferenceException extends Exception {

	private static final long serialVersionUID = 8511729152409999834L;
	private static final String NO_EDITOR_REGISTERED = "No Editor registered for model type id '%s'";

	public NoSuchEditorReferenceException(String modelTypeId) {
		super(String.format(NO_EDITOR_REGISTERED, modelTypeId));
	}


}
