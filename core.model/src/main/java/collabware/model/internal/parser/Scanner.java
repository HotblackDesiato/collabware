package collabware.model.internal.parser;

import java.util.HashSet;

class Scanner {
	
	private final String literal;
	private int currentPosition = 0;

	Scanner(String literal) {
		this.literal = literal;
	}

	private char charAtCurrentPosition() {
		return literal.charAt(currentPosition);
	}

	boolean canMatch(String symbol) {
		int i = currentPosition;
		int j = 0;
		if (currentPosition + symbol.length() > literal.length())
			return false;
		while (j < symbol.length() && i < literal.length()) {
			if (literal.charAt(i) != symbol.charAt(j))
				return false;
			i++;
			j++;
		}
		return true;
	}

	boolean canMatch(char symbol) {
		if (reachedEndOfLine())
			return false;
		return charAtCurrentPosition() == symbol;
	}

	boolean reachedEndOfLine() {
		return currentPosition > literal.length()-1;
	}

	private void advanceCurrentPosition() {
		currentPosition++;
	}

	boolean canMatchNumber() {
		if (charAtCurrentPosition() >= '0' && charAtCurrentPosition() <= '9')
			return true;
		return false;
	}

	String scanCharsTerminatedBy(HashSet<Character> terminators) {		
		StringBuilder nodeId = new StringBuilder(literal.length());
		while (currentPosition < literal.length() && !terminators.contains(charAtCurrentPosition())) {
			nodeId.append(charAtCurrentPosition());
			advanceCurrentPosition();					
		}
		String string = nodeId.toString();
		return string;
	}

	String scanDigitSequence() {
		StringBuilder number = new StringBuilder();
		while (!reachedEndOfLine() && charAtCurrentPosition() >= '0' && charAtCurrentPosition() <= '9') {
			number.append(charAtCurrentPosition());
			advanceCurrentPosition();
		}
		return number.toString();
	}
	
	void scan(String symbol) {
		int j = 0;
		while (j < symbol.length() && currentPosition < literal.length()) {
			if (charAtCurrentPosition() != symbol.charAt(j))
				throw new ScannerException(symbol.charAt(j), charAtCurrentPosition());
			advanceCurrentPosition();
			j++;
		}
	}

	void scan(char symbol) {
		if (charAtCurrentPosition() != symbol)
			throw new ScannerException(symbol, charAtCurrentPosition());
		advanceCurrentPosition();
	}

	void skipSpaces() {
		while (currentPosition < literal.length() && charAtCurrentPosition() == ' ') {
			currentPosition++;
		}
	}

	String scanCharacterSequenceTerminatedBy(char termination) {
		StringBuilder characterSequence = new StringBuilder(literal.length());
		char escapeSymbol = '\\';
		boolean escaping = false;
		
		while (!reachedEndOfLine() && (charAtCurrentPosition() != termination || escaping)) {
			if (charAtCurrentPosition() == escapeSymbol && !escaping) {
				escaping = true;
			} else {
				characterSequence.append(charAtCurrentPosition());
				escaping = false;
			}
			advanceCurrentPosition();
		}
		return characterSequence.toString();
	}

	String scanQuotedString(char quoteCharacter) {
		scan(quoteCharacter);
		String characterSequence = scanCharacterSequenceTerminatedBy(quoteCharacter);
		scan(quoteCharacter);
		return characterSequence;
	}
}