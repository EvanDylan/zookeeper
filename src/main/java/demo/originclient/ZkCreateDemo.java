package demo.originclient;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.Serializable;
import java.util.Map;

/**
 * 创建.
 */
public class ZkCreateDemo {

    public static void main(String[] args) throws Exception {

        ZooKeeper client = ZkConnectUtil.getClient();

        // 同步创建临时数据节点方法
        String path = "/data";
        Node node = new Node();
        node.add("name", "evan");
        /**
         * CreateMode:
         *  PERSISTENT 永久节点
         *  PERSISTENT_SEQUENTIAL 带序列号的永久节点,创建的时候会自动的在节点名称后加入序列号
         *  EPHEMERAL 临时节点,会话期间有效
         *  EPHEMERAL_SEQUENTIAL 有序列号的临时节点,会话期间有效
         */
        client.create(path, SerializationUtils.serialize(node),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        byte[] data = client.getData(path, false, new Stat());
        Node nodeData = SerializationUtils.deserialize(data);
        nodeData.toString();

        // 异步创建临时数据节点方法
        Object ctx = new Object();
        client.create("/asynchronous-data", SerializationUtils.serialize(node),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {

                    /* rc 服务端响应码 0 成功, -4 连接已经断开, -110 节点已经存在, -112 会话已过期
                       path 路径
                       ctx 调用时接口传入的ctx
                       name 返回节点的完整路径-如果创建节点的模式是PERSISTENT_SEQUENTIAL、EPHEMERAL_SEQUENTIAL,
                            那么节点如果没有创建完成是无法实现知道节点的完整的路径的。*/
                    @Override
                    public void processResult(int rc, String path, Object ctx, String name) {
                        if (rc == 0) {
                            System.out.println("asynchronous crate data success");
                        }
                    }
                }, ctx);
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
