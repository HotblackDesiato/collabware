package collabware.collaboration.internal.context;

import static collabware.collaboration.internal.context.ContextHelper.clientNumberOf;
import static collabware.collaboration.internal.context.ContextHelper.contextOf;
import static collabware.collaboration.internal.context.ContextHelper.ownSequenceNumberOf;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextDifference;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.api.operations.context.NonConsecutiveContextException;
import collabware.api.operations.context.OperationsSet;
/**
 *  A vector-based implementation of an Context.
 *  The underlying vector has one sequence number per client and gets 
 *  incremented with every operation performed by the corresponding client. 
 *  Just by looking a vector, we can very quickly decide whether an operation is
 *  include in a context and whether a context is a sub-context of another. This is
 *  very important for the performance of the transformation algorithm.
 *   
 */
public class VectorBasedContext implements Context {

	private final ContextVector sequenceNumbers;
	private final int clientNumber;
	
	public VectorBasedContext(int client, int[] sequenceNumbers) {
		this(client, new ContextVector(sequenceNumbers));
	}
	
	public VectorBasedContext(int client) {
		this(client, new int[0]);
	}

	public VectorBasedContext(int client, ContextVector contextVector) {
		this.clientNumber = client;
		this.sequenceNumbers = contextVector;
	}

	/**
	 * @param operation the operation to include
	 * @return new Context, which includes operation o.
	 * @throws NonConsecutiveContextException
	 */
	public Context including(ContextualizedOperation operation)  {
		assertCanInclude(contextOf(operation));
		ContextVector includingContextVector = sequenceNumbers.updating(clientNumberOf(operation), ownSequenceNumberOf(operation));
		return new VectorBasedContext(getClientNumber(), includingContextVector );
	}
	
	public Context including(Context context)  {
		assertCanInclude((VectorBasedContext)context);
		ContextVector includingContextVector = sequenceNumbers.updating(context.getClientNumber(), nextSequenceNumberOf(context));
		return new VectorBasedContext(getClientNumber(), includingContextVector );
	}

	private int nextSequenceNumberOf(Context context) {
		return ((VectorBasedContext) context).getSequenceNumbers().get(context.getClientNumber())+1;
	}

	private void assertCanInclude(VectorBasedContext otherContext) {
		int otherClientNumber = otherContext.getClientNumber();
		if (!otherContext.isSubsetOf(this) && !equalSequenceNumbersAt(otherClientNumber, otherContext))
			throw new NonConsecutiveContextException();
	}

	private boolean equalSequenceNumbersAt(int index, VectorBasedContext otherContext) {
		return this.sequenceNumbers.get(index) == otherContext.sequenceNumbers.get(index);
	}

	public boolean contains(ContextualizedOperation o) {
		try {
			return o.getSelfIncludingContext().isSubsetOf(this);
		} catch (NonConsecutiveContextException cannotHappen) {
			return false;
		}
	}

	public boolean isSubsetOf(OperationsSet superContext) {
		VectorBasedContext superContextImpl = (VectorBasedContext) superContext;
		return this.sequenceNumbers.lessThanOrEqual(superContextImpl.sequenceNumbers);
	}

	public int getClientNumber() {
		return clientNumber;
	}

	public ContextDifference minus(OperationsSet subtrahend) {
		return new ContextDifferenceImpl(this, (VectorBasedContext) subtrahend);
	}

	public ContextVector getSequenceNumbers() {
		return sequenceNumbers;
	}

	public int hashCode() {
		return 17*sequenceNumbers.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof Context) return equalsContext((VectorBasedContext) o);
		return false;
	}

	private boolean equalsContext(Context context) {
		ContextVector otherSequenceNumbers = ((VectorBasedContext)context ).getSequenceNumbers();
		return sequenceNumbers.equals(otherSequenceNumbers);
	}

	@Override
	public String toString() {
		return "sequenceNumbers: "+sequenceNumbers+", clientNumber: " + clientNumber;
	}

	public Context next() {
		return new VectorBasedContext(clientNumber, sequenceNumbers.incrementing(clientNumber));
	}

}
