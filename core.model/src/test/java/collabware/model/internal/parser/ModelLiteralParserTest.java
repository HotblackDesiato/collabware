package collabware.model.internal.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import collabware.model.Model;
import collabware.model.ModelProvider;
import collabware.model.graph.Node;
import collabware.model.internal.ModelProviderImpl;

public class ModelLiteralParserTest {

	private ModelLiteralParser modelLiteralParser;
	
	@Before
	public void setup() {
		ModelProvider modelProvider = new ModelProviderImpl();
		modelLiteralParser = new ModelLiteralParser(modelProvider);		
	}

	@Test
	public void parseOneNode() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1");
		
		assertNotNull(m.getGraph().getNode("n1"));
	}
	
	@Test
	public void parseOneNodeEscaped() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "'n1'");
		
		assertNotNull(m.getGraph().getNode("n1"));
	}
	
	@Test
	public void parseOneNodeEscapedWithSpace() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "'n 1'");
		
		assertNotNull(m.getGraph().getNode("n 1"));
	}

	@Test
	public void parseOneNodeWithQuoteInName() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "'n\\'1'");
		
		assertNotNull(m.getGraph().getNode("n'1"));
	}

	@Test
	public void parseOneNodeWithEscapeCharaterInQuotedName() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "'n\\\\1'");
		
		assertNotNull(m.getGraph().getNode("n\\1"));
	}
	
	@Test
	public void parseOneNodeWithTrailingSpace() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1  ");
		
		assertNotNull(m.getGraph().getNode("n1"));
	}
	
	@Test
	public void parseTwoNodes() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n2");
		
		assertNotNull(m.getGraph().getNode("n1"));
		assertNotNull(m.getGraph().getNode("n2"));
	}

	@Test
	public void parseOneNodeAndAttribute() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr='blah'");
		
		Node node = m.getGraph().getNode("n1");
		assertEquals("blah", node.getAttributes().get("attr"));
	}

	@Test
	public void parseOneNodeAndAttributeWithEscapedName() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.'attr'='blah'");
		
		Node node = m.getGraph().getNode("n1");
		assertEquals("blah", node.getAttributes().get("attr"));
	}
	
	@Test
	public void parseOneNodeAndAttributeWithEscapedQuotesInName() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.'at\\'tr'='blah'");
		
		Node node = m.getGraph().getNode("n1");
		assertEquals("blah", node.getAttributes().get("at'tr"));
	}

	@Test
	public void parseOneNodeAndAttributeWithConfusingName() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.'n1.attr'='blah'");
		
		Node node = m.getGraph().getNode("n1");
		assertEquals("blah", node.getAttributes().get("n1.attr"));
	}
	
	@Test
	public void parseOneNodeAndTwoAttributes() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr='blah', n1.foo='bar'");
		
		Node node = m.getGraph().getNode("n1");
		assertEquals("blah", node.getAttributes().get("attr"));
		assertEquals("bar", node.getAttributes().get("foo"));
	}
	
	@Test
	public void parseTwoNodeAndTwoAttributes() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n2, n1.attr='blah', n2.foo='bar'");
		
		Node node1 = m.getGraph().getNode("n1");
		Node node2 = m.getGraph().getNode("n2");
		assertEquals("blah", node1.getAttributes().get("attr"));
		assertEquals("bar", node2.getAttributes().get("foo"));
	}
	
	@Test
	public void parseTwoNodeAndReference() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n2, n1.ref->n2");
		
		Node node1 = m.getGraph().getNode("n1");
		Node node2 = m.getGraph().getNode("n2");
		assertEquals(node2, node1.getUnaryReferences().get("ref"));
	}
	
	@Test
	public void parseTwoNodeAndNaryReference() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n2, n1.ref[0]->n2");
		
		Node node1 = m.getGraph().getNode("n1");
		Node node2 = m.getGraph().getNode("n2");
		assertEquals(node2, node1.getNaryReferences().get("ref").get(0));
	}
	
	@Test
	public void parseThreeNodeAndNaryReference() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n2, n3, n1.ref[0]->n2, n1.ref[1]->n3");
		
		Node node1 = m.getGraph().getNode("n1");
		Node node2 = m.getGraph().getNode("n2");
		Node node3 = m.getGraph().getNode("n3");
		assertEquals(node2, node1.getNaryReferences().get("ref").get(0));
		assertEquals(node3, node1.getNaryReferences().get("ref").get(1));
	}

	@Test
	public void parseTwoNodeAndReferenceWithEscapedName() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n2, n1.'ref'->n2");
		
		Node node1 = m.getGraph().getNode("n1");
		Node node2 = m.getGraph().getNode("n2");
		assertEquals(node2, node1.getUnaryReferences().get("ref"));
	}
	@Test
	public void parseTwoNodeAndReferenceWithQuotesInEscapedName() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n2, n1.'re\\'f'->n2");
		
		Node node1 = m.getGraph().getNode("n1");
		Node node2 = m.getGraph().getNode("n2");
		assertEquals(node2, node1.getUnaryReferences().get("re'f"));
	}
	
	@Test
	public void parseTwoNodeAndTwoReferences() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n2, n1.ref->n2, n2.ref->n1");
		
		Node node1 = m.getGraph().getNode("n1");
		Node node2 = m.getGraph().getNode("n2");
		assertEquals(node2, node1.getUnaryReferences().get("ref"));
		assertEquals(node1, node2.getUnaryReferences().get("ref"));
	}
	@Test
	public void parseTwoNodeAndAttributesAndReferences() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr='blah', n2, n1.ref->n2, n2.ref->n1");
		
		Node node1 = m.getGraph().getNode("n1");
		Node node2 = m.getGraph().getNode("n2");
		assertEquals("blah", node1.getAttributes().get("attr"));
		assertEquals(node2, node1.getUnaryReferences().get("ref"));
		assertEquals(node1, node2.getUnaryReferences().get("ref"));
	}
	
	@Test
	public void parseSetBoolenAttributeToTrue() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr=true");
		
		Node node1 = m.getGraph().getNode("n1");
		assertEquals(true, node1.getAttributes().get("attr"));
	}

	@Test
	public void parseSetBoolenAttributeToFalse() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr=false");
		
		Node node1 = m.getGraph().getNode("n1");
		assertEquals(false, node1.getAttributes().get("attr"));
	}
	
	@Test
	public void parseSetIntAttributeTo0() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr=0");
		
		Node node1 = m.getGraph().getNode("n1");
		assertEquals(0, node1.getAttributes().get("attr"));
	}
	
	@Test
	public void parseSetIntAttributeTo1234567890() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr=1234567890");
		
		Node node1 = m.getGraph().getNode("n1");
		assertEquals(1234567890, node1.getAttributes().get("attr"));
	}

	
	@Test
	public void parseSetIntAttributeTo9() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr=9");
		
		Node node1 = m.getGraph().getNode("n1");
		assertEquals(9, node1.getAttributes().get("attr"));
	}
	
	@Test
	public void parseSetNumberAttributeTo1234567890Dot1234567890() throws Exception {
		Model m = modelLiteralParser.modelFromLiteral("", "n1, n1.attr=1234567890.1234567890");
		
		Node node1 = m.getGraph().getNode("n1");
		assertEquals(1234567890.1234567890, node1.getAttributes().get("attr"));
	}
}
