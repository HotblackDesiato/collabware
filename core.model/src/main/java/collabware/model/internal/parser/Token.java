package collabware.model.internal.parser;

class Token<T> {
	
	private final T value;
	private final TokenType type;
	
	Token(T value, TokenType type) {
		super();
		this.value = value;
		this.type = type;
	}
	
	TokenType getType() {
		return type;
	}
	
	T getValue() {
		return value;
	}
	
	public String toString() {
		return "("+type+", "+ value +")";
	}

	boolean isA(TokenType t) {
		return type.equals(t);
	}
	
}
