package collabware.collaboration.internal.context;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;

public class OperationTestUtils {
	
	private OperationTestUtils() {}
	
	public static Context emptyContextForClient(int clientNumber) {
		return new VectorBasedContext(clientNumber);
	}

	public static ContextualizedOperation createOperationWith(Context context) {
		ContextualizedOperation o = createMock(ContextualizedOperation.class);
		expect(o.getContext()).andStubReturn(context);
		expect(o.getSelfIncludingContext()).andStubReturn(context.next());
		replay(o);
		return o;
	}
	
	public static void assertOperationsAreConsecutive(ContextualizedOperation first, ContextualizedOperation second) {
		assertTrue(second.getContext().contains(first));
	}
}
