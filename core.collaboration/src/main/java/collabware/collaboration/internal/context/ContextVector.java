package collabware.collaboration.internal.context;

import java.util.Arrays;

/**
 * A vector respresenting an context.
 * 
 * 
 */
public class ContextVector {
	private final int[] vector;
	private int hashCode = -1;
	
	
	public ContextVector(int[] vector) {
		this.vector = vector;
	}
	/**
	 * 
	 * @param i
	 * @return the {@code i}th entry.
	 */
	public int get(int i) {
		if (withinBounds(i)) 
			return vector[i];
		else 
			return 0;
	}

	int length() {
		return vector.length;
	}
	
	/**
	 * 
	 * @param i
	 * @return a new ContextVector with the {@code i}th entry incremented.
	 */
	ContextVector incrementing(int i) {
		int[] nextVector = copyVector(i);
		nextVector[i]++;
		return new ContextVector(nextVector);
	}

	boolean lessThanOrEqual(ContextVector superContext) {
		int maxLength = Math.max(length(), superContext.length());
		for (int i = 0; i < maxLength; i ++) {
			if (!(get(i) <= superContext.get(i)))
				return false;
		}
		return true;
	}
	
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof ContextVector)	return equalsContextVector((ContextVector) o);
		return false;
	}
	
	boolean equalsContextVector(ContextVector otherVector) {
		int maxLength = Math.max(length(), otherVector.length());
		for (int i = 0; i < maxLength; i ++) {
			if (get(i) != otherVector.get(i))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		if (hashCode != -1) {
			hashCode = vector.length * vector.length;
			for( int i= 0; i <vector.length; i++) {
				hashCode += i*vector[i];
			}
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(vector);
	}

	private int[] copyVector(int i) {
		int length = Math.max(length(), i+1);
		int[] nextVector = new int[length];
		System.arraycopy(vector, 0, nextVector, 0, vector.length); // Cannot use Arrays.copy since GWT does not implement it.
		return nextVector;
	}

	private boolean withinBounds(int i) {
		return i < vector.length;
	}

	ContextVector updating(int index, int newValue) {
		int[] nextVector = copyVector(index);
		nextVector[index] = newValue;
		return new ContextVector(nextVector);
	}
}