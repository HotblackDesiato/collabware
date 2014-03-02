package collabware.collaboration.internal;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Test;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.collaboration.Client;
import collabware.collaboration.ConflictingOperationsException;
import collabware.collaboration.Participant;
import collabware.collaboration.internal.context.ContextVector;
import collabware.collaboration.internal.context.VectorBasedContext;
import collabware.userManagement.User;

public class ClientProxyTest {
	
	@Test
	public void fistClientOperationReceivesInitialContext() throws Exception {
		int clientNumber = 2;
		int serverSequenceNumber = 0;
		VectorBasedContext initialContext = new VectorBasedContext(clientNumber, new ContextVector(new int[]{5,3,1,7}));
		ModifyableCollaboration collaboration = createCollaborationExpectingOperationsWithContexts(initialContext);

		Client clientProxy = new ClientProxy(collaboration, someParticipant(), initialContext);
		clientProxy.applyChangeToCollaboration(serverSequenceNumber, mockComplexOperationWithNPrimitives(3));
		
		verify(collaboration);
	}

	private Participant someParticipant() {
		Participant participant = createMock(Participant.class);
		expect(participant.getUser()).andReturn(createMock(User.class)).anyTimes();
		replay(participant);
		return participant;
	}
	
	@Test
	public void secondClientOperationReceivesInitialContextPlusOne() throws Exception {
		int clientNumber = 2;
		int serverSequenceNumber = 0;
		VectorBasedContext initialContext = new VectorBasedContext(clientNumber, new ContextVector(new int[]{5,3,1,7}));
		ModifyableCollaboration collaboration = createCollaborationExpectingOperationsWithContexts(initialContext, initialContext.including(initialContext));
		
		Client clientProxy = new ClientProxy(collaboration, someParticipant(), initialContext);
		clientProxy.applyChangeToCollaboration(serverSequenceNumber, mockComplexOperationWithNPrimitives(3));
		clientProxy.applyChangeToCollaboration(serverSequenceNumber, mockComplexOperationWithNPrimitives(3));
		
		verify(collaboration);
	}
	
	@Test
	public void clientOperationHasServerOperationInItsContext() throws Exception {
		int clientNumber = 2;
		int serverSequenceNumber = 1;
		ModifyableCollaboration collaboration = createCollaborationExpectingOperationsWithContexts(new VectorBasedContext(clientNumber, new int[]{5,4,1,7}));
		VectorBasedContext initialState = new VectorBasedContext(clientNumber, new ContextVector(new int[]{5,3,1,7}));
		ClientProxy clientProxy = new ClientProxy(collaboration, someParticipant(), initialState);
		clientProxy.sendToClient(new ContextualizedComplexOperationImpl(new VectorBasedContext(1, new int[]{5,3,1,7}), mockComplexOperationWithNPrimitives(3)));
		clientProxy.applyChangeToCollaboration(serverSequenceNumber, mockComplexOperationWithNPrimitives(3));
		
		verify(collaboration);
	}

	@Test(expected=IllegalArgumentException.class)
	public void serverSequenceNumberMustNotDecreaseOverTime() throws Exception {
		int clientNumber = 1;
		int serverSequenceNumber = 1;
		ModifyableCollaboration collaboration = createNiceMock(ModifyableCollaboration.class);
		
		ClientProxy clientProxy = new ClientProxy(collaboration, someParticipant(), new VectorBasedContext(clientNumber, new ContextVector(new int[]{5,3,1,7})));
		clientProxy.sendToClient(new ContextualizedComplexOperationImpl(new VectorBasedContext(1, new int[]{5,3,1,7}), mockComplexOperationWithNPrimitives(3)));
		clientProxy.applyChangeToCollaboration(serverSequenceNumber, mockComplexOperationWithNPrimitives(3));
		clientProxy.applyChangeToCollaboration(serverSequenceNumber-1, mockComplexOperationWithNPrimitives(3));
	}
	
	@Test
	public void serverSequenceNumberMayJump() throws Exception {
		int clientNumber = 1;
		int serverSequenceNumber = 0;
		ModifyableCollaboration collaboration = createCollaborationExpectingOperationsWithContexts(new VectorBasedContext(clientNumber, new int[]{5,3,1,7}), new VectorBasedContext(clientNumber, new int[]{5,4,1,10}));
		
		ClientProxy clientProxy = new ClientProxy(collaboration, someParticipant(), new VectorBasedContext(clientNumber, new ContextVector(new int[]{5,3,1,7})));
		clientProxy.sendToClient(new ContextualizedComplexOperationImpl(new VectorBasedContext(3, new int[]{5,3,1,7}), mockComplexOperationWithNPrimitives(3)));
		clientProxy.sendToClient(new ContextualizedComplexOperationImpl(new VectorBasedContext(3, new int[]{5,3,1,8}), mockComplexOperationWithNPrimitives(3)));
		clientProxy.sendToClient(new ContextualizedComplexOperationImpl(new VectorBasedContext(3, new int[]{5,3,1,9}), mockComplexOperationWithNPrimitives(3)));
		clientProxy.applyChangeToCollaboration(serverSequenceNumber, mockComplexOperationWithNPrimitives(3));
		clientProxy.applyChangeToCollaboration(serverSequenceNumber+3, mockComplexOperationWithNPrimitives(3));

		verify(collaboration);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void serverSequenceMustNotExceedNumberOfComplexOperationSentToClient() throws Exception {
		int clientNumber = 1;
		int serverSequenceNumber = 0;
		ModifyableCollaboration collaboration = createCollaborationExpectingOperationsWithContexts(new VectorBasedContext(clientNumber, new int[]{5,3,1,7}), new VectorBasedContext(clientNumber, new int[]{5,6,1,16}));
		
		ClientProxy clientProxy = new ClientProxy(collaboration, someParticipant(), new VectorBasedContext(clientNumber, new ContextVector(new int[]{5,3,1,7})));
		clientProxy.sendToClient(new ContextualizedComplexOperationImpl(new VectorBasedContext(3, new int[]{5,3,1,7}), mockComplexOperationWithNPrimitives(3)));
		clientProxy.sendToClient(new ContextualizedComplexOperationImpl(new VectorBasedContext(3, new int[]{5,3,1,10}), mockComplexOperationWithNPrimitives(3)));
		clientProxy.sendToClient(new ContextualizedComplexOperationImpl(new VectorBasedContext(3, new int[]{5,3,1,13}), mockComplexOperationWithNPrimitives(3)));
		clientProxy.applyChangeToCollaboration(serverSequenceNumber+4, mockComplexOperationWithNPrimitives(3));
		
		verify(collaboration);
	}
	
	private ComplexOperation mockComplexOperationWithNPrimitives(int n) {
		List<PrimitiveOperation> primitives = new ArrayList<PrimitiveOperation>();
		for (int i = 0; i < n; i++) {
			PrimitiveOperation op = createMock(PrimitiveOperation.class);
			replay(op);
			primitives.add(op);
		}
		ComplexOperation complex = new ComplexOperationImpl("", primitives);
		return complex;
	}

	private ModifyableCollaboration createCollaborationExpectingOperationsWithContexts(Context ... expectedContexts) throws ConflictingOperationsException {
		ModifyableCollaboration collaboration = createMock(ModifyableCollaboration.class);
		for (final Context expectedContext: expectedContexts) {
			final Capture<ContextualizedComplexOperation> op = new Capture<ContextualizedComplexOperation>();
			collaboration.apply(capture(op), anyObject(User.class));
			expectLastCall().andAnswer(new IAnswer<Object>() {
				@Override
				public Object answer() throws Throwable {
					assertEquals(expectedContext, op.getValue().getContext());
					return null;
				}
			});
		}
		
		replay(collaboration);
		return collaboration;
	}
	
}