package collabware.collaboration.internal.context;

import collabware.api.operations.context.BackedContextDifference;
import collabware.api.operations.context.ContextDifference;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.api.operations.context.DocumentState;

class ContextDifferenceImpl implements ContextDifference {

	private final VectorBasedContext minuend;
	private final VectorBasedContext subtrahend;
	private final int size;

	public ContextDifferenceImpl(VectorBasedContext minuend, VectorBasedContext subtrahend) {
		this.minuend = minuend;
		this.subtrahend = subtrahend;
		this.size = calculateSize();
	}

	private int calculateSize() {
		int length = Math.max(minuend.getSequenceNumbers().length(), subtrahend.getSequenceNumbers().length());
		int size = 0;
		for (int i = 0; i < length; i++)
			size += differenceAt(i);
		return size;
	}
	
	private int differenceAt(int index) {
		int difference = minuend.getSequenceNumbers().get(index) - subtrahend.getSequenceNumbers().get(index);
		if (difference > 0)
			return difference;
		else 
			return 0;
	}

	public int size() {
		return size;
	}

	public boolean contains(ContextualizedOperation o) {
		return minuend.contains(o) && !subtrahend.contains(o);
	}

	public BackedContextDifference backedBy(DocumentState backedOpSet) {
		return new BackedContextDifferenceImpl(minuend, subtrahend, (BackedOperationsSetImpl) backedOpSet);
	}

}
