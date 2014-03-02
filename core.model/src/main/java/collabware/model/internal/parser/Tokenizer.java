package collabware.model.internal.parser;

import java.util.HashSet;

class Tokenizer  {
	
	private static final char SPACE = ' ';
	private static final String FALSE = "false";
	private static final String TRUE = "true";
	private static final char QUOTE = '\'';
	private static final String ARROW = "->";
	private static final char EQUALS = '=';
	private static final char COMMA = ',';
	private static final char DOT = '.';
	private static final char OPENING_SQUARE_BRACKET = '[';
	private static final char CLOSING_SQUARE_BRACKET = ']';
	@SuppressWarnings("serial")
	private static final HashSet<Character> terminators = new HashSet<Character>() {{
			add(DOT); add(COMMA); add(EQUALS); add('-'); add(SPACE); add(OPENING_SQUARE_BRACKET);
		}};;
	
	private final Scanner scanner;
	private Token<?> currentToken = null;
	
	Tokenizer(String literal) {
		scanner = new Scanner(literal);
	}

	private Token<?> next() {
		return tokenizeNextToken();
	}

	private Token<?> tokenizeNextToken() {
		scanner.skipSpaces();
		
		if (scanner.reachedEndOfLine())	return new Token<String>("", TokenType.EOF);
		if (scanner.canMatch(DOT))    return tokenizeDot();
		if (scanner.canMatch(COMMA))  return tokenizeComma();
		if (scanner.canMatch(EQUALS)) return tokenizeEquals();
		if (scanner.canMatch(ARROW))  return tokenizeRefAssignement();
		if (scanner.canMatch(QUOTE))  return tokenizeQuotedString();
		if (scanner.canMatch(TRUE))   return tokenizeBoolean(TRUE);
		if (scanner.canMatch(FALSE))  return tokenizeBoolean(FALSE);
		if (scanner.canMatch(OPENING_SQUARE_BRACKET))  return tokenizeOpeningSquareBracket();
		if (scanner.canMatch(CLOSING_SQUARE_BRACKET))  return tokenizeClosingSquareBracket();
		if (scanner.canMatchNumber()) return tokenizeNumber();
		else 						  return tokenizeIdentifier();
	}
	
	private Token<?> tokenizeClosingSquareBracket() {
		scanner.scan(CLOSING_SQUARE_BRACKET);
		return new Token<Character>(CLOSING_SQUARE_BRACKET, TokenType.CLOSING_SQUARE_BRACKET);
	}

	private Token<?> tokenizeOpeningSquareBracket() {
		scanner.scan(OPENING_SQUARE_BRACKET);
		return new Token<Character>(OPENING_SQUARE_BRACKET, TokenType.OPENING_SQUARE_BRACKET);
	}

	private Token<Character> tokenizeDot() {
		scanner.scan(DOT);
		return new Token<Character>(DOT, TokenType.DOT);
	}

	private Token<Character> tokenizeComma() {
		scanner.scan(COMMA);
		return new Token<Character>(COMMA, TokenType.COMMA);
	}

	private Token<Character> tokenizeEquals() {
		scanner.scan(EQUALS);
		return new Token<Character>(EQUALS, TokenType.EQUALS);
	}

	private Token<String> tokenizeRefAssignement() {
		scanner.scan(ARROW);
		return new Token<String>(ARROW, TokenType.REF);
	}

	private Token<Boolean> tokenizeBoolean(String booleanSymbol) {
		scanner.scan(booleanSymbol);
		return new Token<Boolean>(Boolean.parseBoolean(booleanSymbol), TokenType.BOOLEAN);
	}

	private Token<Number> tokenizeNumber() {
		StringBuilder number = new StringBuilder();
		number.append( scanIntegerPart() );
		if (hasDecimalPart()) {
			number.append( scanDecimalPart() );
			return new Token<Number>(Double.parseDouble(number.toString()), TokenType.NUMBER);
		} else {
			return new Token<Number>(Integer.parseInt(number.toString()), TokenType.NUMBER);						
		}
	}

	private boolean hasDecimalPart() {
		return scanner.canMatch(DOT);
	}

	private String scanDecimalPart() {
		scanner.scan(DOT);
		return DOT + scanner.scanDigitSequence();
	}

	private String scanIntegerPart() {
		return scanner.scanDigitSequence();
	}

	private Token<String> tokenizeQuotedString() {
		String characterSequence = scanner.scanQuotedString(QUOTE);
		return new Token<String>(characterSequence, TokenType.STRING);
	}

	private Token<String> tokenizeIdentifier() {
		String string = scanner.scanCharsTerminatedBy(terminators);
		return new Token<String>(string, TokenType.ID);
	}

	Token<?> getCurrentToken() {
		if (currentToken == null) {
			currentToken = next();
		}
		return currentToken;
	}
	
	Token<?> consume(Token<?> t) {
		Token<?> consumedToken = currentToken;
		if (t == currentToken) {
			currentToken = next();
		}
		return consumedToken;
	}
}
