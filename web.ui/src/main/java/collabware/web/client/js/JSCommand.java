package collabware.web.client.js;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import collabware.api.document.ModifyableDocument;
import collabware.collaboration.client.Command;
import collabware.model.internal.ModelImpl;

@ExportPackage("collabware")
@Export
public class JSCommand implements Command, Exportable {
	private final String description;
	private final ChangeFunction change;

	JSCommand(String description, ChangeFunction change) {
		this.description = description;
		this.change = change;
	}

	public String getDescription() {
		return description;
	}

	public void apply(ModifyableDocument doc) {
		change.apply(new JSGraph(((ModelImpl)doc).getGraph()));
	}
}