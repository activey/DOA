package pl.doa;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Traverser;


public abstract class NeoNodesIterator<T> implements Iterator<T> {

	private final Iterator<Node> neoIterator;

	public NeoNodesIterator(Traverser fieldsNodesTraverser) {
		this.neoIterator = fieldsNodesTraverser.iterator();
		
	}
	
	@Override
	public boolean hasNext() {
		return neoIterator.hasNext();
	}

	@Override
	public T next() {
		Node node = neoIterator.next();
		return next(node);
	}

	public abstract T next(Node node);
	
	@Override
	public void remove() {
		neoIterator.remove();
	}

	
}
