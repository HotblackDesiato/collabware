package collabware.api.operations;

import java.util.List;

/**
 * A ComplexOperation groups a number of PrimitiveOperations into one logical operation. 
 *
 */
public interface ComplexOperation extends Operation, Iterable<PrimitiveOperation> {
	/**
	 * 
	 * @return the constituting primitive operations.
	 */
	List<?extends PrimitiveOperation> getPrimitiveOperations();
	
	/**
	 * 
	 * @return the description of this complex operation.
	 */
	String getDescription();
	
	/**
	 * @return the inverse which is the inverse of the constituent primitive operation in reverse order. 
	 */
	ComplexOperation inverse();
	
	/**
	 * 
	 * @return the number of constituent primitive operations.
	 */
	int size();
}
