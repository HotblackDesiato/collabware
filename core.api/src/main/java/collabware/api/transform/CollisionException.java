package collabware.api.transform;

import collabware.api.operations.Operation;

public class CollisionException extends Exception {
	
	private static final long serialVersionUID = 379417932211955043L;
	private final Operation offender;
	private final Operation offended;

	public CollisionException(Operation offender, Operation offended) {
		this.offender = offender;
		this.offended = offended;
	}
	

	public Operation getOffender() {
		return offender;
	}

	public Operation getOffended() {
		return offended;
	}

}
