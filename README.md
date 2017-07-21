# Zookeeper

## 简介
### 1.概述 
    Zookeeper是一个开放源代码的分布式协调服务，工业级分布式数据一致性的解决方案。
### 2.设计目标
    1). 简单的 这里简单是指数据的存储模型。zk使得分布式程序能够通过一个共享的、树形结构的命名空间来进行相互协调。命名空间和标准
    的文件系统比较相似，由一系列被称为ZNode的数据节点组成。总体来说，其数据模型类型一个文件系统，而ZNode之间的层级关系，就像文
    件系统的目录结构一样。不过它又和传统的磁盘文件系统有不同之处，zk将数据存储在内存中，所以zk可以达到很高的突出量和较低的延迟。
       
    2). 复制的 像分布式程序一样不同zk Server之间也会相互协调，zk致力于一组机器之间是可复制的，其实也就是集群啦。
    组成zk集群的每台机器都会在持久化存储器中维护内存中服务器状态的快照以及事务快照。只要服务器有一半以上时可用的，zk就可以正常的
    对外提供服务。zk的客户端会选择zk集群中的一台机器通过tcp创建连接，通过连接发送请求、接受返回、获取watch事件、发送心跳。假如
    连接意外断开了，客户端会自动连接到其他不同的服务器。
     
    3). 有序的 zk通过一个递增的编号来标记每个更新操作，这个编号反映了所有事务的操作顺序。可以通过zookeeper的这个特征来实现更高
    层次抽象，比如同步原语。
     
    4). 高性能的 特别是在以读多场景下，在读写比例为10:1时最快。

官网给出的性能测试图中(3台机器、910个连接、10:1的读写比), QPS(这里说成QPS不是很恰当)在8万左右，如下所示
![](https://zookeeper.apache.org/doc/r3.4.10/images/zkperfRW-3.2.jpg)
    
    

## 安装部署

### 1. 安装启动
    
#### 1). 下载并解压 
    下载地址 http://zookeeper.apache.org/releases.html#download
    解压缩 tar -zxvf zookeeper-3.4.10.tar.gz
    
#### 2). 修改环境
    vim /etc/profile
    export PATH=/usr/local/zookeeper-3.4.10/bin:$PATH;
#### 3). 修改zookeeper配置文件
        
        a). 拷贝份新的配置文件
        cp zoo_sample.cfg zoo.cfg
        
        b). 修改配置文件内容如下
        # The number of milliseconds of each tick
        tickTime=2000
        # The number of ticks that the initial 
        # synchronization phase can take
        initLimit=10
        # The number of ticks that can pass between 
        # sending a request and getting an acknowledgement
        syncLimit=5
        # the directory where the snapshot is stored.
        # do not use /tmp for storage, /tmp here is just 
        # example sakes.zookeeper workspace
        dataDir=/tmp/zookeeper 
        # the port at which the clients will connect
        clientPort=2181
        # the maximum number of client connections.
        # increase this if you need to handle more clients
        #maxClientCnxns=60
        #
        # Be sure to read the maintenance section of the 
        # administrator guide before turning on autopurge.
        #
        # http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
        #
        # The number of snapshots to retain in dataDir
        #autopurge.snapRetainCount=3
        # Purge task interval in hours
        # Set to "0" to disable auto purge feature
        #autopurge.purgeInterval=1
        server.1=127.0.0.1:2888:3888
        
        c). 在zookeeper工作目录下创建myid文件,文件第一行写入机器编号。
        如：server.1=127.0.0.1:2888:3888 则机器编号为1
        
#### 5). 启动机器
    
        zkServer.sh start
    
#### 6). 验证服务器状态
    
        zkServer.sh status
        
### 2.	zookeeper运行模式
  
  1). 单机模式。
  
  2). 伪集群模式，单机模拟集群的情况，一个机器启动多个zk实例，注意端口不要冲突。
  
  3). 集群模式，多台机器组成集群。

## 客户端

### 1. 自带命令行工具
1). 连接zk 
    
    $ bin/zkCli.sh -server 127.0.0.1:2181
    
2). 连接成功后可以输入help查看有哪些命令可以使用，如下：
	
	[zk: localhost:2181(CONNECTED) 11] help
	ZooKeeper -server host:port cmd args
	stat path [watch]
	set path data [version]
	ls path [watch]
	delquota [-n|-b] path
	ls2 path [watch]
	setAcl path acl
	setquota -n|-b val path
	history 
	redo cmdno
	printwatches on|off
	delete path [version]
	sync path
	listquota path
	rmr path
	get path [watch]
	create [-s] [-e] path data acl
	addauth scheme auth
	quit 
	getAcl path
	close 
	connect host:port
### 2. 第三方客户端工具
#### 1). 有哪些
	
	a). 原生的客户端工具，maven地址:https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper
	b). Netflix开源的curator，maven地址:https://mvnrepository.com/artifact/org.apache.curator/curator-framework

#### 2). 使用zookeeper原生的客户端工具
	
a). 创建连接

```
public class ZkConnectDemo {

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        System.out.println("begin connect to zk server...");
        // 创建客户端，超时时间3s.
        ZooKeeper client = new ZooKeeper("127.0.0.1:2181", 3000,
                new DefaultWatcher(countDownLatch));
        countDownLatch.await();
        System.out.println("session established...");
    }
}

class DefaultWatcher implements Watcher {

    private CountDownLatch countDownLatch;

    public DefaultWatcher(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            System.out.println("client already connected, session created...");
            countDownLatch.countDown();
        }
    }
}
```
      