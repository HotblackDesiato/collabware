package collabware.model.internal.ops;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.OperationApplicationException;
import collabware.api.operations.PrimitiveOperation;

public abstract class BasePrimitiveOperation implements PrimitiveOperation {

	private ComplexOperation complex;
	
	public ComplexOperation getComplexOperation() {
		return complex;
	}
	
	public void setComplexOperation(ComplexOperation complex) {
		this.complex = complex;
	}

	public void apply(ModifyableDocument graph) throws OperationApplicationException {
		throw new UnsupportedOperationException("Not implemented");
	}

	public PrimitiveOperation inverse() {
		throw new UnsupportedOperationException("Not implemented");
	}
}
