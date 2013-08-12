package pl.doa.neo.utils;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;

import pl.doa.IDOA;
import pl.doa.NeoEntityDelegator;
import pl.doa.entity.IEntity;

public class EntitiesListIterator implements Iterator<IEntity> {

	private final Iterator<Path> traverser;
	private final IDOA doa;

	public EntitiesListIterator(IDOA doa, Iterator<Path> traverser) {
		this.doa = doa;
		this.traverser = traverser;
	}

	@Override
	public boolean hasNext() {
		return traverser.hasNext();
	}

	@Override
	public IEntity next() {
		Path entityPath = traverser.next();
		Node entityNode = entityPath.endNode();
		return NeoEntityDelegator.createEntityInstance(doa, entityNode);
	}

	@Override
	public void remove() {
		traverser.remove();
	}
}
