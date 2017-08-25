package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

public class ZkLeaderSelector {

    public static void main(String[] args) throws Exception {

        CuratorFramework client = ConnectUtil.connect();
        client.start();

        LeaderSelector leaderSelector = new LeaderSelector(client, "/lock", new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                System.out.println("complete master select, i'm has being selector");
                Thread.sleep(3000);
            }
        });
        // 当选举完成时会从新入队参与选举
        leaderSelector.autoRequeue();
        leaderSelector.start();
        Thread.sleep(Integer.MAX_VALUE);
    }

}
