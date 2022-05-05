package com.handshake.raft.raftServer.proto.Impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.Node;
import com.handshake.raft.raftServer.Status;
import com.handshake.raft.raftServer.proto.Command;
import com.handshake.raft.raftServer.proto.ServerInfo;
import com.handshake.raft.raftServer.rpc.RpcClient;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class ChangeConfigurationCommand implements Command {

    private ArrayList<String> servers;
    private ArrayList<String> serversSpringAddress;
    private Boolean executed = false;

    @JsonCreator
    public ChangeConfigurationCommand(@JsonProperty("servers")ArrayList<String> servers,
                                      @JsonProperty("serversSpringAddress")ArrayList<String> serversSpringAddress,
                                      @JsonProperty("executed")Boolean executed) {
        this.servers = servers;
        this.serversSpringAddress = serversSpringAddress;
        this.executed = executed;
    }

    @Override
    public void execute() {
        NodeConfig nodeConfig = SpringContextUtil.getBean(NodeConfig.class);
        if(!executed){
            //update server config for rpc
            RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);
            Node node = SpringContextUtil.getBean(Node.class);
            ArrayList<String> difference = new ArrayList<>();
            if(servers.size() > nodeConfig.getServers().size()){
                difference.addAll(servers);
                difference.removeAll(nodeConfig.getServers());
                for (String address: difference){
                    rpcClient.addServerConfig(address);
                    //add nextIndex and matchIndex for new server if it is leader
                    if(node.getNodeStatus() == Status.LEADER){
                        node.getNextIndex().put(address,node.getLog().getLastIndex());
                        node.getMatchIndex().put(address,0);
                    }
                }
            }
            //set node configuration
            nodeConfig.setOldConfiguration(new ServerInfo(nodeConfig.getServers(),nodeConfig.getServersSpringAddress()));
            nodeConfig.setServerInfo(new ServerInfo(servers,serversSpringAddress));
            //maybe interrupted here and execute multiple times
            executed = true;
        }
        //second time: this command is committed
        else {
            nodeConfig.setOldConfiguration(null);
            nodeConfig.setNewServer(false);
        }
    }
}
