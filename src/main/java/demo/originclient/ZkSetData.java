package demo.originclient;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 更新节点信息.
 */
public class ZkSetData {

    public static void main(String[] args) throws Exception {

        ZooKeeper client = ZkConnectUtil.getClient();
        String path = client.create("/zk", "zookeeper".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL);

        client.setData(path, "zookeeper1".getBytes(), -1);

        byte[] data = client.getData(path, false, new Stat());
        System.out.println(new String(data).toString());
    }

}
