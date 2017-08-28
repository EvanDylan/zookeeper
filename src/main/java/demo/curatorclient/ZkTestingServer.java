package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.io.File;

public class ZkTestingServer {

    public static void main(String[] args) throws Exception {

        /**
         * 小心路径，停止的时候会删除该路径下的文件
         */
        TestingServer server = new TestingServer(2185, new File("/Users/evan/tmp"));
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(server.getConnectString())
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        System.out.println(client.getChildren().forPath("/zookeeper"));
        server.close();
    }

}
