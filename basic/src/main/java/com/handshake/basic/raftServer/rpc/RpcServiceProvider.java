package com.handshake.basic.raftServer.rpc;

import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.handshake.basic.common.utils.IpUtil;
import com.handshake.basic.config.NodeConfig;
import com.handshake.basic.raftServer.LifeCycle;
import com.handshake.basic.raftServer.service.Impl.RaftConsensusServiceImpl;
import com.handshake.basic.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RpcServiceProvider implements LifeCycle {

    private static final Logger logger = LoggerFactory.getLogger(RpcServiceProvider.class);

    @Autowired
    private NodeConfig nodeConfig;
    ProviderConfig<RaftConsensusService> providerConfig = null;

    public void init() {
        ServerConfig serverConfig = new ServerConfig()
                .setProtocol("bolt")
                .setPort(IpUtil.getPort(nodeConfig.getSelf()))
                .setDaemon(false);

        providerConfig = new ProviderConfig<RaftConsensusService>()
                .setInterfaceId(RaftConsensusService.class.getName())
                .setRef(new RaftConsensusServiceImpl())
                .setServer(serverConfig);

        providerConfig.export();
    }

    @Override
    public void stop() {
        logger.debug("Stopping {}", RpcServiceProvider.class);
        if(providerConfig != null){
            providerConfig.unExport();
        }
        logger.debug("Stopped {}", RpcServiceProvider.class);
    }
}
