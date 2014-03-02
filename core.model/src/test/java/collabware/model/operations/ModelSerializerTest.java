package collabware.model.operations;

import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.OperationApplicationException;
import collabware.model.Model;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.internal.ModelProviderImpl;


public class ModelSerializerTest {
	private final class BaseMatcherExtension extends BaseMatcher<ComplexOperation> {
		private final Model expectedModel;

		private BaseMatcherExtension(Model expectedModel) {
			this.expectedModel = expectedModel;
		}

		@Override
		public boolean matches(Object arg0) {
			if (arg0 instanceof ComplexOperation) {
				return recreatesExpectedModel((ComplexOperation) arg0);
			} else {
				return false;
			}
		}

		private boolean recreatesExpectedModel(ComplexOperation initSequence) {
			ModifyableDocument copy = anEmptyModel();
			try {
				initSequence.apply(copy);
			} catch (OperationApplicationException e) {
				throw new RuntimeException(e);
			}
			return copy.equals(expectedModel);
		}

		@Override
		public void describeTo(Description arg0) {
			
		}
	}

	private ModelProvider modelProvider = new ModelProviderImpl();
	
	@Test
	public void serializeModelWithOneNode() throws Exception {
		Model original = theModel("n1");
		
		ComplexOperation initSequence = original.asOperation();
		
		assertThat(initSequence, recreates(original));
	}

	private Matcher<ComplexOperation> recreates(final Model original) {
		return new BaseMatcherExtension(original);
	}

	@Test
	public void serializeModelWithTwoNodes() throws Exception {
		Model original = theModel("n1, n2");
		
		ComplexOperation initSequence = original.asOperation();
		
		assertThat(initSequence, recreates(original));
	}

	@Test
	public void serializeModelWithTwoNodesAndAttributes() throws Exception {
		Model original = theModel("n1, n2, n1.attr=42, n2.foo='bar'");
		
		ComplexOperation initSequence = original.asOperation();
		
		assertThat(initSequence, recreates(original));
	}
	
	@Test
	public void serializeModelWithTwoNode() throws Exception {
		Model original = theModel("n1, n2");
		
		ComplexOperation initSequence = original.asOperation();
		
		assertThat(initSequence, recreates(original));
	}

	@Test
	public void serializeModelWithTwoNodeAndReference() throws Exception {
		Model original = theModel("n1, n2, n1.ref->n2");
		
		ComplexOperation initSequence = original.asOperation();
		
		assertThat(initSequence, recreates(original));
	}

	private ModifyableModel theModel(String literal) {
		return (ModifyableModel) modelProvider.createModelFromLiteral("MY_TYPE", literal);
	}

	private ModifyableDocument anEmptyModel() {
		return modelProvider.createDocument("MY_TYPE");
	}

	@Test
	public void serializeModelWithTwoNodeAndNaryReference() throws Exception {
		Model original = theModel("n1, n2, n1.ref[0]->n2");
		
		ComplexOperation initSequence = original.asOperation();
		
		assertThat(initSequence, recreates(original));
	}
	@Test
	public void serializeModelWithThreeNodeAndNaryReference() throws Exception {
		Model original = theModel("n1, n2, n3, n1.ref[0]->n2, n1.ref[1]->n3");
		
		ComplexOperation initSequence = original.asOperation();
		
		assertThat(initSequence, recreates(original));
	}
}
