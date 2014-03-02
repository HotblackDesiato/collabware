package collabware.web.client.js;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

@Export
@ExportClosure
public interface ChangeFunction extends Exportable {
	public void apply(JSGraph graph);
}
