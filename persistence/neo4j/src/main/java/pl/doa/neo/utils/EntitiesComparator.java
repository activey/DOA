package pl.doa.neo.utils;

import java.util.Comparator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;

import pl.doa.IDOA;
import pl.doa.NeoEntityDelegator;
import pl.doa.entity.IEntity;
import pl.doa.entity.sort.IEntitiesSortComparator;

public class EntitiesComparator<T extends IEntity> implements Comparator<Path> {

	private final IEntitiesSortComparator<T> comparator;
	private final IDOA doa;

	public EntitiesComparator(IDOA doa, IEntitiesSortComparator<T> comparator) {
		this.doa = doa;
		this.comparator = comparator;
	}

	@Override
	public int compare(Path path1, Path path2) {
		if (comparator == null) {
			return 0;
		}
		Node node1 = path1.endNode();
		Node node2 = path2.endNode();
		
		T entity1 = (T) NeoEntityDelegator.createEntityInstance(doa, node1);
		T entity2 = (T) NeoEntityDelegator.createEntityInstance(doa, node2);
		
		return comparator.compare(entity1, entity2);
	}

}
