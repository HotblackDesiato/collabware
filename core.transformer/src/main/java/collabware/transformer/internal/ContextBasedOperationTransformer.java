package collabware.transformer.internal;

import static collabware.utils.Asserts.assertNotNull;

import java.util.logging.Logger;

import collabware.api.operations.context.BackedContextDifference;
import collabware.api.operations.context.BackedOperationsSet;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextDifference;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedOperation;
import collabware.api.operations.context.DocumentState;
import collabware.api.operations.context.NoSuchOperationException;
import collabware.api.transform.CollisionException;
import collabware.transformer.Transformer;
import collabware.transformer.internal.cache.Cache;
import collabware.transformer.internal.cache.NullCache;
import collabware.utils.Formats;

/**
 * Implements the context-base operational transformation (COT) algorithm presented by
 * D. Sun and C. Sun, “Context-based operational transformation in distributed collaborative editing sys- tems,” 
 * IEEE Trans. Parallel Distrib. Syst., vol. 20, no. 10, pp. 1454–1470, 2009.
 * 
 *
 */
public class ContextBasedOperationTransformer implements Transformer {

	private static final Logger logger = Logger.getLogger("collabware.transformer.internal.ContextBasedOperationTransformer"); 
	
	private final ComplexOperationTransformer complexTransfomer;
	private BackedOperationsSet backedOperationsSet;
	private Cache cache = new NullCache();
	
	public ContextBasedOperationTransformer(ComplexOperationTransformer complexTransfomer) {
		assertNotNull("complexTransfomer", complexTransfomer);
		this.complexTransfomer = complexTransfomer;
	}

	public ContextualizedComplexOperation transform(ContextualizedComplexOperation operation, DocumentState modelState) throws CollisionException {
		assertNotNull("operation", operation); assertNotNull("modelState", modelState);
		logger.info(Formats.format("Transforming operation {0} to match state {1}.", operation, modelState));
		
		backedOperationsSet = modelState.copy();
		ContextualizedComplexOperation transformed;
		try {
			BackedContextDifference difference = modelState.minus(contextOf(operation));
			transformed = doTransform(operation, difference);
		} catch (NoSuchOperationException e) {
			logger.severe("Exception while transforming: " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		logger.info(Formats.format("Transformed operation is {0}.", transformed));
		return transformed;
	}

	private ContextualizedComplexOperation doTransform(ContextualizedComplexOperation operation, BackedContextDifference difference) throws NoSuchOperationException, CollisionException {
		ContextualizedComplexOperation current, currentTransformed, transformed = operation;
		
		while (difference.size() != 0) {
			current = (ContextualizedComplexOperation) difference.selectAndRemoveWithSubContextOf(transformed);
			currentTransformed = upgradeContextToMatch(current, transformed);
			transformed = simpleTransform(transformed, currentTransformed);
		}
		
		return transformed;
	}

	private ContextualizedComplexOperation upgradeContextToMatch(ContextualizedComplexOperation current, ContextualizedComplexOperation transformed)
			throws NoSuchOperationException, CollisionException {
		ContextualizedComplexOperation currentTransformed;
		if (cache.hasVersion(current, transformed.getContext())) {
			currentTransformed = (ContextualizedComplexOperation) cache.getVersion(current, transformed.getContext());
		} else {
			currentTransformed = _transform(current, contextOf(transformed) .minus (contextOf(current)) );
			cache.storeVersion(current, currentTransformed);
		}
		return currentTransformed;
	}

	private ContextualizedComplexOperation simpleTransform(ContextualizedComplexOperation first, ContextualizedComplexOperation second)	throws CollisionException {
		TransformationResult result = complexTransfomer.transform(first, second);
		cache.storeVersion(first, result.getFirstTransformed());
		// we are very likely to need the transposed transformation very soon
		cache.storeVersion(second, result.getSecondTransformed());
		return result.getFirstTransformed();
	}

	
	private ContextualizedComplexOperation _transform(ContextualizedComplexOperation operation, ContextDifference diff) throws NoSuchOperationException, CollisionException {
		return doTransform(operation, diff.backedBy(backedOperationsSet));
	}

	private Context contextOf(ContextualizedOperation operation) {
		return operation.getContext();
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		if (cache == null){
			this.cache = new NullCache();
		} else {
			this.cache = cache;
		}
	}
}
