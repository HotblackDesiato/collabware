package collabware.api.operations.context;


public interface BackedOperationsSet extends DocumentState {

	void add(ContextualizedOperation o1) throws NonConsecutiveContextException;

}
