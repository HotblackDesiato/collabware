package collabware.transformer.internal.cache;

import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;

public interface Cache {

	boolean hasVersion(ContextualizedOperation original, Context versionContext);

	ContextualizedOperation getVersion(ContextualizedOperation original, Context versionContext);

	void storeVersion(ContextualizedOperation original, ContextualizedOperation version);

	void empty();
	
	void cleanUp();
	
}
