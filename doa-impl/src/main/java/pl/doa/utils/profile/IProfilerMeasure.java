package pl.doa.utils.profile;

public interface IProfilerMeasure {

    public void actionStarted(IProfiledAction action);

    public void actionFinished();
}
