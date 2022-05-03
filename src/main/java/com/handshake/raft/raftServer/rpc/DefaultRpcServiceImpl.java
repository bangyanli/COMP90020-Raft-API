package com.handshake.raft.raftServer.rpc;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.RpcServer;
import com.handshake.raft.service.Impl.BookServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRpcServiceImpl implements RpcService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final RpcServer rpcServer;
    private final RaftNode node;

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