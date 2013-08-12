package pl.doa.utils.profile;

import java.text.MessageFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.GeneralDOAException;

public class PerformanceProfiler {

    private static final String DOA_PROPERTY_PROFILER = "doa.profiler";
    private final static Logger perfLog = LoggerFactory.getLogger("PROFILER");

    public static <T> T runProfiled(IProfiledAction<T> action)
            throws GeneralDOAException {
        if (!isProfilerEnabled()) {
            return action.invoke();
        }
        String logEntryFormat = "{0};{1};{2};{3}";

        long start = System.currentTimeMillis();
        T actionResult = action.invoke();
        long duration = System.currentTimeMillis() - start;

        String logEntry =
                MessageFormat.format(logEntryFormat, new Date().getTime() + "",
                        action.getActionName(),
                        action.getActionData(), duration + "");
        perfLog.debug(logEntry);

        return actionResult;
    }

    private static boolean isProfilerEnabled() {
        String profilerProperty =
                System.getProperty(DOA_PROPERTY_PROFILER, "false");
        return Boolean.parseBoolean(profilerProperty);
    }
}
