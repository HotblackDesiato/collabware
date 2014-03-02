package collabware.web.client.replay;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import collabware.collaboration.client.replay.ReplayControl;

@Export("ReplayControl")
public class ReplayControlWrapper implements ReplayControl, Exportable {

	private ReplayControl replayControl;

	public ReplayControlWrapper(ReplayControl replayControl) {
		this.replayControl = replayControl;
	}

	@Export
	@Override
	public boolean canPrevious() {
		return replayControl.canPrevious();
	}

	@Export
	@Override
	public boolean canNext() {
		return replayControl.canNext();
	}
	
	@Export
	@Override
	public void beginning() {
		replayControl.beginning();
	}
	
	@Export
	@Override
	public void previous() {
		replayControl.previous();
	}
	
	@Export
	@Override
	public void next() {
		replayControl.next();
	}

	@Export
	@Override
	public void end() {
		replayControl.end();
	}

	@Export
	@Override
	public int length() {
		return replayControl.length();
	}

	@Export
	@Override
	public void seek(int position) {
		replayControl.seek(position);
	}

	@Export
	@Override
	public int getPosition() {
		return replayControl.getPosition();
	}

}
