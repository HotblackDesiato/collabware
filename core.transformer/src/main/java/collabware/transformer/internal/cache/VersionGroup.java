package collabware.transformer.internal.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;

public class VersionGroup {
	private final List<CachedVersion> versions = new ArrayList<CachedVersion>();
	private final ContextualizedOperation original;
	private int groupHitCount = 0;
	
	public VersionGroup(ContextualizedOperation original) {
		this.original = original;
	}

	private CachedVersion getCachedVersion(Context context) {
		for (CachedVersion version : versions) {
			if (version.getOperation().getContext().equals(context)) {
				return version;
			}
		}
		return null;
	}
	
	void addVersion(ContextualizedOperation version) {
		CachedVersion storedVersion = getCachedVersion(version.getContext());
		if(storedVersion == null) { 
			versions.add(new CachedVersion(version));
		} else if (storedVersion != null && !version.equals(storedVersion.getOperation())) {
			throw new UnsupportedOperationException(String.format("Cannot overwrite stored version %s with %s", storedVersion.getOperation(), version));
		} else {
			// do nothing
		}
	}
	
	ContextualizedOperation getVersion(Context context) {
		CachedVersion cachedVersion = getCachedVersion(context);
		if (cachedVersion != null) {
			ContextualizedOperation operation = cachedVersion.getOperation();
			cachedVersion.incrementHitCount();
			groupHitCount++;
			return operation;				
		} else {
			throw new NoSuchElementException();
		}
	}
	
	boolean hasVersion(Context context) {
		return getCachedVersion(context) != null;
	}
	
	int size() {
		return versions.size();
	}

	public ContextualizedOperation getOriginal() {
		return original;
	}

	int getGroupHitCount() {
		return groupHitCount;
	}
	
	Collection<CachedVersion> getVersions() {
		return Collections.unmodifiableCollection(versions);
	}
	
	public String toString() {
		StringBuffer versionsString = new StringBuffer();
		for (CachedVersion version : versions) {
			versionsString.append("\t " + version.toString());
		}
		return original.getContext() +"\t " + groupHitCount + versionsString;
	}

	public void removeVersionOlderThan(long l) {
		for (Iterator<CachedVersion> i = versions.iterator(); i.hasNext();) {
			CachedVersion version = i.next();
			if (version.getLastTouched() < l)
				i.remove();
		}
	}
}