package collabware.model.internal;

import static collabware.model.operations.SerializationConstants.ADD_NODE_OPERATION;
import static collabware.model.operations.SerializationConstants.ADD_REFERENCE_OPERATION;
import static collabware.model.operations.SerializationConstants.REMOVE_NODE_OPERATION;
import static collabware.model.operations.SerializationConstants.REMOVE_REFERENCE_OPERATION;
import static collabware.model.operations.SerializationConstants.SET_ATTRIBUTE_OPERATION;
import static collabware.model.operations.SerializationConstants.SET_REFERENCE_OPERATION;
import static collabware.model.operations.SerializationConstants.TYPE;

import java.util.Map;

import collabware.api.operations.OperationGenerator;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.GraphOperationGenerator;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;
import collabware.model.internal.ops.transformation.GraphTransformationMatrix;
import collabware.model.internal.parser.ModelLiteralParser;

public class ModelProviderImpl implements ModelProvider {

	private static final String DOCUMENT_TYPE = "collabware.model";
	
	private final GraphTransformationMatrix trans = new GraphTransformationMatrix();
	
	@Override
	public ModifyableModel createDocument(String type) {
		return new ModelImpl(type);
	}

	@Override
	public ModifyableModel createModelFromLiteral(String type, String literal) {
		ModelLiteralParser parser = new ModelLiteralParser(this);
		return parser.modelFromLiteral(type, literal);
	}

	@Override
	public void populateModelFromLiteral(ModifyableModel model, String literal) {
		ModelLiteralParser parser = new ModelLiteralParser(this);
		parser.populateModelFromLiteral(model, literal);
	}

	@Override
	public String getDocumentType() {
		return DOCUMENT_TYPE;
	}

	@Override
	public OperationGenerator createOperationGenerator() {
		return new GraphOperationGenerator();
	}

	@Override
	public PrimitiveOperation deserializeOperation(Map<String, Object> map) {
		Object type = map.get(TYPE);
		if (type.equals(ADD_NODE_OPERATION)){
			return new AddNodeOperation(map);
		} else if (type.equals(REMOVE_NODE_OPERATION)){
			return new RemoveNodeOperation(map);
		} else if (type.equals(SET_ATTRIBUTE_OPERATION)){
			return new SetAttributeOperation(map);
		} else if (type.equals(SET_REFERENCE_OPERATION)){
			return new SetUnaryReferenceOperation(map);
		} else if (type.equals(ADD_REFERENCE_OPERATION)){
			return new AddReferenceOperation(map);
		} else if (type.equals(REMOVE_REFERENCE_OPERATION)){
			return new RemoveReferenceOperation(map);
		} else {
			throw new IllegalArgumentException("Cannot de-serialize '" + map +"'.");
		}
	}

	@Override
	public PrimitiveOperation transform(PrimitiveOperation base, PrimitiveOperation against) throws CollisionException {
		return trans.transform(base).against(against);
	}

}
