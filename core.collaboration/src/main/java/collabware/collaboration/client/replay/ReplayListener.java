package collabware.collaboration.client.replay;

import collabware.api.operations.ComplexOperation;

public interface ReplayListener {
	void replayed(ComplexOperation op);
	void started();
	void ended();
}
