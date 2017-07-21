package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;

/**
 * 创建.
 */
public class ZkCreateDemo {

    public static void main(String[] args) {

        CuratorFramework client = ConnectUtil.connect();
        client.start();

    }

}
