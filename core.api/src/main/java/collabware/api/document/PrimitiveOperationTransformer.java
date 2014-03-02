package collabware.api.document;

import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;

public interface PrimitiveOperationTransformer {

	/**
	 * Transforms one PrimitiveOperation against another PrimitiveOperations.
	 * The following invariant must hold true for ALL combinations of operations:
	 * <pre>
	 * {@code
	 * Document d1, d2;
	 * PrimitiveOperation op1, op2, op1Transfromed, op2Transformed;
	 * op1Transformed = transform(op1, op2);
	 * op2Transformed = transform(op2, op1);
	 *  
	 * op1.apply(d1);
	 * op2Transformed(d1);
	 * 
	 * op2.apply(d2);
	 * op1Transformed(d2);
	 * 
	 * d1.equals(d2) // true
	 * }
	 * </pre>
	 * 
	 * 
	 * @param base
	 * @param other
	 * @return the transformed version of base.
	 * @throws CollisionException if {@code base} cannot be transformed against {@code other}.
	 */
	PrimitiveOperation transform(PrimitiveOperation base, PrimitiveOperation other) throws CollisionException;

}