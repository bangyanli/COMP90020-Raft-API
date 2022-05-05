package com.handshake.raft.raftServer.ThreadPool;

import com.handshake.raft.raftServer.LifeCycle;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@Data
public class RaftThreadPool implements LifeCycle {

    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture electionTimer = null;
    private ScheduledFuture heartbeatTask = null;


    @Override
    public void init() {
        executorService = new ThreadPoolExecutor(
                20,
                20,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        scheduledExecutorService = Executors.newScheduledThreadPool(5);
    }


    @Override
    public void stop() {
        executorService.shutdown();
        scheduledExecutorService.shutdown();
    }
}
