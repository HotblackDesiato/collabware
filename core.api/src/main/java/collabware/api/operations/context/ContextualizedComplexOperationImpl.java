package collabware.api.operations.context;

import java.util.Iterator;
import java.util.List;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.OperationApplicationException;
import collabware.api.operations.PrimitiveOperation;
/**
 * Decorates a ComplexOperation with context information vital for the transformation procedures. 
 * It also implements the ComplexOperation interface.
 * 
 * This class is immutable, provided the decorated ComplexOperation is immutable.
 */
public class ContextualizedComplexOperationImpl implements ContextualizedComplexOperation {

	private final Context context;
	private final ComplexOperation decoratedOperation;
	private final ContextualizedComplexOperation original;
	private final long arrivalTime;
	private static int count = 0;

	private static synchronized long getIncrementedTimeStamp() {
		return count++;
	}
	
	public ContextualizedComplexOperationImpl(Context context, ComplexOperation complexOperation, ContextualizedComplexOperation original) {
		this.context = context;
		this.decoratedOperation = complexOperation;
		// "this" cannot be passed as original into this constructor hence this ugly construct
		if (original != null) 
			this.original = original;
		else 
			this.original = this;
		this.arrivalTime = getIncrementedTimeStamp();
	}

	public ContextualizedComplexOperationImpl(Context context, ComplexOperation complexOperation) {
		this(context,complexOperation,null);
	}

	@Override
	public Iterator<PrimitiveOperation> iterator() {
		return decoratedOperation.iterator();
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public ContextualizedComplexOperation getOriginal() {
		return original;
	}

	@Override
	public Context getSelfIncludingContext() {
		return this.context.next();
	}

	@Override
	public void addTo(BackedOperationsSet state) {
		state.add(this);
	}

	@Override
	public ContextualizedComplexOperation inverse() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public ComplexOperation getDecoratedOperation() {
		return decoratedOperation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrimitiveOperation> getPrimitiveOperations() {
		return (List<PrimitiveOperation>) this.decoratedOperation.getPrimitiveOperations();
	}

	@Override
	public String getDescription() {
		return decoratedOperation.getDescription();
	}

	@Override
	public int size() {
		return decoratedOperation.size();
	}

	@Override
	public void apply(ModifyableDocument graph) throws OperationApplicationException {
		decoratedOperation.apply(graph);
	}


	@Override
	public String toString() {
		return "ContextualizedComplexOperation [context=" + context
				+ ", decoratedOperation=" + decoratedOperation + "]";
	}

	@Override
	public long getArrivalTimeStamp() {
		return arrivalTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (arrivalTime ^ (arrivalTime >>> 32));
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((decoratedOperation == null) ? 0 : decoratedOperation.hashCode());
		result = prime * result + ((original == null) ? 0 : original.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContextualizedComplexOperationImpl other = (ContextualizedComplexOperationImpl) obj;
//		if (arrivalTime != other.arrivalTime)
//			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (decoratedOperation == null) {
			if (other.decoratedOperation != null)
				return false;
		} else if (!decoratedOperation.equals(other.decoratedOperation))
			return false;
		return true;
	}
}
