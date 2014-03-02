package collabware.web.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import collabware.api.document.PrimitiveOperationDeserializer;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.Operation;
import collabware.api.operations.PrimitiveOperation;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;
import collabware.web.cometd.utils.ServerJsonProvider;
import collabware.web.shared.JsonizationException;
import collabware.web.shared.OperationDejsonizer;


public class OperationDejsonizerTest {
	
	private PrimitiveOperationDeserializer operationsProvider = new ModelProviderImpl();
	private OperationDejsonizer<JSONObject, JSONArray> dejsonizer = new OperationDejsonizer<JSONObject, JSONArray>(operationsProvider, new ServerJsonProvider());
	
	@Test
	public void jsonizeNoOperation() throws Exception {
		assertJsonizedEquals(nop(), "{t:'no'}");
	}

	private NoOperation nop() {
		return (NoOperation) NoOperation.NOP;
	}

	private void assertJsonizedEquals(Operation op, String string)	throws JsonizationException, JSONException {
		Operation opFromJson = dejsonizer.dejsonize(new JSONObject(string));
		assertEquals(op, opFromJson);
	}

	@Test
	public void jsonizeAddNodeOperation() throws Exception {
		assertJsonizedEquals(addNode("n42"), "{t:'an', n:'n42'}");
	}

	private AddNodeOperation addNode(String nodeId) {
		return new AddNodeOperation(nodeId);
	}

	@Test
	public void jsonizeRemoveNodeOperation() throws Exception {
		assertJsonizedEquals(remNode("foobar"), "{t:'rn', n:'foobar'}");
	}

	private RemoveNodeOperation remNode(String nodeId) {
		return new RemoveNodeOperation(nodeId);
	}

	@Test
	public void jsonizeSetAttributeOperationWithFloatValues() throws Exception {
		assertJsonizedEquals(setAttr("foobar", "someAttr", 2.56, 3.14), "{t:'sa', n:'foobar', a:'someAttr', ov:2.56, nv:3.14}");
	}
	@Test
	public void jsonizeSetAttributeOperationWithNumericValues() throws Exception {
		assertJsonizedEquals(setAttr("foobar", "someAttr", null, (Number) 42), "{t:'sa', n:'foobar', a:'someAttr', nv:42}");
	}

	private SetAttributeOperation setAttr(String nodeId, String attributeName, Number oldValue, Number newValue) {
		return new SetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}

	@Test
	public void jsonizeSetAttributeOperationWithNumericValuesAndNull() throws Exception {
		assertJsonizedEquals(setAttr("foobar", "someAttr", 22, null), "{t:'sa', n:'foobar', a:'someAttr', ov:22}");
	}

	@Test
	public void jsonizeSetAttributeOperationWithBooleanValues() throws Exception {
		assertJsonizedEquals(setAttr("foobar", "someAttr", true, false), "{t:'sa', n:'foobar', a:'someAttr', ov:true, nv:false}");
	}

	private Operation setAttr(String nodeId, String attributeName, boolean oldValue,boolean newValue) {
		return new SetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}

	@Test
	public void jsonizeSetAttributeOperationWithStringValues() throws Exception {
		assertJsonizedEquals(setAttr("foobar", "someAttr", "foo", "bar"),"{t:'sa', n:'foobar', a:'someAttr', ov:'foo', nv:'bar'}");
	}

	private Operation setAttr(String nodeId, String attributeName, String oldValue, String newValue) {
		return new SetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}

	@Test
	public void jsonizeSetReferenceOperation() throws Exception {
		assertJsonizedEquals(setRef("node1", "someRef", "node2", "node3"),"{t:'sr', n:'node1', r:'someRef', ot:'node2', nt:'node3'}");
	}
	private Operation setRef(String nodeId, String referenceName, String oldTargetId, String newTargetId) {
		return new SetUnaryReferenceOperation(nodeId, referenceName, oldTargetId, newTargetId);
	}

	@Test
	public void jsonizeSetReferenceOperationWithoutOldTarget() throws Exception {
		assertJsonizedEquals(setRef("node1", "someRef", null, "node3"),"{t:'sr', n:'node1', r:'someRef', nt:'node3'}");
	}
	
	@Test
	public void jsonizeSetReferenceOperationWithoutNewTarget() throws Exception {
		assertJsonizedEquals(setRef("node1", "someRef", "node2", null),"{t:'sr', n:'node1', r:'someRef', ot:'node2'}");
	}
	
	@Test
	public void jsonizeAddReferenceOperation() throws Exception {
		assertJsonizedEquals(addRef("node1", "someRef", 5, "node2"),"{t:'ar', n:'node1', r:'someRef' ,nt:'node2', p:5}");
	}
	
	private Operation addRef(String nodeId, String refName, int position, String targetId) {
		return new AddReferenceOperation(nodeId, refName, position, targetId);
	}
	@Test
	public void jsonizeRemoveReferenceOperation() throws Exception {
		assertJsonizedEquals(remRef("node1", "someRef", 5, "node2"),"{t:'rr', n:'node1', r:'someRef' ,ot:'node2', p:5}");
	}
	
	private Operation remRef(String nodeId, String refName, int position, String targetId) {
		return new RemoveReferenceOperation(nodeId, refName, position, targetId);
	}

	@Test
	public void jsonizeComplexOperation() throws Exception {
		ComplexOperation complex = new ComplexOperationImpl("The description", Arrays.asList((PrimitiveOperation)addNode("n1"), (PrimitiveOperation)remNode("n3")));
		assertJsonizedEquals(complex, "{t:'c', d:'The description', ops:[{t:'an', n:'n1'}, {t:'rn', n:'n3'}]}");
	}
	
}
