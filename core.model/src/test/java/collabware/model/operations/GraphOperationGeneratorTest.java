package collabware.model.operations;

import static collabware.model.operations.OperationHelper.addNode;
import static collabware.model.operations.OperationHelper.addRef;
import static collabware.model.operations.OperationHelper.remNode;
import static collabware.model.operations.OperationHelper.remRef;
import static collabware.model.operations.OperationHelper.setAttr;
import static collabware.model.operations.OperationHelper.setRef;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import collabware.api.operations.OperationGenerator;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.ModelImpl;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.GraphOperationGenerator;
public class GraphOperationGeneratorTest {

	private ModelProviderImpl provider = new ModelProviderImpl();
	private OperationGenerator operationGeneratior = new GraphOperationGenerator();
	private ModifyableModel theModel = new ModelImpl("");

	@Before
	public void setup() {
	}
	
	@Test
	public void whenNodeIsAdded_thenAddNodeOperationIsGenerated() {
		theModel.addChangeListener(operationGeneratior);
		
		theModel.getGraph().addNode("myNode");

		assertThat(operationGeneratior.getGeneratedOperations(), equalTo(asList(addNode("myNode"))));
	}

	@Test
	public void givenModelWithOneNode_whenNodeIsRemoved_thenRemoveNodeOperationIsGenerated() {
		givenModel("n1");
		theModel.addChangeListener(operationGeneratior);
		
		node("n1").detach();
		
		assertThat(operationGeneratior.getGeneratedOperations(), equalTo(asList(remNode("n1"))));
	}

	@Test
	public void whenReferenceIsAdded_thenAddReferenceOperationIsGenerated() {
		givenModel("n1,n2");
		theModel.addChangeListener(operationGeneratior);
		
		node("n1").getNaryReferences().add("ref", 0, node("n2"));
		
		assertThat(operationGeneratior.getGeneratedOperations(), equalTo(asList(addRef("n1", "ref", 0, "n2"))));
	}

	@Test
	public void whenReferenceIsRemoved_thenRemoveReferenceOperationIsGenerated() {
		givenModel("n1,n2,n1.ref[0]->n2");
		theModel.addChangeListener(operationGeneratior);
		
		node("n1").getNaryReferences().remove("ref", 0);
		
		assertThat(operationGeneratior.getGeneratedOperations(), equalTo(asList(remRef("n1", "ref", 0, "n2"))));
	}

	@Test
	public void whenAttributeIsUpdated_thenSetAttributeOperationIsGenerated() {
		givenModel("n1,n2");
		theModel.addChangeListener(operationGeneratior);
		
		node("n1").getAttributes().set("foo", "bar");
		
		assertThat(operationGeneratior.getGeneratedOperations(), equalTo(asList(setAttr("n1", "foo", null, "bar"))));
	}

	@Test
	public void whenReferenceIsUpdated_thenSetReferenceOperationIsGenerated() {
		givenModel("n1,n2");
		theModel.addChangeListener(operationGeneratior);
		
		node("n1").getUnaryReferences().set("foo", node("n2"));
		
		assertThat(operationGeneratior.getGeneratedOperations(), equalTo(asList(setRef("n1", "foo", null, "n2"))));
	}

	private ModifyableNode node(String id) {
		return theModel.getGraph().getNode(id);
	}

	private void givenModel(String literal) {
		theModel = (ModifyableModel) provider.createModelFromLiteral("", literal);
	}

}
