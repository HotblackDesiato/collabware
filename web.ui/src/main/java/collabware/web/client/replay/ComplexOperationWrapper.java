package collabware.web.client.replay;

import java.util.Iterator;
import java.util.List;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.OperationApplicationException;
import collabware.api.operations.PrimitiveOperation;

@Export
public class ComplexOperationWrapper implements ComplexOperation, Exportable {

	private ComplexOperation complexOperation;

	public ComplexOperationWrapper(ComplexOperation op) {
		this.complexOperation = op;
	}
	
	@Export
	public static ComplexOperationWrapper wrap(ComplexOperation op) {
		return new ComplexOperationWrapper(op);
	}
	
	@Override
	public void apply(ModifyableDocument graph) throws OperationApplicationException {

	}

	@Override
	public Iterator<PrimitiveOperation> iterator() {
		return null;
	}

	@Override
	public List<? extends PrimitiveOperation> getPrimitiveOperations() {
		return null;
	}

	@Export
	@Override
	public String getDescription() {
		return complexOperation.getDescription();
	}

	@Override
	public ComplexOperation inverse() {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

}
