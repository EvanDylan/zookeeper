package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * 创建.
 */
public class ZkCURDDemo {

    public static void main(String[] args) throws Exception{

        CuratorFramework client = ConnectUtil.connect();
        client.start();

        client.create().withMode(CreateMode.PERSISTENT).forPath("/zk");

        client.setData().forPath("/zk", "liushi".getBytes());

        byte[] data = client.getData().forPath("/zk");
        System.out.println(new String(data));

        client.delete().guaranteed().forPath("/zk");
    }

}
