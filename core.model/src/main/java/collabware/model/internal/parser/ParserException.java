package collabware.model.internal.parser;

public class ParserException extends RuntimeException {

	private static final long serialVersionUID = -4951126255548271535L;

	public ParserException(Token<?> currentToken, TokenType expected) {
		super(String.format("Expected Token %s, but was %s", expected, currentToken.getType()));
	}
	
	public ParserException(Token<?> currentToken, TokenType ...expectedTokens) {
		super(String.format("Expected Token %s, but was %s", enumerate(expectedTokens), currentToken.getType()));
	}
	
	private static String enumerate(TokenType... expectedTokens) {
		StringBuilder expectedTokenString = new StringBuilder();
		for (TokenType t: expectedTokens) {
			expectedTokenString.append(t.name());
			if (t != expectedTokens[expectedTokens.length-1]) {
				expectedTokenString.append(" or ");				
			}
		}
		return expectedTokenString.toString();
	}

}
