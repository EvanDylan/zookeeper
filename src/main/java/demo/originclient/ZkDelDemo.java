package demo.originclient;

import org.apache.zookeeper.*;

/**
 * zk 删除节点.
 */
public class ZkDelDemo {

    public static void main(String[] args) throws Exception {

        ZooKeeper client = ZkConnectUtil.getClient();
        /*client.create("/zk", "zookeeper".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL, new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, String name) {
                        if (rc == 0) {

                        }
                        countDownLatch.countDown();
                    }
                }, new Object());*/
        String path = client.create("/zk", "zookeeper".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL);
        // 如果version是-1,则匹配所有的版本
        client.delete(path, -1);
    }
}

