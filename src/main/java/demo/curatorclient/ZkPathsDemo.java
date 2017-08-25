package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;

public class ZkPathsDemo {

    public static void main(String[] args) {
        /*CuratorFramework client = ConnectUtil.connect();
        client.start();*/
        String path = "/path";
        System.out.println(ZKPaths.fixForNamespace("namespace", path)); // /namespace/path
        System.out.println(ZKPaths.makePath(path, "sub")); // /path/sub
        System.out.println(ZKPaths.getNodeFromPath("/curator_zkpath_sample/sub1")); // sub1

        ZKPaths.PathAndNode pn = ZKPaths.getPathAndNode( "/curator_zkpath_sample/sub1" );
        System.out.println(pn.getPath()); // curator_zkpath_sample
        System.out.println(pn.getNode()); // sub1
    }
}
