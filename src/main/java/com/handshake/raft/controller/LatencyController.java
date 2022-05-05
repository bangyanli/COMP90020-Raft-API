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
@RequestMapping("/latency")
public class LatencyController {

    private static boolean online = true;

    @Autowired
    Node node;

    @PostMapping
    public ResponseResult<Object> postBook(@RequestParam("ip") String ip,
                                           @RequestParam("latency") int latency){
        RaftConsensusServiceImpl.latencyMap.put(ip,latency);
        return ResponseResult.suc("successfully set latency!");
    }

    /**
     * remove it from cluster
     * @return
     */
    @PostMapping("/shutdown")
    public ResponseResult<Object> preShutdown(){
        if(node.getNodeStatus() == Status.LEADER){
            return ResponseResult.fail("Node " + node.getNodeConfig().getSelf() + " is leader!");
        } else if(online){
            node.removeItself();
            online = false;
            System.exit(0);
            return ResponseResult.suc("remove Node " + node.getNodeConfig().getSelf() + " from cluster!");
        }
        else {
            return ResponseResult.fail("Node " + node.getNodeConfig().getSelf() + " already offline!");
        }
    }

}
