package collabware.transformer.internal.utils;

import collabware.api.document.PrimitiveOperationTransformer;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;

public class IdentityTransformationMatrix implements PrimitiveOperationTransformer {
	@Override
	public PrimitiveOperation transform(PrimitiveOperation base, PrimitiveOperation against) throws CollisionException {
		return base;
	}
}