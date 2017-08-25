package demo.curatorclient;

import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingZooKeeperServer;
import org.apache.zookeeper.server.quorum.QuorumPeer;

import java.util.List;

public class ZkTestingCluster {

    public static void main(String[] args) throws Exception {

        TestingCluster clusterServer = new TestingCluster(3);
        clusterServer.start();
        Thread.sleep(2000);

        List<TestingZooKeeperServer> servers = clusterServer.getServers();

        TestingZooKeeperServer leader = null;
        for (TestingZooKeeperServer server : servers) {
            QuorumPeer quorumPeer = server.getQuorumPeer();
            System.out.println(server.getInstanceSpec().getServerId() + "-" + server.getQuorumPeer().getServerState() + "-" + server.getInstanceSpec().getDataDirectory());
            if (quorumPeer.getServerState().toString().equals("leading")) {
                leader = server;
            }
        }
        leader.kill();

        for (TestingZooKeeperServer server : servers) {
            QuorumPeer quorumPeer = server.getQuorumPeer();
            System.out.println(server.getInstanceSpec().getServerId() + "-" + server.getQuorumPeer().getServerState() + "-" + server.getInstanceSpec().getDataDirectory());
        }
    }

}
