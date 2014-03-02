package collabware.transformer.internal.cache;

import collabware.api.operations.context.ContextualizedOperation;

public class CachedVersion {
	private int hitCount = 0;
	private final ContextualizedOperation version;
	private long lastTouched;
	
	public CachedVersion(ContextualizedOperation version) {
		this.version = version;
		this.lastTouched = System.currentTimeMillis();
	}
	
	public int getHitCount() {
		return hitCount;
	}
	
	public ContextualizedOperation getOperation() {
		this.lastTouched = System.currentTimeMillis();
		return version;
	}
	
	void incrementHitCount() {
		hitCount++;
	}
	
	public String toString() {
		return "" + hitCount;
	}

	public long getLastTouched() {
		return this.lastTouched;
	}
}