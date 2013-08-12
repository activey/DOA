/**
 * 
 */
package pl.doa.neo.utils.profile;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.relation.DOARelationship;
import pl.doa.utils.profile.IProfiledAction;

/**
 * @author activey
 * 
 */
public class LookupForNodeAction implements IProfiledAction<Node> {

	private final static Logger log = LoggerFactory
			.getLogger(LookupForNodeAction.class);
	private final Node startNode;
	private final EntityLocationIterator path;

	public LookupForNodeAction(Node startNode, EntityLocationIterator path) {
		this.startNode = startNode;
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see pl.doa.utils.profile.IProfiledAction#invoke()
	 */
	@Override
	public Node invoke() throws GeneralDOAException {
		Traverser traverser =
				Traversal
						.description()
						.depthFirst()
						.evaluator(Evaluators.toDepth(path.getOriginalDepth()))
						.evaluator(Evaluators.excludeStartPosition())
						.uniqueness(Uniqueness.NODE_PATH)
						.relationships(DOARelationship.HAS_ENTITY,
								Direction.OUTGOING).evaluator(new Evaluator() {

							@Override
							public Evaluation evaluate(Path path) {
								EntityLocationIterator entityLocation =
										LookupForNodeAction.this.path;
								if (path.length() < entityLocation
										.getCurrentDepth()) {
									return Evaluation.EXCLUDE_AND_PRUNE;
								}
								Node startNode = path.startNode();
								Node endNode = path.endNode();
								if (startNode.equals(endNode)) {
									return Evaluation.EXCLUDE_AND_CONTINUE;
								}
								if (!endNode.hasProperty("name")) {
									return Evaluation.EXCLUDE_AND_PRUNE;
								}
								String locationPart =
										entityLocation.travelTo(path.length());
								String nodeName =
										(String) endNode.getProperty("name");
								if (locationPart.equals(nodeName)) {
									if (path.length() == LookupForNodeAction.this.path
											.getOriginalDepth()) {
										return Evaluation.INCLUDE_AND_PRUNE;
									} else {
										if (endNode.hasProperty("_is_doa")) {
											return Evaluation.INCLUDE_AND_PRUNE;
										}
										return Evaluation.EXCLUDE_AND_CONTINUE;
									}
								}
								return Evaluation.EXCLUDE_AND_PRUNE;
							}
						}).traverse(startNode);
		try {
			for (Path pathFragment : traverser) {
				Node node = pathFragment.endNode();
				return node;
			}
		} catch (Throwable t) {
			throw new GeneralDOAException(t);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see pl.doa.utils.profile.IProfiledAction#getActionId()
	 */
	@Override
	public String getActionData() {
		return path.getOriginalLocation();
	}

	@Override
	public String getActionName() {
		return "LookupForNodeAction";
	}
}
