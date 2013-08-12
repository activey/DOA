/*******************************************************************************
 * Copyright 2011 Inhibi Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are
 * permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright 
 * notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright 
 * notice, this list
 *        of conditions and the following disclaimer in the documentation 
 * and/or other materials
 *        provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY INHIBI LTD ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * INHIBI LTD OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation 
 * are those of the authors and should not be interpreted as representing 
 * official policies, either expressed or implied, of Inhibi Ltd.
 *
 * Contributors:
 *    Inhibi Ltd - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package pl.doa.entity.impl.neo;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

import pl.doa.IDOA;
import pl.doa.NeoEntityDelegator;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;

/**
 * @author activey
 * 
 */
public class NeoReturnableEvaluator implements IEntityEvaluator, Evaluator {

	private IEntityEvaluator evaluator;
	private final IDOA doa;
	private boolean deep;

	public NeoReturnableEvaluator(IDOA doa, IEntityEvaluator evaluator,
			boolean deep) {
		this.doa = doa;
		this.evaluator = evaluator;
		this.deep = deep;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * pl.doa.entity.IReturnableEvaluator#isReturnableEntity(pl.doa.entity.IEntity
	 * )
	 */

	public boolean isReturnableEntity(IEntity currentEntity) {
		return (evaluator == null) ? true : evaluator
				.isReturnableEntity(currentEntity);
	}

	@Override
	public Evaluation evaluate(Path path) {
		if (path.length() == 0) {
			return Evaluation.EXCLUDE_AND_CONTINUE;
		}
		Node currentNode = path.endNode();
		if (currentNode == null) {
			return Evaluation.EXCLUDE_AND_PRUNE;
		}
		IEntity entity =
				NeoEntityDelegator.createEntityInstance(doa, currentNode);
		if (entity == null) {
			return Evaluation.EXCLUDE_AND_PRUNE;
		}
		if (isReturnableEntity(entity)) {
			return Evaluation.INCLUDE_AND_PRUNE;
		}
		return (deep) ? Evaluation.EXCLUDE_AND_CONTINUE
				: Evaluation.EXCLUDE_AND_PRUNE;
	}

}
