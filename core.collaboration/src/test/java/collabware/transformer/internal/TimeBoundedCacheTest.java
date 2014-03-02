package collabware.transformer.internal;

import static collabware.transformer.internal.utils.MockOperationProvider.anOperationWithContext;
import static collabware.transformer.internal.utils.MockOperationProvider.ctx;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import collabware.api.operations.context.ContextualizedOperation;
import collabware.transformer.internal.cache.Cache;
import collabware.transformer.internal.cache.TimeBoundedCache;

public class TimeBoundedCacheTest {

	@Test
	public void cleanUpRemovesOldVersions() throws InterruptedException {
		Cache cache = new TimeBoundedCache(50);
		
		ContextualizedOperation original = anOperationWithContext(ctx(0, new int[]{}));
		ContextualizedOperation version = anOperationWithContext(ctx(0, new int[]{0,5}));
		cache.storeVersion(original, version);
		
		Thread.sleep(100);
		cache.cleanUp();
		
		assertThat(cache.hasVersion(original, version.getContext()), is(false));
	}

	@Test
	public void cleanUpDoesNotRemovesNewerVersions() throws InterruptedException {
		Cache cache = new TimeBoundedCache(50);
		
		ContextualizedOperation original = anOperationWithContext(ctx(0, new int[]{}));
		ContextualizedOperation version = anOperationWithContext(ctx(0, new int[]{0,5}));
		cache.storeVersion(original, version);
		
		Thread.sleep(10);
		cache.cleanUp();
		
		assertThat(cache.hasVersion(original, version.getContext()), is(true));
	}

}
