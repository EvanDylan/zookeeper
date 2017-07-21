package demo.originclient;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

/**
 * zk权限控制.
 * ACL: access control list 访问控制列表
 *
 * <p>
 * zk中的权限有:
 * CREATE、DELETE 能创建和删除子节点
 * READ、WRITE、
 * ADMIN 可以设定权限
 * <p>
 *
 * 内置的权限控制模式
 * world: 它下面只有一个id, 叫anyone, world:anyone代表任何人，zookeeper中对所有人有权限的结点就是属于world:anyone的
 * auth: 它不需要id, 只要是通过authentication的user都有权限（zookeeper支持通过kerberos来进行authencation,
 * 也支持username/password形式的authentication)
 * digest: 它对应的id为username:BASE64(SHA1(password))，它需要先通过username:password形式的authentication
 * ip: 它对应的id为客户机的IP地址，设置的时候可以设置一个ip段，比如ip:192.168.1.0/16, 表示匹配前16个bit的IP段
 * super: 在这种scheme情况下，对应的id拥有超级权限，可以做任何事情(cdrwa)
 * x509: 轻量级目录访问协议
 *
 * 局限性：
 * （1）ACL并无递归机制，任何一个znode创建后，都需要单独设置ACL，无法继承父节点的ACL设置。
 * （2）除了ip这种scheme，digest和auth的使用对用户都不是透明的，这也给使用带来了很大的成本，很多依赖zookeeper的开源框架也没有加入对ACL的支持，例如hbase，storm
 *
 *
 *
 */
public class ZkACLDemo {

    public static void main(String[] args) throws Exception {

        ZooKeeper client = ZkConnectUtil.getClient();
        client.addAuthInfo("digest", DigestAuthenticationProvider.generateDigest("admin:admin").getBytes());
        client.create("/zk", "as long as you love me".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

        /**
         *  由于当前连接没有认证信息,所以抛出一下的错误
         *  org.apache.zookeeper.KeeperException$NoAuthException: KeeperErrorCode = NoAuth for /zk
         */
        ZooKeeper client1 = ZkConnectUtil.getClient();
        byte[] data = client1.getData("/zk", false, new Stat());
        System.out.println(new String(data).toString());
    }

}
