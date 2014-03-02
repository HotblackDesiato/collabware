package collabware.api.operations;

public class OperationApplicationException extends Exception {

	private static final long serialVersionUID = 7455534568956894277L;

	private static String renderMessage(Operation operation, Exception e) {
		return "Application of operation '"+operation+"' failed because:/n" + e.getMessage();
	}
	
	public OperationApplicationException(Operation operation, Exception e) {
		super(renderMessage(operation, e), e);
	}

	public OperationApplicationException(String message) {
		super(message);
	}


}
