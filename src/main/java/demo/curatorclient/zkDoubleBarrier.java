package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;

public class zkDoubleBarrier {

    public static void main(String[] args) throws Exception {

        CuratorFramework client = ConnectUtil.connect();
        client.start();
        String path = "/barrier";

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                DistributedDoubleBarrier doubleBarrier = new DistributedDoubleBarrier(client, path, 3);
                try {
                    doubleBarrier.enter();
                    System.out.println("enter .... ");
                    doubleBarrier.leave();
                    System.out.println("leave .... ");
                } catch (Exception e) {

                }
            }).start();
        }

        Thread.sleep(4000);

    }

}
