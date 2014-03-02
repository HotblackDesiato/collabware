package collabware.collaboration.internal.client.replay;

import java.util.List;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.collaboration.client.replay.ReplayControl;
import collabware.collaboration.client.replay.ReplayListener;

public class ReplayControlImpl implements ReplayControl {

	private final  ModifyableDocument model;
	private List<ComplexOperation> replaySequence;
	private int currentPosition = 0;
	private final ReplayListener listener;
	
	public ReplayControlImpl(ModifyableDocument document, ReplayListener listener) {
		this.model = document;
		this.listener = listener;
	}

	public void setReplaySequence(List<ComplexOperation> replaySequence) {
		this.replaySequence = replaySequence;
	}
	
	@Override
	public boolean canPrevious() {
		return currentPosition > 0;
	}

	@Override
	public boolean canNext() {
		return currentPosition < replaySequence.size();
	}

	@Override
	public void beginning() {
		seek(0);
	}

	@Override
	public void previous() {
		if (canPrevious()){
			currentPosition--;
			ComplexOperation previousOperation = replaySequence.get(currentPosition);
			model.apply(previousOperation.inverse());
			listener.replayed(previousOperation);
		}
	}

	@Override
	public void next() {
		if (canNext()) {
			ComplexOperation nextOperation = replaySequence.get(currentPosition);
			model.apply(nextOperation);
			currentPosition++;
			listener.replayed(nextOperation);
		}
	}

	@Override
	public void end() {
		seek(length());
	}

	@Override
	public int length() {
		return this.replaySequence.size();
	}

	@Override
	public void seek(int position) {
		if (position > currentPosition)
			seekForward(position);
		else
			seekBackward(position);
	}

	private void seekBackward(int position) {
		while (position < currentPosition) {
			previous();
		}
	}

	private void seekForward(int position){
		while (position > currentPosition) {
			next();
		}
	}

	@Override
	public int getPosition() {
		return currentPosition;
	}

}
