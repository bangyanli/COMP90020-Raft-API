package com.handshake.raft.controller;

import com.handshake.raft.common.response.ResponseResult;
import com.handshake.raft.raftServer.service.Impl.RaftConsensusServiceImpl;
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

    @PostMapping
    public ResponseResult<Object> postBook(@RequestParam("ip") String ip,
                                           @RequestParam("latency") int latency){
        RaftConsensusServiceImpl.latencyMap.put(ip,latency);
        return ResponseResult.suc("successfully set latency!");
    }

}
