package collabware.web.client.replay;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import collabware.collaboration.client.replay.Replay;

import com.google.gwt.core.client.JavaScriptObject;

@Export
public class ReplayWrapper implements Exportable {

	private Replay replay;
	private ReplayControlWrapper replayControl;

	public ReplayWrapper(Replay replay) {
		this.replay = replay;
		this.replayControl = new ReplayControlWrapper(replay.getReplayControl());
	}
	
	@Export
	public void addListener(JavaScriptObject listener) {
		replay.addListener(new ReplayListenerWrapper(listener));
	}
	
	@Export
	public void enter() {
		replay.enter();
	}
	
	@Export
	public ReplayControlWrapper getReplayControl() {
		return replayControl;
	}

	@Export
	public void exit() {
		replay.exit();
	}

}
