package collabware.transformer.internal;

import org.springframework.beans.factory.annotation.Autowired;

import collabware.api.document.PrimitiveOperationTransformer;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.transformer.PrecedenceRule;
import collabware.transformer.TransformationProvider;
import collabware.transformer.Transformer;
public class TransformationProviderImpl implements TransformationProvider {

	private PrimitiveOperationTransformer partiallyInclusiveOperationalTransformation;
	private PrecedenceRule precedenceRule = new PrecedenceRule() {
		@Override
		public boolean hasPrecedence(ContextualizedComplexOperation first, ContextualizedComplexOperation second) {
			if (first.getArrivalTimeStamp() < second.getArrivalTimeStamp()) {
				return true;
			} else if (first.getArrivalTimeStamp() > second.getArrivalTimeStamp()) {
				return false;
			} else {
				throw new RuntimeException("This should never happen!");
			}
		}
	};
	
	public Transformer createTransformer() {
		return createTransformer(precedenceRule);
	}
	
	public Transformer createTransformer(PrecedenceRule precedence) {
		return new ContextBasedOperationTransformer(new ExclusiveComplexTransformer(partiallyInclusiveOperationalTransformation, precedence));
	}

	@Autowired
	public void setPartiallyInclusiveOperationalTransformation(PrimitiveOperationTransformer graphOperationsProvider) {
		this.partiallyInclusiveOperationalTransformation = graphOperationsProvider;
	}

}
