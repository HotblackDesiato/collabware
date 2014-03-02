package collabware.collaboration.internal;

import java.util.Collections;
import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.ReplaySequence;

public class ReplaySequenceImpl implements ReplaySequence {

	private final ComplexOperation init;
	private final List<ComplexOperation> replaySequence;

	public ReplaySequenceImpl(ComplexOperation init, List<ComplexOperation> replaySequence) {
		this.init = init;
		this.replaySequence = Collections.unmodifiableList(replaySequence);
	}

	@Override
	public ComplexOperation getInit() {
		return init;
	}

	@Override
	public ComplexOperation get(int i) {
		return replaySequence.get(i);
	}

	@Override
	public int length() {
		return replaySequence.size();
	}

}
