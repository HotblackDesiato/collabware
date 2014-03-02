package collabware.model;

import collabware.api.document.ModifyableDocument;
import collabware.model.graph.ModifyableGraph;

public interface ModifyableModel extends Model, ModifyableDocument {

	ModifyableGraph getGraph();

	
}
