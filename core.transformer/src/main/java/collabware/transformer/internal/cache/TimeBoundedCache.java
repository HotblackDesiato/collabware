package collabware.transformer.internal.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedOperation;

public class TimeBoundedCache implements Cache{

	private List<VersionGroup> versionGroups = new ArrayList<VersionGroup>();
	private int misses = 0;
	private int hits = 0;
	private long time;
	
	public TimeBoundedCache(long time) {
		this.time = time;
	}

	private VersionGroup getVersionGroup(ContextualizedOperation op) {
		for (VersionGroup group: versionGroups) {
			if (group.getOriginal().equals(op)) {
				return group;
			}
		}
		return null;
	}
	
	@Override
	public boolean hasVersion(ContextualizedOperation operation, Context targetContext) {
		VersionGroup versions = getVersionGroup(operation);
		if (versions != null) {
			boolean hasVersion = versions.hasVersion(targetContext);
			if (!hasVersion) misses++;
			return hasVersion;			
		} else {
			misses++;
			return false;
		}
	}
	@Override
	
	public ContextualizedOperation getVersion(ContextualizedOperation operation, Context targetContext) {
		VersionGroup versions = getVersionGroup(operation);
		if (versions != null) {
			ContextualizedOperation version = versions.getVersion(targetContext);
			hits++;
			return version;
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void storeVersion(ContextualizedOperation original, ContextualizedOperation version) {
		VersionGroup versions = getVersionGroup(original);
		if (versions == null) {
			versions = new VersionGroup(original);
			versionGroups.add(versions);
		}
		versions.addVersion(version);
	}

	@Override
	public void empty() {
		versionGroups.clear();
	}
	
	public int getNumberOfVersionGroups() {
		return versionGroups.size();
	}

	public int size() {
		int size = 0;
		for (VersionGroup group: this.versionGroups) {
			size += group.size();
		}
		return size;
	}
	
	Collection<VersionGroup> getVersionGroups() {
		return Collections.unmodifiableCollection(versionGroups);
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Size:   ").append(size()).append('\n');
		s.append("Hits:   ").append(hits).append('\n');
		s.append("Misses: ").append(misses).append('\n');
		
		for (VersionGroup group: versionGroups) {
			s.append(group.toString()).append('\n');
		}
		return s.toString();
	}

	@Override
	public void cleanUp() {
		for (VersionGroup group: versionGroups) {
			group.removeVersionOlderThan(System.currentTimeMillis() - time);
		}
	}
}
