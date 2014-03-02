package collabware.model.internal;

import java.util.ArrayList;
import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.model.Model;
import collabware.model.graph.Node;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.operations.GraphOperationsProvider;

class ModelSerializer {
	private final GraphOperationsProvider operationsProvider;
	private List<PrimitiveOperation> primtiveOperations;

	public ModelSerializer(GraphOperationsProvider operationsProvider) {
		this.operationsProvider = operationsProvider;
	}

	public ComplexOperation serializeModel(Model m) {
		primtiveOperations = new ArrayList<PrimitiveOperation>();
		for (Node n: m.getGraph().getNodes()) {
			addAddNodeOperations(n);
			addSetAttributeOperations(n);
		}
		addReferenceOperations(m);
		return new ComplexOperationImpl("Model initialization", primtiveOperations);
	}


	private void addAddNodeOperations(Node n) {
		if (!isRootNode(n)) {
			AddNodeOperation addNode = operationsProvider.createAddNodeOperation(n.getId());
			primtiveOperations.add(addNode);				
		}
	}

	private boolean isRootNode(Node n) {
		return n.getGraph().getRootNode().equals(n);
	}

	private void addSetAttributeOperations(Node n) {
		for (String attrName: n.getAttributes()) {
			String nodeId = n.getId();
			Object newValue = n.getAttributes().get(attrName);
			PrimitiveOperation op = operationsProvider.createSetAttributeOperation(nodeId , attrName, null, newValue);
			primtiveOperations.add(op);
		}
	}

	private void addReferenceOperations(Model m) {
		for (Node n: m.getGraph().getNodes()) {
			addUnaryReferenceOperations(n);
			addNaryReferenceOperations(n);
		}
	}

	private void addNaryReferenceOperations(Node n) {
		for (String refName: n.getNaryReferences()) {
			int index = 0;
			for (Node target: n.getNaryReferences().get(refName)) {
				PrimitiveOperation op = operationsProvider.createAddReferenceOperation(n.getId(), refName, index, target.getId());
				primtiveOperations.add(op);
				index++;
			}
		}
	}

	private void addUnaryReferenceOperations(Node n) {
		for (String refName: n.getUnaryReferences()) {
			Node target = n.getUnaryReferences().get(refName);
			PrimitiveOperation op = operationsProvider.createSetUnaryReferenceOperation(n.getId(), refName, null, target.getId());
			primtiveOperations.add(op);				
		}
	}

}
