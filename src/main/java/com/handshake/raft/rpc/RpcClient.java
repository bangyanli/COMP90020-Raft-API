package com.handshake.raft.rpc;
import com.alipay.remoting.exception.RemotingException;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcClient {
    private final static com.alipay.remoting.rpc.RpcClient CLIENT = new com.alipay.remoting.rpc.RpcClient();
    public <R> R send(Request request) {
        return send(request, (int) TimeUnit.SECONDS.toMillis(5));
    }
    public <R> R send(Request request, int timeout) {
        Response<R> result;
        try {
            result = (Response<R>) CLIENT.invokeSync(request.getUrl(), request, timeout);
            return result.getResult();
        } catch (RemotingException e) {
            throw new RaftRemotingException("rpc RaftRemotingException ", e);
        } catch (InterruptedException e) {
            // ignore
        }
        return null;
    }
    public void init() {
        CLIENT.startup();
    }
    public void destroy() {
        CLIENT.shutdown();
        log.info("destroy success");
    }
}
