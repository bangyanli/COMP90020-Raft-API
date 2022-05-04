package com.handshake.raft;

import com.handshake.raft.config.LibraryConfig;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.Node;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.raft.raftServer.log.LogSystem;
import com.handshake.raft.raftServer.rpc.RpcClient;
import com.handshake.raft.raftServer.rpc.RpcServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;

@Component
public class init implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(init.class);

    @Autowired
    NodeConfig nodeConfig;

    @Autowired
    LibraryConfig libraryConfig;

    @Autowired
    RpcServiceProvider rpcServiceProvider;

    @Autowired
    RpcClient rpcClient;

    @Autowired
    RaftThreadPool raftThreadPool;

    @Autowired
    LogSystem logSystem;

    @Autowired
    Node node;


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(nodeConfig.toString());
        logger.info(libraryConfig.toString());
        initLibrary();
        logSystem.init();
        raftThreadPool.init();
        rpcServiceProvider.init();
        rpcClient.init();
        node.init();
    }

    @PreDestroy
    public void stop() {
        logger.info("###STOPing###");
        node.stop();
        rpcClient.stop();
        rpcServiceProvider.stop();
        logSystem.stop();
        logger.info("###STOP FROM THE LIFECYCLE###");
    }

    /**
     * create the library if it does not exist
     */
    public void initLibrary(){
        File file = new File(libraryConfig.getAddress());
        if(!file.exists()){
            if(!file.mkdir()){
                System.out.println("Fail to make dictionary!");
                System.exit(-1);
            }
        }
        logger.info("Successfully connect to library!");
    }
}
