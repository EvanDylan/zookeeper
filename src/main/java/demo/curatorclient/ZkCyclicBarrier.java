package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;

public class ZkCyclicBarrier {

    public static void main(String[] args) throws Exception {

        CuratorFramework client = ConnectUtil.connect();
        client.start();
        String path = "/barrier";

        DistributedBarrier barrier = new DistributedBarrier(client, path);

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    System.out.println("begin .... ");
                    barrier.setBarrier();
                    barrier.waitOnBarrier();
                    System.out.println("end .... ");
                } catch (Exception e) {

                }
            }).start();
        }
        Thread.sleep(3000);
        barrier.removeBarrier();
    }

}
