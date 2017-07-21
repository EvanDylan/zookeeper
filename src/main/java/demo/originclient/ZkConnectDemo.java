package demo.originclient;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * 连接zk.
 */
public class ZkConnectDemo {

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        System.out.println("begin connect to zk server...");
        ZooKeeper client = new ZooKeeper("127.0.0.1:2181", 3000,
                new DefaultWatcher(countDownLatch));
        countDownLatch.await();
        System.out.println("session established...");
    }

}

class DefaultWatcher implements Watcher {

    private CountDownLatch countDownLatch;

    public DefaultWatcher(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            System.out.println("client already connected, session created...");
            countDownLatch.countDown();
        }
    }
}
