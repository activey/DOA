package pl.doa.entity.evaluator;

import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;

public class CompoundEvaluator implements IEntityEvaluator {

    private final IEntityEvaluator[] evaluators;

    private CompoundEvaluator(IEntityEvaluator... evaluators) {
        this.evaluators = evaluators;
    }

    @Override
    public boolean isReturnableEntity(IEntity currentEntity) {
        for (IEntityEvaluator evaluator : evaluators) {
            if (!evaluator.isReturnableEntity(currentEntity)) {
                return false;
            }
        }
        return true;
    }

    public static CompoundEvaluator forAll(IEntityEvaluator... evaluators) {
        return new CompoundEvaluator(evaluators);
    }
}
