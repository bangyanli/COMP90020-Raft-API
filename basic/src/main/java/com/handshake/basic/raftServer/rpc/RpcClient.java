package com.handshake.basic.raftServer.rpc;

import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.handshake.basic.config.NodeConfig;
import com.handshake.basic.raftServer.LifeCycle;
import com.handshake.basic.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RpcClient implements LifeCycle {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private static final int TIME_OUT = 30000;

    @Autowired
    private NodeConfig nodeConfig;

    private final ConcurrentHashMap<String,ConsumerConfig<RaftConsensusService>> serverHashMap = new ConcurrentHashMap<>();

    /**
     * return rpc service or null
     * @param ip ip of remove server
     * @return rpc service or null
     */
    public RaftConsensusService connectToService(String ip){
        ConsumerConfig<RaftConsensusService> consumerConfig = serverHashMap.get(ip);
        RaftConsensusService raftConsensusService = null;
        try {
            raftConsensusService = consumerConfig.refer();
        }
        catch (Exception e){
            //logger.info(e.getMessage(),e);
            logger.info("Fail to connect to " + ip);
        }
        return raftConsensusService;
    }

    @Override
    public void init() {
        ArrayList<String> otherServer = nodeConfig.getOtherServers();
        for (String address: otherServer){
            ConsumerConfig<RaftConsensusService> consumerConfig = new ConsumerConfig<RaftConsensusService>()
                    .setInterfaceId(RaftConsensusService.class.getName())
                    .setProtocol("bolt")
                    .setDirectUrl("bolt://" + address)
                    .setTimeout(TIME_OUT)
                    .setConnectTimeout(TIME_OUT);
            serverHashMap.put(address,consumerConfig);
        }
    }

    @Override
    public void stop() {
        logger.debug("Stopping {}", RpcClient.class);
        for(ConsumerConfig<RaftConsensusService> consumerConfig: serverHashMap.values()){
            consumerConfig.unRefer();
        }
        logger.debug("Stopped {}", RpcClient.class);
    }

}
