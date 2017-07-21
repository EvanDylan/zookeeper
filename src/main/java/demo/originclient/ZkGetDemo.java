package demo.originclient;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * zk获取数据.
 */
public class ZkGetDemo {

    public static void main(String[] args) throws Exception {

        final ZooKeeper client = ZkConnectUtil.getClient();

        String path = client.create("/zk", "zookeeper".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL);

        // 同步获取数据
        /**
         * path 节点路径
         * watch
         * stat 返回节点的状态信息
         */
        Stat stat = new Stat();
        byte[] data = client.getData(path, false, stat);
        System.out.println(new java.lang.String(data).toString());


        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // 异步获取数据
        client.getData(path, false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, java.lang.String path, Object ctx, byte[] data, Stat stat) {
                if (rc == 0) {
                    System.out.println(new java.lang.String(data).toString());
                }
                countDownLatch.countDown();
            }
        }, stat);

        countDownLatch.await();
    }

}
