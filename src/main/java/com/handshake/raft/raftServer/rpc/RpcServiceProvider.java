package com.handshake.raft.raftServer.rpc;

import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.handshake.raft.common.utils.IpUtil;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.service.Impl.RaftConsensusServiceImpl;
import com.handshake.raft.raftServer.service.LifeCycle;
import com.handshake.raft.raftServer.service.RaftConsensusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RpcServiceProvider implements LifeCycle {

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
        if(providerConfig != null){
            providerConfig.unExport();
        }
    }
}