package collabware.transformer.internal.cache;

import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;

public class NullCache implements Cache {

	@Override
	public boolean hasVersion(ContextualizedOperation operation, Context targetContext) {
		return false;
	}

	@Override
	public ContextualizedOperation getVersion(ContextualizedOperation operation, Context targetContext) {
		return null;
	}

	@Override
	public void storeVersion(ContextualizedOperation operation, ContextualizedOperation transformed) {
	}

	@Override
	public void empty() {
	}

	@Override
	public void cleanUp() {
		// nothing to do here.
	}

	
}
