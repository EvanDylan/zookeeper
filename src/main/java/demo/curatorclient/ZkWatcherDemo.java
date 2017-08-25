package demo.curatorclient;

import book.chapter05.$5_4_2.PathChildrenCache_Sample;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 监听器.
 */
public class ZkWatcherDemo {

    public static void main(String[] args)  throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        CuratorFramework   curatorFramework = ConnectUtil.connect();

        // 增加连接事件监听
        curatorFramework.getConnectionStateListenable().addListener(((client, newState) -> {

            if (newState.isConnected()) {
                System.out.println("connect to zk server successful");
                countDownLatch.countDown();
            }

        }));

        // 这里的监听事件不会被反复触发
        curatorFramework.getCuratorListenable().addListener(new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println(event);
            }
        });

        PathChildrenCache cache = new PathChildrenCache(curatorFramework, "/", false);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType())  {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED");
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED");
                        break;
                    case INITIALIZED:
                        System.out.println("INITIALIZED");
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED");
                        break;
                }
            }
        });
        cache.start(PathChildrenCache.StartMode.NORMAL);


        curatorFramework.start();
        countDownLatch.await();

        curatorFramework.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/lock");
        Thread.sleep(1000);
        curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath("/lock");
        Thread.sleep(1000);
        curatorFramework.setData().forPath("/lock");
        Thread.sleep(1000);
        curatorFramework.delete().forPath("/lock");
    }
}