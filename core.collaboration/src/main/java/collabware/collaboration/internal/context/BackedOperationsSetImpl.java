package collabware.collaboration.internal.context;

import static collabware.collaboration.internal.context.ContextHelper.clientNumberOf;

import java.util.ArrayList;
import java.util.List;

import collabware.api.operations.context.BackedContextDifference;
import collabware.api.operations.context.BackedOperationsSet;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.api.operations.context.NonConsecutiveContextException;


public class BackedOperationsSetImpl implements BackedOperationsSet {

	private List<List<ContextualizedOperation>> operationsSequencesPerClient = new ArrayList<List<ContextualizedOperation>>();
	
	private VectorBasedContext currentContext = new VectorBasedContext(0);
	
	public Context asContextForClient(int i) {
		return new VectorBasedContext(i, currentContext.getSequenceNumbers());
	}

	public void add(ContextualizedOperation operation) throws NonConsecutiveContextException {
		currentContext = (VectorBasedContext) currentContext.including(operation);
		List<ContextualizedOperation> operationSequence = getOperationSequenceFor(clientNumberOf(operation));
		operationSequence.add(operation);
	}

	private List<ContextualizedOperation> getOperationSequenceFor(int clientNumber) {
		if (clientNumber >= operationsSequencesPerClient.size())
			expand(operationsSequencesPerClient, clientNumber);
		return getOrCreateOperationSequenceForClient(clientNumber);
	}

	private List<ContextualizedOperation> getOrCreateOperationSequenceForClient(int clientNumber) {
		List<ContextualizedOperation> operations = operationsSequencesPerClient.get(clientNumber);
		if (operations == null) {
			operations = new ArrayList<ContextualizedOperation>();
			operationsSequencesPerClient.set(clientNumber, operations);
		}
		return operations;
	}

	private void expand(List<List<ContextualizedOperation>> sequence, int clientNumber) {
		for (int i = sequence.size(); i <= clientNumber; i++) 
			sequence.add(i, null);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof Context)
			return equalsContext((Context) o);
		else
			return false;
	}
	
	private boolean equalsContext(Context context) {
		VectorBasedContext contextImpl = (VectorBasedContext) context;
		return currentContext.equals(contextImpl);
	}

	ContextualizedOperation getOperation(int clientNumber, int sequenceNumber) {
		List<ContextualizedOperation> operationSequence = getOperationSequenceFor(clientNumber);
		int listIndex = zeroBased(sequenceNumber);
		return operationSequence.get(listIndex);
	}

	private int zeroBased(int index) {
		return index - 1;
	}
	
	@Override
	public String toString() {
		return currentContext.toString();
	}

	public BackedContextDifference minus(Context context) {
		return new BackedContextDifferenceImpl(currentContext, (VectorBasedContext) context, this);
	}

	public BackedOperationsSetImpl copy() {
		BackedOperationsSetImpl operationsSet = new BackedOperationsSetImpl();
		operationsSet.currentContext = this.currentContext;
		operationsSet.operationsSequencesPerClient = copyOperationSequencePerClient();
		return operationsSet;
	}

	private List<List<ContextualizedOperation>> copyOperationSequencePerClient() {
		// TODO this is a slow version of copy. To speed it up use the original as a reference and only store differences.
		List<List<ContextualizedOperation>> copiedOperationsSequencesPerClient= new ArrayList<List<ContextualizedOperation>>();
		for (List<ContextualizedOperation> origOperationSequence : operationsSequencesPerClient) {
			if (origOperationSequence == null) {
				copiedOperationsSequencesPerClient.add(null);
			} else {
				copiedOperationsSequencesPerClient.add(copyOperationSequence(origOperationSequence));
			}
		}
		return copiedOperationsSequencesPerClient;
	}

	private List<ContextualizedOperation> copyOperationSequence(List<ContextualizedOperation> origOperationSequence) {
		List<ContextualizedOperation> copyOperationSequence = new ArrayList<ContextualizedOperation>(origOperationSequence.size());
		for (ContextualizedOperation op : origOperationSequence) {
			copyOperationSequence.add(op);
		}
		return copyOperationSequence;
	}

	public boolean contains(ContextualizedOperation o) {
		return currentContext.contains(o);
	}

}
