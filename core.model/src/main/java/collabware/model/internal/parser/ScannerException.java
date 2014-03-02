package collabware.model.internal.parser;

public class ScannerException extends RuntimeException {

	private static final long serialVersionUID = -2641146185237666223L;

	public ScannerException(char expected, char charAtCurrentPosition) {
		super(String.format("Expected Character '%s', but was '%s'", expected, charAtCurrentPosition));
	}

}
