/*
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.handshake.raft.rpc;


import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.RpcServer;
import com.handshake.raft.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DefaultRpcServiceImpl implements RpcService {

    private final RaftNode node;

    private final RpcServer rpcServer;

    public DefaultRpcServiceImpl(int port, RaftNode node) {
        rpcServer = new RpcServer(port, false, false);
        rpcServer.registerUserProcessor(new RaftUserProcessor<Request>() {

            @Override
            public Object handleRequest(BizContext bizCtx, Request request) {
                return handlerRequest(request);
            }
        });

        this.node = node;
    }


    @Override
    public Response<?> handlerRequest(Request request) {
        if (request.getCmd() == Request.R_VOTE) {
            return new Response<>(node.handlerRequestVote((RvoteParam) request.getObj()));
        } else if (request.getCmd() == Request.A_ENTRIES) {
            return new Response<>(node.handlerAppendEntries((AentryParam) request.getObj()));
        } else if (request.getCmd() == Request.CLIENT_REQ) {
            return new Response<>(node.handlerClientRequest((ClientKVReq) request.getObj()));
        } else if (request.getCmd() == Request.CHANGE_CONFIG_REMOVE) {
            return new Response<>(((ClusterMembershipChanges) node).removePeer((Peer) request.getObj()));
        } else if (request.getCmd() == Request.CHANGE_CONFIG_ADD) {
            return new Response<>(((ClusterMembershipChanges) node).addPeer((Peer) request.getObj()));
        }
        return null;
    }


    public void init() {
        rpcServer.startup();
    }

    public void destroy() {
        rpcServer.shutdown();
        log.info("destroy success");
    }
}