package collabware.model.internal.parser;

import static collabware.model.internal.parser.TokenType.BOOLEAN;
import static collabware.model.internal.parser.TokenType.CLOSING_SQUARE_BRACKET;
import static collabware.model.internal.parser.TokenType.COMMA;
import static collabware.model.internal.parser.TokenType.DOT;
import static collabware.model.internal.parser.TokenType.EOF;
import static collabware.model.internal.parser.TokenType.EQUALS;
import static collabware.model.internal.parser.TokenType.ID;
import static collabware.model.internal.parser.TokenType.NUMBER;
import static collabware.model.internal.parser.TokenType.OPENING_SQUARE_BRACKET;
import static collabware.model.internal.parser.TokenType.REF;
import static collabware.model.internal.parser.TokenType.STRING;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableNode;
public class ModelLiteralParser {

	private final ModelProvider modelProvider;
	private ModifyableModel model;
	private Tokenizer tokenizer;

	public ModelLiteralParser(ModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

	/*
	 * Grammar:
	 * modelLiteral  ::= simpleLiteral [',' modelLiteral];
	 * simpleLiteral ::= nodeId ['.' featureLiteral];
	 * featureLiteral ::= featureId attributeLiteral | referenceLiteral;
	 * attributeLiteral ::= '=' string;
	 * referenceLiteral ::= unaryReferenceLiteral | naryReferenceLiteral; 
	 * unaryReferenceLiteral ::= '->' nodeId;
	 * naryReferenceLiteral ::= '[' number ']' '->' nodeId;
	 * nodeId        ::=  identifier;
	 */
	
	
	public ModifyableModel modelFromLiteral(String type, String literal) {
		this.tokenizer = new Tokenizer(literal);
		model = (ModifyableModel) modelProvider.createDocument(type);
		parseModelLiteral();
		return model;
	}

	public void populateModelFromLiteral(ModifyableModel model, String literal) {
		this.tokenizer = new Tokenizer(literal);
		this.model = model;
		parseModelLiteral();
	}

	private void parseModelLiteral() {
		parseSimpleLiteral();
		if (currentTokenIsA(COMMA)) {
			consume(currentToken());
			parseModelLiteral();
		} else if (!currentTokenIsA(EOF)) {
			throw new ParserException(currentToken(), EOF);
		}
	}

	private void parseSimpleLiteral() {
		String nodeId = parseNodeId();
		if (currentTokenIsA(DOT)) {
			consume(currentToken());
			parseFeatureLiteral(nodeId);
		} else {
			model.getGraph().addNode(nodeId);
		}
	}

	private String parseNodeId() {
		if (currentTokenIsA(ID) || currentTokenIsA(STRING)) {
			Token<String> t = currentToken(String.class);
			consume(currentToken());
			return t.getValue();
		} else {
			throw new ParserException(currentToken(), ID);
		}
	}

	private void parseFeatureLiteral(String nodeId) {
		String featureId = parseFeatureId();
		Token<?> token = consume(currentToken());
		if (token.isA(EQUALS)) {
			parseAttributeLiteral(nodeId, featureId);				
		} else if (token.isA(REF)) {
			parseUnaryReferenceLiteral(nodeId, featureId);
		} else if (token.isA(OPENING_SQUARE_BRACKET)) {
			parseNaryReferenceLiteral(nodeId, featureId);
		} else {
			throw new ParserException(token, EQUALS, REF, OPENING_SQUARE_BRACKET);
		}
	}

	private void parseNaryReferenceLiteral(String nodeId, String featureId) {
		Token<Number> referenceIndex = consume(NUMBER);
		consume(CLOSING_SQUARE_BRACKET);
		consume(REF);
		String targetNodeId = parseNodeId();
		addReference(nodeId, featureId,referenceIndex.getValue().intValue(), targetNodeId);
	}

	private void addReference(String nodeId, String featureId, int intValue, String targetId) {
		ModifyableNode node = model.getGraph().getNode(nodeId);
		ModifyableNode target = model.getGraph().getNode(targetId);
		node.getNaryReferences().add(featureId, intValue, target);
	}

	@SuppressWarnings("unchecked")
	private <T> Token<T> consume(TokenType tokenType) {
		if (currentTokenIsA(tokenType)) {
			return (Token<T>) consume(currentToken());
		} else {
			throw new ParserException(currentToken(), tokenType);
		}
	}

	private String parseFeatureId() {
		if (currentTokenIsA(ID) || currentTokenIsA(STRING)) {
			Token<String> t = currentToken(String.class);
			consume(currentToken());
			return t.getValue();
		} else {
			throw new ParserException(currentToken(), ID);
		}
	}

	private void parseAttributeLiteral(String nodeId, String featureId) {
		if (currentTokenIsA(STRING)) {
			Token<String> t = consume(currentToken(String.class));
			setAttributeValue(nodeId, featureId, t.getValue());
		} else if (currentTokenIsA(BOOLEAN)) {
			Token<Boolean> t = consume(currentToken(Boolean.class));
			setAttributeValue(nodeId, featureId, t.getValue());
		} else if (currentTokenIsA(NUMBER)) {
			Token<Number> t = consume(currentToken(Number.class));
			setAttributeValue(nodeId, featureId, t.getValue());
		} else {
			throw new ParserException(currentToken(), STRING, BOOLEAN, NUMBER);
		}
	}

	private void setAttributeValue(String nodeId, String attributeName, boolean attributeValue) {
		ModifyableNode node = model.getGraph().getNode(nodeId);
		node.getAttributes().set(attributeName, attributeValue);
	}
	
	private void setAttributeValue(String nodeId, String attributeName, Number attributeValue) {
		ModifyableNode node = model.getGraph().getNode(nodeId);
		node.getAttributes().set(attributeName, attributeValue);
	}

	private void setAttributeValue(String nodeId, String attributeName, String featureValue) {
		ModifyableNode node = model.getGraph().getNode(nodeId);
		node.getAttributes().set(attributeName, featureValue);
	}

	private void parseUnaryReferenceLiteral(String nodeId, String featureId) {
		String targetNodeId = parseNodeId();
		setReferenceTarget(nodeId, featureId, targetNodeId);
	}

	private void setReferenceTarget(String nodeId, String featureId, String targetId) {
		ModifyableNode node = model.getGraph().getNode(nodeId);
		ModifyableNode target = model.getGraph().getNode(targetId);
		node.getUnaryReferences().set(featureId, target);
	}

	private boolean currentTokenIsA(TokenType tokenType) {
		return currentToken().isA(tokenType);
	}

	private Token<?> currentToken() {
		return tokenizer.getCurrentToken();
	}
	
	@SuppressWarnings("unchecked")
	private <T> Token<T> currentToken(Class<T> c) {
		return (Token<T>) tokenizer.getCurrentToken();
	}

	@SuppressWarnings("unchecked")
	private <T> Token<T> consume(Token<T> t) {
		return (Token<T>) tokenizer.consume(t);
	}


}
