package collabware.model.internal;

import static collabware.utils.Asserts.assertNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.api.document.change.ComplexChangeEnded;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.Operation;
import collabware.api.operations.OperationApplicationException;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableGraph;
import collabware.model.internal.graph.GraphImpl;
import collabware.model.internal.ops.GraphOperationsProviderImpl;
import collabware.utils.Formats;

public class ModelImpl implements ModifyableModel, ChangeListener {

	private static final Logger logger = Logger.getLogger("collabware.model.internal.ModelImpl"); 
	
	private final String type;
	private final Set<ChangeListener> listeners = new HashSet<ChangeListener>();
	private final ModifyableGraph graph = new GraphImpl(this);

	private final ModelSerializer modelSerializer = new ModelSerializer(new GraphOperationsProviderImpl());
	
	public ModelImpl(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public ModifyableGraph getGraph() {
		return graph;
	}

	public void apply(Operation op) {
		logger.info("Applying operation " + op.toString());
		try {
			op.apply(this);
			if (op instanceof ComplexOperation) notifyChange(new ComplexChangeEnded(((ComplexOperation)op).getDescription()));
		} catch (OperationApplicationException e) {
			logger.severe(Formats.format("Exception while applying operation {0}: {1}", op, e.getMessage()));
			throw new RuntimeException(e);
		}
		logger.info(Formats.format("Operation {0} applied.", op));
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		assertNotNull("listener", listener);
		listeners.add(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		assertNotNull("listener", listener);
		listeners.remove(listener);
	}

	@Override
	public void notifyChange(Change change) {
		for(ChangeListener listener: listeners) {
			try {
				listener.notifyChange(change);
			} catch (Exception e) {
				logger.severe("Error while notifying listener: " + e.getMessage());
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ModelImpl other = (ModelImpl) obj;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
//		if (type == null) {
//			if (other.type != null)
//				return false;
//		} else if (!type.equals(other.type))
//			return false;
		return true;
	}

	public void clear() {
		this.graph.clear();
	}

	@Override
	public boolean isEmpty() {
		return this.graph.getNodes().size() == 1; // there is always a root node!
	}

	@Override
	public ComplexOperation asOperation() {
		return modelSerializer.serializeModel(this);
	}

	@Override
	public String getDocumentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		return type;
	}
}
