package pl.doa.utils.profile;

import pl.doa.GeneralDOAException;

public interface IProfiledAction<T> {

    public T invoke() throws GeneralDOAException;

    public String getActionData();

    public String getActionName();
}
