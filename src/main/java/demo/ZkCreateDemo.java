package demo;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.util.SerializeUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 创建.
 */
public class ZkCreateDemo {

    public static void main(String[] args) throws Exception {

        ZooKeeper client = ZkConnectUtil.getClient();

        // 同步创建数据节点方法
        String path = "/data";
        Node node = new Node();
        node.add("name", "evan");
        client.create(path, SerializationUtils.serialize(node),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        byte[] data = client.getData(path, false, new Stat());
        Node nodeData = SerializationUtils.deserialize(data);
        nodeData.toString();

        // 异步创建数据节点方法
        Object ctx = new Object();
        client.create("/asynchronous-data", SerializationUtils.serialize(node),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, String name) {
                       if (rc == 0) {
                           System.out.println("asynchronous crate data success");
                       }
                    }
                },  ctx);
    }

    static class Node implements Serializable {

        Map<String, String> data = Maps.newHashMap();

        public void add(String k, String v) {
            data.put(k, v);
        }

        @Override
        public String toString() {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
            }
            return "";
        }
    }
}
