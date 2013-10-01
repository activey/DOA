package pl.doa.thread.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.doa.thread.IThreadManager;

public class SimpleThreadManager implements IThreadManager {

    private static final String DEFAULT_NAME = "threadPoolManager";

    private static final int DEFAULT_POOL_SIZE = 40;

    private final Logger log = LoggerFactory
            .getLogger(SimpleThreadManager.class);

    private ExecutorService executorService;

    private String threadManagerName;

    public SimpleThreadManager() {
        this(DEFAULT_NAME, DEFAULT_POOL_SIZE);
    }

    public SimpleThreadManager(String threadManagerName) {
        this.threadManagerName = threadManagerName;
        this.executorService = Executors.newCachedThreadPool();
    }

    public SimpleThreadManager(String threadManagerName, int threadNumber) {
        this.threadManagerName = threadManagerName;
        this.executorService = Executors.newFixedThreadPool(threadNumber);
    }

    public SimpleThreadManager(String threadManagerName, int threadNumber,
                               String threadFactoryClass) {
        this.threadManagerName = threadManagerName;
        try {
            ThreadFactory threadFactory =
                    (ThreadFactory) Class.forName(threadFactoryClass)
                            .newInstance();
            this.executorService =
                    Executors.newFixedThreadPool(threadNumber, threadFactory);
        } catch (Exception ex) {
            this.executorService = Executors.newFixedThreadPool(threadNumber);
        }
    }

    @Override
    public void execute(Runnable runnable) {
        if (runnable == null) {
            log.info("Cannot execute empty thread");
            return;
        }
        log.debug("Adding thread to thread manager: " + threadManagerName);
        executorService.execute(runnable);
    }

}
