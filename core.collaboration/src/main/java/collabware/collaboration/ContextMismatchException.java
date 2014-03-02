package collabware.collaboration;

import collabware.api.document.DocumentException;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.api.operations.context.DocumentState;

public class ContextMismatchException extends DocumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1910324123693046565L;


	public ContextMismatchException(ContextualizedOperation op, DocumentState modelState) {
		super(String.format("Context of operation %s does not match model state %s", op, modelState));
	}

}
