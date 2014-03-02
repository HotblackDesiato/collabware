package collabware.api.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import collabware.api.document.ModifyableDocument;

public class ComplexOperationImpl implements ComplexOperation {

	private final String description;
	private final List<PrimitiveOperation> primitives;
	private ComplexOperation inverse = null;
	
	public ComplexOperationImpl(String description, List<PrimitiveOperation> primitives) {
		if (primitives == null) throw new IllegalArgumentException("primities must not be null");
//		if (primitives.isEmpty()) throw new IllegalArgumentException("primitives must not be empty");
		this.description = description;
		this.primitives = Collections.unmodifiableList(new ArrayList<PrimitiveOperation>(primitives));
	}

	@Override
	public void apply(ModifyableDocument graph) throws OperationApplicationException {
		for (PrimitiveOperation op: primitives) {
			op.apply(graph);
		}
	}

	@Override
	public Iterator<PrimitiveOperation> iterator() {
		return primitives.iterator();
	}

	@Override
	public List<? extends PrimitiveOperation> getPrimitiveOperations() {
		return primitives;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int size() {
		return primitives.size();
	}

	@Override
	public ComplexOperation inverse() {
		if (inverse == null) {
			inverse = createInverse();			
		}
		return inverse;
	}

	private ComplexOperation createInverse() {
		List<PrimitiveOperation> inversePrimities = new ArrayList<PrimitiveOperation>(primitives.size());
		for (int i = primitives.size() -1; i >= 0; i--) {
			PrimitiveOperation o = primitives.get(i);
			inversePrimities.add(o.inverse());
		}
		return new ComplexOperationImpl(description, inversePrimities);
	}

	@Override
	public String toString() {
		return "ComplexOperationImpl [description=" + description
				+ ", primitives=" + primitives + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((primitives == null) ? 0 : primitives.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComplexOperationImpl other = (ComplexOperationImpl) obj;
		if (description == null) {
			if (other.description != null)
				return false;
			
		} else if (!description.equals(other.description))
			return false;
		if (primitives == null) {
			if (other.primitives != null)
				return false;
		} else if (!primitives.equals(other.primitives))
			return false;
		return true;
	}

}
