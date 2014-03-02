package collabware.transformer.internal;

import static collabware.transformer.internal.utils.MockOperationProvider.anOperationWithContext;
import static collabware.transformer.internal.utils.MockOperationProvider.ctx;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.NoSuchElementException;

import org.junit.Test;

import collabware.api.operations.context.ContextualizedOperation;
import collabware.transformer.internal.cache.Cache;
import collabware.transformer.internal.cache.UnboundedCache;

public class UnboundedCacheTest {

	private Cache cache = new UnboundedCache();
	
	@Test
	public void emptyCacheHasNoVersions() {
		ContextualizedOperation operation = anOperationWithContext(ctx(0, new int[]{}));

		assertThat(cache.hasVersion(operation, ctx(1, new int[]{1,3})), is(false));
	}

	
	@Test
	public void afterStoringVersionCanBeRetrieved() throws Exception {
		ContextualizedOperation original = anOperationWithContext(ctx(0, new int[]{}));
		ContextualizedOperation version = anOperationWithContext(ctx(0, new int[]{0,5}));
		cache.storeVersion(original, version);
		
		assertThat(cache.getVersion(original, version.getContext()), is (version));
	}

	@Test
	public void whenRetreivingVersionClientNumberIsIrrelevant() throws Exception {
		ContextualizedOperation original = anOperationWithContext(ctx(0, new int[]{}));
		ContextualizedOperation version = anOperationWithContext(ctx(1, new int[]{0,5}));
		cache.storeVersion(original, version);
		
		assertThat(cache.getVersion(original, ctx(5, new int[]{0,5})), is(version));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void retrievingNonExistingVersionThrowsNoSuchElementException() throws Exception {
		ContextualizedOperation original = anOperationWithContext(ctx(0, new int[]{}));
		cache.getVersion(original, ctx(5, new int[]{0,5}));
	}

	@Test(expected=NoSuchElementException.class)
	public void retrievingNonExistingVersionThrowsNoSuchElementException_2() throws Exception {
		ContextualizedOperation original = anOperationWithContext(ctx(0, new int[]{}));
		ContextualizedOperation version = anOperationWithContext(ctx(0, new int[]{0,5}));
		cache.storeVersion(original, version);
		
		cache.getVersion(original, ctx(5, new int[]{3,0}));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void storedVersionsCannotBeOverwritten() throws Exception {
		ContextualizedOperation original = anOperationWithContext(ctx(0, new int[]{}));
		ContextualizedOperation version = anOperationWithContext(ctx(0, new int[]{0,5}));
		cache.storeVersion(original, version);

		ContextualizedOperation otherVersionWithSameContext = anOperationWithContext(ctx(0, new int[]{0,5}));
		cache.storeVersion(original, otherVersionWithSameContext);
	}

}
