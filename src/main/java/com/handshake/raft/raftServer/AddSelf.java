package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.raft.raftServer.proto.AddPeerParam;
import com.handshake.raft.raftServer.proto.AddPeerResult;
import com.handshake.raft.raftServer.proto.GetClusterInfoParam;
import com.handshake.raft.raftServer.proto.GetClusterInfoResult;
import com.handshake.raft.raftServer.rpc.RpcClient;
import com.handshake.raft.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class AddSelf implements LifeCycle{

    private static final Logger logger = LoggerFactory.getLogger(AddSelf.class);
    private static ScheduledFuture addSelfSchedule;

    @Autowired
    private RaftThreadPool raftThreadPool;

    @Autowired
    private Node node;

    @Override
    public void init() {
        logger.debug("Add itself");
        if (addSelfSchedule != null && !addSelfSchedule.isDone()) {
            addSelfSchedule.cancel(true);
        }
        if(node.getNodeConfig().isNewServer()){
            addSelfSchedule = raftThreadPool.getScheduledExecutorService().scheduleWithFixedDelay(
                    ()->{
                        addItself();
                        if(!node.getNodeConfig().isNewServer()){
                            stop();
                        }
                    },
                    0,
                    3000,
                    TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        if(addSelfSchedule != null && !addSelfSchedule.isDone()){
            addSelfSchedule.cancel(true);
        }
    }

    /**
     * try to add itself to cluster
     */
    public void addItself(){
        RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);
        NodeConfig nodeConfig = SpringContextUtil.getBean(NodeConfig.class);
        Node node = SpringContextUtil.getBean(Node.class);
        logger.info("Try to add itself {}", nodeConfig.getSelf());
        //param
        AddPeerParam addPeerParam = AddPeerParam.builder()
                .peerIp(nodeConfig.getSelf())
                .peerSpringAddress(nodeConfig.getSpringAddress(nodeConfig.getSelf()))
                .build();
        //try to connect to any server to get leader
        ArrayList<String> servers = nodeConfig.getOtherServers();
        String leaderId = null;
        for (String server: servers){
            RaftConsensusService raftConsensusService = rpcClient.connectToService(server);
            if(raftConsensusService != null){
                try {
                    AddPeerResult addPeerResult = raftConsensusService.addPeer(addPeerParam);
                    leaderId = addPeerResult.getLeaderId();
                    if(leaderId != null){
                        break;
                    }
                }
                catch (Exception e) {
                    logger.info("Fail to connect to {}", server);
                }
            }
        }
        //send the request to leader
        if(leaderId == null){
            return;
        }
        RaftConsensusService raftConsensusService = rpcClient.connectToService(leaderId);
        if(raftConsensusService != null){
            try {
                AddPeerResult addPeerResult = raftConsensusService.addPeer(addPeerParam);
                //already in the cluster, try to get the newest cluster
                if(addPeerResult.isSuccess()){
                    GetClusterInfoResult clusterInfo = raftConsensusService.getClusterInfo(new GetClusterInfoParam(nodeConfig.getSelf()));
                    if(clusterInfo.isSuccess()){
                        //update cluster configuration
                        nodeConfig.setServerInfo(clusterInfo.getServerInfo());
                        //set leader id
                        node.setLeaderId(clusterInfo.getLeaderId());
                        //create rpc client for these servers
                        for (String address: clusterInfo.getServerInfo().getServers()){
                            rpcClient.addServerConfig(address);
                        }
                        nodeConfig.setNewServer(false);
                    }
                }
            }
            catch (Exception e) {
                logger.info("Fail to connect to {}", leaderId);
            }
        }
    }
}
