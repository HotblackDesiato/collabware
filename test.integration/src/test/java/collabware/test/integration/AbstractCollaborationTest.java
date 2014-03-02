package collabware.test.integration;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;

import collabware.api.document.Document;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.collaboration.Client;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationDetails;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.ConflictingOperationsException;
import collabware.collaboration.Participant;
import collabware.collaboration.internal.CollaborationProviderImpl;
import collabware.model.Model;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.GraphOperationsProviderImpl;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;
import collabware.model.internal.ops.transformation.GraphTransformationMatrix;
import collabware.model.operations.GraphOperationsProvider;
import collabware.transformer.TransformationProvider;
import collabware.transformer.internal.TransformationProviderImpl;
import collabware.userManagement.User;
import collabware.userManagement.UserDetails;
import collabware.userManagement.exception.DuplicateUserIdException;
import collabware.userManagement.internal.UserManagementImpl;

public class AbstractCollaborationTest {

	private ModelProviderImpl modelProvider;
	private GraphOperationsProvider operationProvider;
	private Collaboration collaboration;
	private Client client1;
	private Client client2;
	private CollaborationProvider collaborationProvider;

	@Before
	public void setUp() throws DuplicateUserIdException {
		this.operationProvider = new GraphOperationsProviderImpl();
		this.modelProvider = setupModelProvider();
	
		collaborationProvider = setupCollaborationProvider(operationProvider, modelProvider);
		CollaborationDetails details = new CollaborationDetails("test", "test");
		this.collaboration = collaborationProvider.createCollaboration(details , createParticipant(collaborationProvider));
		Participant participant1 = createParticipant(collaborationProvider);
		Participant participant2 = createParticipant(collaborationProvider);
		this.client1 = this.collaboration.join(participant1);
		this.client2 = this.collaboration.join(participant2);
	}

	private Model parseModelLiteral(String modelLiteral) {
		return modelProvider.createModelFromLiteral("", modelLiteral);
	}

	private Participant createParticipant(CollaborationProvider collaborationProvider) throws DuplicateUserIdException {
		UserManagementImpl userManagementImpl = new UserManagementImpl();
		UserDetails parameterObject = new UserDetails("testUser", "1234", "Test User");
		User user = userManagementImpl.createUser(parameterObject);
		return collaborationProvider.getParticipant(user);
	}

	private CollaborationProvider setupCollaborationProvider(GraphOperationsProvider operationsProvider, ModelProvider modelProvider) {
		CollaborationProviderImpl collaborationProvider = new CollaborationProviderImpl();
		collaborationProvider.setModelProvider(modelProvider);
		collaborationProvider.setTransformationProvider(setupTransformationProvider(operationsProvider));
		collaborationProvider.startup();
		return collaborationProvider;
	}

	private TransformationProvider setupTransformationProvider(GraphOperationsProvider graphOperationsProvider) {
		TransformationProviderImpl transformationProvider = new TransformationProviderImpl();
		transformationProvider.setPartiallyInclusiveOperationalTransformation(new GraphTransformationMatrix());
		return transformationProvider;
	}

	private ModelProviderImpl setupModelProvider() {
		ModelProviderImpl modelProvider = new ModelProviderImpl();
		return modelProvider;
	}

	protected void assertModelEqualsTo(String modelLiteral) {
		Document model = collaboration.getDocument();
		Model expectedModel = parseModelLiteral(modelLiteral);
		assertTrue(expectedModel.equals(model));
	}

	protected void client2Applies(int i, ComplexOperation complex)throws ConflictingOperationsException {
		this.client2.applyChangeToCollaboration(i, complex);
	}

	protected void client1Applies(int i, ComplexOperation complex) throws ConflictingOperationsException {
		this.client1.applyChangeToCollaboration(i, complex);
	}

	protected void client2Applies(PrimitiveOperation op) throws ConflictingOperationsException {
		client2Applies(complex(op));
	}

	protected void client1Applies(PrimitiveOperation op) throws ConflictingOperationsException {
		client1Applies(complex(op));
	}

	protected void client2Applies(ComplexOperation operation2) throws ConflictingOperationsException {
		this.client2.applyChangeToCollaboration(0, operation2);
	}

	protected void client1Applies(ComplexOperation operation1) throws ConflictingOperationsException {
		this.client1.applyChangeToCollaboration(0, operation1);
	}

	protected ComplexOperation complex(PrimitiveOperation ...operations) {
		return new ComplexOperationImpl("", Arrays.asList(operations));
	}

	protected PrimitiveOperation remNode(String nodeId) {
		return operationProvider .createRemoveNodeOperation(nodeId);
	}

	protected PrimitiveOperation addNode(String nodeId) {
		return operationProvider.createAddNodeOperation(nodeId);
	}

	protected SetAttributeOperation setAttr(String nodeId, String attributeName, String oldValue, String newValue) {
		return operationProvider.createSetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}

	protected SetUnaryReferenceOperation setRef(String nodeId, String referenceName, String oldTargetId, String newTargetId) {
		return operationProvider.createSetUnaryReferenceOperation(nodeId, referenceName, oldTargetId, newTargetId);
	}
	protected AddReferenceOperation addRef(String nodeId, String refName, int position, String targetId) {
		return operationProvider.createAddReferenceOperation(nodeId, refName, position, targetId);
	}

	protected void givenInitialModel(String modelLiteral) {
		modelProvider.populateModelFromLiteral((ModifyableModel) collaboration.getDocument(), modelLiteral);
	}
	
	protected Client join() {
		try {
			return this.collaboration.join(createParticipant(collaborationProvider));
		} catch (DuplicateUserIdException e) {
			fail();
			return null;
		}
	}
}
