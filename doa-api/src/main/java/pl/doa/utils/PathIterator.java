package pl.doa.utils;

import java.util.ListIterator;

public interface PathIterator<T> extends ListIterator<T> {

	public String getTraveledPath();

	public String getRemainingPath();
	
	public String getCurrentPathPart();

	public boolean isRootPath();
	
	public long getLength();
}
