package collabware.model;

import collabware.api.document.Document;
import collabware.model.graph.Graph;

public interface Model extends Document {

	Graph getGraph();

}
