package com.handshake.basic.controller;


import com.handshake.basic.common.response.ResponseResult;
import com.handshake.basic.raftServer.Node;
import com.handshake.basic.raftServer.service.Impl.RaftConsensusServiceImpl;
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
    public void shutdown(){
        System.exit(0);
    }

    /**
     * start election
     * @return
     */
    @PostMapping("/election")
    public ResponseResult<Object> election(){
        return ResponseResult.fail("No election in basic model");
    }

}
