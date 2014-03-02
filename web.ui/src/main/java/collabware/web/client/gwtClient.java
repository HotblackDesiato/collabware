package collabware.web.client;

import org.timepedia.exporter.client.Exporter;

import collabware.web.client.replay.ComplexOperationWrapper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class gwtClient implements EntryPoint {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";


  private native void onLoadImpl() /*-{
	  console.log("Starting client");
	  if ($wnd.gwtClientLoaded && typeof ($wnd.gwtClientLoaded) === 'function') {
		$wnd.gwtClientLoaded();
	  }
	}-*/;

  
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  ((Exporter) GWT.create(SessionWrapper.class)).export();
	  ((Exporter) GWT.create(LocalClient.class)).export();
	  ((Exporter) GWT.create(ComplexOperationWrapper.class)).export();

	  onLoadImpl();
  }
}
