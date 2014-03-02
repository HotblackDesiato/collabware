package collabware.api.operations.context;

import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.PrimitiveOperation;

/**
 * A {@link ComplexOperation} with additional bookkeeping information required by the transformation algorithm.
 * Only  {@link ComplexOperation} need to have additional context information as {@link PrimitiveOperation}s are required to be strictly
 * consecutive (i.e. without any other {@link PrimitiveOperation}s interleaving) and hence their context can be derived
 * from the context of {@link ComplexOperation}.
 */
public interface ContextualizedComplexOperation extends ComplexOperation, ContextualizedOperation {

	/**
	 * {@inheritDoc OperationContextDecorator#getOriginal()}
	 */
	ContextualizedComplexOperation getOriginal();
	
	/**
	 * {@inheritDoc ComplexOperation#inverse()}
	 */
	ContextualizedComplexOperation inverse();
	
	/**
	 * {@inheritDoc OperationContextDecorator#getDecoratedOperation()}
	 */
	ComplexOperation getDecoratedOperation();

	/**
	 * {@inheritDoc ComplexOperation#getPrimitiveOperations()}
	 */
	List<PrimitiveOperation> getPrimitiveOperations();

	/**
	 * @return the timestamp of arrival at the client or server.
	 */
	long getArrivalTimeStamp();

}