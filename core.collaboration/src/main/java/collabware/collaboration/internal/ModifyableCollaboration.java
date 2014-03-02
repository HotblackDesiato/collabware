package collabware.collaboration.internal;

import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.collaboration.Collaboration;
import collabware.collaboration.ConflictingOperationsException;
import collabware.userManagement.User;

/**
 * A {@link Collaboration} that can be modified by applying {@link ContextualizedComplexOperation}s.
 *
 */
interface ModifyableCollaboration extends Collaboration {

	/**
	 * Applies an operation to the underlying document on behalf of a user.
	 * @param operation
	 * @param user
	 * @throws ConflictingOperationsException if {@code operation} is in conflict with another concurrent operation.
	 */
	void apply(ContextualizedComplexOperation operation, User user) throws ConflictingOperationsException;
}
