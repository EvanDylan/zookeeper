package demo.originclient;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * zk中的watch功能.
 * zk中以下的读取相关的操作(getData(), getChildren(), exists())都可以选择性的设定watch.
 *
 * zk对watch的定义：当节点数据发生变化时，watch事件会被发送到设定watch的客户端，但是只能被触发一次.
 * 下面三点是定义watch的关键点:
 * 1) 只触发一次
 *      设定的watch只会被触发一次,如果需要多次触发需要重新设定watcher.
 * 2) 发送到客户端
 *      简言之： a). 通知是异步的，数据有可能都成功的修改，但是事件还没有通知到客户端.
 *              b). 有序性，在事件没有到达之前更新之后的数据客户端是看不到的.不同的客户端有可能因为网络延迟或者其他因素
 *              在不同时刻看到同一个事件，但是对于单个客户端来说事件始终是有序的.
 * 3) watch的内容
 *      这里涉及到两个不同的节点改变的方式.为了便于理解把zk想象成包含两个不同的watch列表: 节点数据变更事件、子节点变更事件.
 *   getData()和exists()属于节点数据变更事件,getChildren属于节点变更事件.create()会触发当前节点的数据变更事件和父节点子
 *   节点变更事件.delete()会触发当前节点数据变更事件和子节点变更事件、以及父节点的子节点变更事件.
 *
 *    watch在客户端所连接的服务端本地维护.watch的设置、维护、分发操作都很轻量级.当客户端连接到新的服务端，watch将被任一会话事件触发.
 * 与服务端断开连接时，不能获取watch事件.客户端重连后，之前注册的watch将被重新注册并在需要时触发.通常这一切透明地发生，用户不会察
 * 觉到.有一种情况watch可能丢失：之前对一个尚未建立的节点的设置了exists watch，如果断开期间该节点被建立或删除，那么此watch将丢失.
 *
 * link https://zookeeper.apache.org/doc/trunk/zookeeperProgrammers.html#ch_zkWatches
 * link http://zookeeper.majunwei.com/document/3.4.6/DeveloperProgrammerGuide.html
 *
 */
public class ZkWatcherDemo implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper client;

    public static void main(String[] args) throws Exception {

        client = new ZooKeeper("127.0.0.1:2181", 3000, new ZkWatcherDemo());
        countDownLatch.await();
        String path = "/zk";

        // 节点数据内容变更的watch -- 创建、删除、更新
        /*
            1. 判断节点是否存在,Stat不为空则存在,为null则为空
            2. watch 节点创建、删除、更新会触发默认的watcher,但是每个watcher只会触发一次.
               所以每次触发之后需要重新调用exists注册默认watcher.-- 默认watcher是创建连接
               的时候传入的.
         */
        Stat stat = client.exists(path, true);
        client.create(path, "zookeeper".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        client.setData(path, "zookeeper1".getBytes(), -1);
        client.delete(path, -1);
    }

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            if (Event.EventType.None == event.getType() && null == event.getPath()) {
                countDownLatch.countDown();
            }
            else {
                System.out.println(event.toString());
                try {
                    client.exists(event.getPath(), true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
