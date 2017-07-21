package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * util.
 */
public class ConnectUtil {

    public static CuratorFramework connect() {
        return CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(1000)
                .connectionTimeoutMs(1000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
    }

}
