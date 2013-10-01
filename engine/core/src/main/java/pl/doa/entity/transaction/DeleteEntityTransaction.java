package pl.doa.entity.transaction;

import pl.doa.entity.IEntity;
import pl.doa.entity.ITransactionCallback;

public class DeleteEntityTransaction implements ITransactionCallback<Boolean> {

    private IEntity entity;

    public DeleteEntityTransaction(IEntity entity) {
        this.entity = entity;
    }

    @Override
    public Boolean performOperation() throws Exception {
        return entity.remove(true);
    }

}
