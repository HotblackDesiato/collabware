package collabware.web.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

@Export
@ExportPackage("collabware")
@ExportClosure
public interface Callback extends Exportable {
	public void call();
}
