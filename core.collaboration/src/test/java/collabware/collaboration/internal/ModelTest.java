package collabware.collaboration.internal;


public class ModelTest {
//	private CollaborationImpl model = new CollaborationImpl();
//	private User someUser = null;
//	
//	@Test
//	public void applyOperation() throws Exception {
//		ContextualizedComplexOperation operation = createOperationWith(model.getState(), 1);
//		
//		model.apply(operation, someUser);
//		
//		assertTrue(model.getState().contains(operation));
//	}
//
//	@Test
//	public void applyTwoSubsequentOperation() throws Exception {
//		ContextualizedComplexOperation operation1 = createOperationWith (model.getState().asContextForClient(1));
//		ContextualizedComplexOperation operation2 = createOperationWith (contextIncluding(operation1));
//		
//		model.apply(operation1, someUser);
//		model.apply(operation2, someUser);
//		
//		assertTrue(model.getState().contains(operation2));
//	}
//
//	@Test(expected=ContextMismatchException.class)
//	public void applyOperationWithWrongContext() throws Exception {
//		Context context = model.getState().asContextForClient(0);
//		
//		model.apply(anOperationWithWrong(context), someUser);
//	}
//	
//	private ContextualizedComplexOperation anOperationWithWrong(Context startContext) throws OperationApplicationException {
//		ContextualizedComplexOperation operation = createOperationWith(startContext);
//		Context wrongContext = contextIncluding(operation);
//		ContextualizedComplexOperation operationWithWrongContext = createOperationWith(wrongContext);
//		return operationWithWrongContext;
//	}
//
//	private Context contextIncluding(ContextualizedComplexOperation operation1) {
//		return operation1.getContext().including(operation1);
//	}
//
//	private ContextualizedComplexOperation createOperationWith(DocumentState modelState, int withClientNumber) throws OperationApplicationException {
//		return createOperationWith(modelState.asContextForClient(withClientNumber));
//	}
//
//	private ContextualizedComplexOperation createOperationWith(Context context) throws OperationApplicationException {
//		final ContextualizedComplexOperation operation = createMock(ContextualizedComplexOperation.class);
//		expectApplyCalledOn (operation);
//		expect(operation.getContext()).andReturn(context).atLeastOnce();	
//		expect(operation.getSelfIncludingContext()).andReturn(context.next()).atLeastOnce();
//		expect(operation.getOriginal()).andReturn(operation);
//		final Capture<BackedOperationsSet> captureState = new Capture<BackedOperationsSet>();
//		
//		IAnswer<? extends Object> answer = new IAnswer<Object>() {
//			@Override
//			public Object answer() throws Throwable {
//				captureState.getValue().add(operation);
//				return null;
//			}
//		};
//		
//		operation.addTo(EasyMock.capture(captureState));
//		expectLastCall().andAnswer(answer).anyTimes();
//		replay(operation);
//		return operation;
//	}
//
//	private void expectApplyCalledOn(ContextualizedOperation o) throws OperationApplicationException {
//		o.apply(EasyMock.anyObject(ModifyableGraph.class));
//		expectLastCall().once();
//	}
//	
}
