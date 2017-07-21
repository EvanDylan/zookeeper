package demo.zkoperation;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * zk 连接工具.
 */
public class ZkConnectUtil {

    public static ZooKeeper getClient() throws IOException {
        return new ZooKeeper("127.0.0.1:2181", 3000,
                new DefaultWatcher());
    }


    private static class DefaultWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            System.out.println(event.toString());
        }
    }

}
