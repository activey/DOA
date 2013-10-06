/**
 *
 */
package pl.doa.impl;

import pl.doa.utils.PathIterator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author activey
 */
public abstract class AbstractPathIterator implements Iterable<String>,
        PathIterator<String>, Serializable {

    private boolean backwards;
    private List<String> parts = new ArrayList<String>();
    private String originalLocation;
    private int originalDepth;
    private int currentDepth;
    private String currentLocationPart;

    public AbstractPathIterator() {

    }

    public AbstractPathIterator(String path) {
        this(path, false);
    }

    public AbstractPathIterator(String path, boolean backwards) {
        this.backwards = backwards;
        StringTokenizer locationTokenizer =
                new StringTokenizer(path, getPathSeparator());
        int index = 0;
        while (locationTokenizer.hasMoreTokens()) {
            String locationPart = locationTokenizer.nextToken();
            if (index == 0 && ".".equals(locationPart)) {
                continue;
            }
            String part = locationPart;
            if (backwards && !locationTokenizer.hasMoreTokens()) {
                this.currentLocationPart = part;
            }
            parts.add(part);
        }
        if (!backwards) {
            this.currentLocationPart = "/";
        } else {
            this.currentDepth = parts.size();
        }
        this.originalDepth = parts.size();
        this.originalLocation = path;
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        int possibleDepth = currentDepth + 1;
        if (possibleDepth > originalDepth) {
            return false;
        }
        return true;
    }

    @Override
    public String next() {
        currentDepth++;
        this.currentLocationPart = parts.get(currentDepth - 1);
        return this.currentLocationPart;
    }

    @Override
    public void remove() {
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public int getOriginalDepth() {
        return originalDepth;
    }

    public int getRemainingDepth() {
        return originalDepth - currentDepth;
    }

    public String getCurrentPathPart() {
        return currentLocationPart;
    }

    public String travelTo(int locationLevel) {
        if (currentDepth == locationLevel) {
            return this.currentLocationPart;
        }
        if (locationLevel > originalDepth) {
            return travelTo(originalDepth);
        }
        this.currentDepth = locationLevel;
        this.currentLocationPart = parts.get(currentDepth - 1);
        return this.currentLocationPart;
    }

    public boolean isRoot() {
        return originalDepth == 0;
    }

    @Override
    public String toString() {
        return this.originalLocation;
    }

    public void trim() {
        if (currentDepth == 0) {
            return;
        }
        this.parts = parts.subList(currentDepth, originalDepth);
        StringBuffer newLocation = new StringBuffer(getPathSeparator());
        for (int i = 0; i < parts.size(); i++) {
            String locationPart = parts.get(i);
            newLocation.append(locationPart).append(getPathSeparator());
        }
        this.currentLocationPart = new String(getPathSeparator());
        this.originalDepth = parts.size();
        this.originalLocation = newLocation.toString();
        this.currentDepth = 0;
    }

    @Override
    public void add(String arg0) {
    }

    @Override
    public boolean hasPrevious() {
        return currentDepth > 0;
    }

    @Override
    public int nextIndex() {
        return currentDepth + 1;
    }

    @Override
    public String previous() {
        currentDepth--;
        this.currentLocationPart = parts.get(currentDepth);
        return this.currentLocationPart;
    }

    @Override
    public int previousIndex() {
        return currentDepth - 1;
    }

    @Override
    public void set(String currentPart) {
        currentLocationPart = currentPart;
    }

    public String getTraveledPath() {
        StringBuffer locationBuffer = new StringBuffer();
        if (backwards) {
            for (int i = currentDepth; i < originalDepth; i++) {
                locationBuffer.append(getPathSeparator()).append(parts.get(i));
            }
        } else {
            for (int i = 0; i < currentDepth; i++) {
                locationBuffer.append(getPathSeparator()).append(parts.get(i));
            }
        }
        return locationBuffer.toString();
    }

    public String getRemainingPath() {
        StringBuffer locationBuffer = new StringBuffer();
        if (backwards) {
            for (int i = 0; i < currentDepth; i++) {
                locationBuffer.append(getPathSeparator()).append(parts.get(i));
            }
        } else {
            for (int i = currentDepth; i < originalDepth; i++) {
                locationBuffer.append(getPathSeparator()).append(parts.get(i));
            }
        }
        return locationBuffer.toString();
    }

    public String getOriginalLocation() {
        return originalLocation;
    }

    @Override
    public boolean isRootPath() {
        return getPathSeparator().equals(originalLocation);
    }

    @Override
    public long getLength() {
        return originalDepth;
    }

    protected abstract String getPathSeparator();


}