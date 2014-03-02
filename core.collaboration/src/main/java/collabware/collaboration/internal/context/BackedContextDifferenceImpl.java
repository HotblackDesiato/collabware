package collabware.collaboration.internal.context;

import collabware.api.operations.context.BackedContextDifference;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.api.operations.context.NoSuchOperationException;

class BackedContextDifferenceImpl extends ContextDifferenceImpl implements BackedContextDifference {

	private final BackedOperationsSetImpl backedOpSet;
	private int size;
	private final VectorBasedContext minuend;
	private VectorBasedContext subtrahend;

	public BackedContextDifferenceImpl(VectorBasedContext minuend,	VectorBasedContext subtrahend, BackedOperationsSetImpl backedOpSet) {
		super(minuend, subtrahend);
		this.minuend = minuend;
		this.subtrahend = subtrahend;		
		this.backedOpSet = backedOpSet;
		this.size = super.size();
	}

	@Override
	public int size() {
		return size;
	}

	public ContextualizedOperation selectAndRemoveWithSubContextOf(ContextualizedOperation operation) throws NoSuchOperationException {
		ContextualizedOperation selectedOperation = selectOperationWithSubContextOf(operation);
		removeFromDifference(selectedOperation);
		return selectedOperation;
	}

	private ContextualizedOperation selectOperationWithSubContextOf(ContextualizedOperation operation) throws NoSuchOperationException {
		int clientNumber = clientNumberWithOperationsInDifference((VectorBasedContext) operation.getContext());
		int sequenceNumber = lowestSequenceNumberInDifferenceFor(clientNumber);
		ContextualizedOperation selectedOperation = backedOpSet.getOperation(clientNumber, sequenceNumber);
		return selectedOperation;
	}

	private void removeFromDifference(ContextualizedOperation selectedOperation) {
		this.subtrahend = (VectorBasedContext) subtrahend.including(selectedOperation);
		size--;
	}

	private int lowestSequenceNumberInDifferenceFor(int clientNumber) {
		return subtrahend.getSequenceNumbers().get(clientNumber) + 1;
	}

	private int clientNumberWithOperationsInDifference(VectorBasedContext context) throws NoSuchOperationException {
		int maxLength = Math.max(minuend.getSequenceNumbers().length(), subtrahend.getSequenceNumbers().length());
		for (int i = 0; i < maxLength; i++) {
			if (operationsInDifferenceForClient(i) && operationsInSubContextForClient(context, i))
				return i;
		}
		throw new NoSuchOperationException("Empty difference.");
	}

	private boolean operationsInSubContextForClient(VectorBasedContext context, int i) {
		return minuend.getSequenceNumbers().get(i) >= context.getSequenceNumbers().get(i);
	}

	private boolean operationsInDifferenceForClient(int i) {
		return minuend.getSequenceNumbers().get(i) > subtrahend.getSequenceNumbers().get(i);
	}
}
