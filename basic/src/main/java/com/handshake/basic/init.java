package com.handshake.basic;

import com.handshake.basic.config.LibraryConfig;
import com.handshake.basic.config.NodeConfig;
import com.handshake.basic.raftServer.Node;
import com.handshake.basic.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.basic.raftServer.rpc.RpcClient;
import com.handshake.basic.raftServer.rpc.RpcServiceProvider;
import com.handshake.basic.service.Impl.WebSocketServer;
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
    Node node;

    @Autowired
    WebSocketServer webSocketServer;


    @Override
    public void afterPropertiesSet() throws Exception {
        //logger.info(nodeConfig.toString());
        //logger.info(libraryConfig.toString());
        initLibrary();
        raftThreadPool.init();
        rpcServiceProvider.init();
        rpcClient.init();
        node.init();
        webSocketServer.startSendLog();
    }

    @PreDestroy
    public void stop() {
        logger.info("###STOPING###");
        node.stop();
        rpcClient.stop();
        rpcServiceProvider.stop();
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
