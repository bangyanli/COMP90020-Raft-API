package com.handshake.raft;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
@Slf4j
public class RaftNodesStart {
    public static void main(String[] args) throws Throwable {
        boot();
    }

    public static void boot() throws Throwable {
        String[] peerAddr = {"localhost:8775", "localhost:8776", "localhost:8777", "localhost:8778", "localhost:8779"};

        NodeConfig config = new NodeConfig();

        // 自身节点
        config.setSelfPort(Integer.parseInt(System.getProperty("serverPort", "8779")));

        // 其他节点地址
        config.setPeerAddrs(Arrays.asList(peerAddr));
        config.setStateMachineSaveType(StateMachineSaveType.ROCKS_DB);

        Node node = RaftNode.getInstance();
        node.setConfig(config);

        node.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronized (node) {
                node.notifyAll();
            }
        }));

        log.info("gracefully wait");

        synchronized (node) {
            node.wait();
        }

        log.info("gracefully stop");
        node.destroy();
    }
}
