package com.handshake.raft.controller;

import com.handshake.raft.common.response.ResponseResult;
import com.handshake.raft.raftServer.Node;
import com.handshake.raft.raftServer.Status;
import com.handshake.raft.raftServer.service.Impl.RaftConsensusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  latency controller
 * </p>
 *
 * @author Lingxiao
 * @since 2022-05-04
 */
@CrossOrigin
@RestController
@RequestMapping("/raftControl")
public class RaftController {

    @Autowired
    Node node;


    @PostMapping("/latency")
    public ResponseResult<Object> setLatency(@RequestParam("ip") String ip,
                                           @RequestParam("latency") int latency){
        RaftConsensusServiceImpl.latencyMap.put(ip,latency);
        return ResponseResult.suc("successfully set latency!");
    }

    /**
     * try to remove itself from cluster
     * @return
     */
    @PostMapping("/shutdown")
    public ResponseResult<Object> shutdown(){
        if(node.getNodeStatus() == Status.LEADER){
            return ResponseResult.fail("Node " + node.getNodeConfig().getSelf() + " is leader!");
        } else {
            node.tryToShutDown();
            return ResponseResult.suc("Try to remove Node " + node.getNodeConfig().getSelf() + " from cluster!");
        }
    }

    /**
     * start election
     * @return
     */
    @PostMapping("/election")
    public ResponseResult<Object> election(){
        if(node.getNodeStatus() == Status.LEADER){
            return ResponseResult.fail("Node " + node.getNodeConfig().getSelf() + " is leader!");
        } else {
            node.setNodeStatus(Status.CANDIDATE);
            return ResponseResult.suc("Node " + node.getNodeConfig().getSelf() + " start election!");
        }
    }

}
