package collabware.model.internal.ops;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import collabware.api.operations.OperationApplicationException;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AddReferenceOperation;

public class AddReferenceTest {

	private final ModelProvider modelProvider = new ModelProviderImpl();
	
	private ModifyableModel givenModel(String literal) {
		return (ModifyableModel) modelProvider.createModelFromLiteral("", literal);
	}

	@Test
	public void apply() throws Exception {
		
		ModifyableModel model = givenModel("source, target");
		
		AddReferenceOperation addRef = new AddReferenceOperation("source", "ref", 0, "target");
		addRef.apply(model);

		assertThat(model.getGraph().getNode("source").getNaryReferences().get("ref").get(0).getId(), is("target"));
	}

	@Test(expected=OperationApplicationException.class)
	public void noSuchTargetNode() throws Exception {
		
		ModifyableModel model = givenModel("source, target");
		
		AddReferenceOperation addRef = new AddReferenceOperation("source", "ref", 0, "noSuchNode");
		addRef.apply(model);
	}
	
	@Test(expected=OperationApplicationException.class)
	public void noSuchSourceNode() throws Exception {
		
		ModifyableModel model = givenModel("source, target");
		
		AddReferenceOperation addRef = new AddReferenceOperation("noSuchNode", "ref", 0, "target");
		addRef.apply(model);
	}
	
	@Test
	public void serialize() {
		AddReferenceOperation addRef = new AddReferenceOperation("source", "ref", 0, "target");
		Map<String, Object> serialized = addRef.serialize();
		
		assertThat(serialized, hasEntry("t", (Object)"ar"));
		
		assertThat(serialized, hasEntry("n", (Object)addRef.getNodeId()));
		assertThat(serialized, hasEntry("nt", (Object)addRef.getTargetId()));
		assertThat(serialized, hasEntry("p", (Object)addRef.getPosition()));
		assertThat(serialized, hasEntry("r", (Object)addRef.getReferenceName()));
	}
	
}
