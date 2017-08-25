package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.atomic.PromotedToLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.TimeUnit;

public class ZkDistAtomicInt {

    public static void main(String[] args) throws Exception {
        CuratorFramework client = ConnectUtil.connect();
        client.start();
        String path = "/lock";
        // 默认使用乐观锁
        DistributedAtomicInteger distributedAtomicInteger =
                new DistributedAtomicInteger(client, path, new RetryNTimes(3, 100));
        AtomicValue<Integer> value = distributedAtomicInteger.decrement();
        System.out.println(value.succeeded());

        // 使用互斥锁
        // 1.构建互斥锁
        PromotedToLock.Builder builder = PromotedToLock.builder();
        builder.lockPath(path).timeout(1000, TimeUnit.MILLISECONDS).retryPolicy(new RetryNTimes(3, 1000));
        // Creates in mutex promotion mode. The optimistic lock will be tried first using
        // * the given retry policy. If the increment does not succeed, a {@link InterProcessMutex} will be tried * with its own retry policy
        DistributedAtomicInteger mutexLockAtomic = new DistributedAtomicInteger(client, path, new RetryNTimes(3, 1000), builder.build());
        AtomicValue<Integer> mutexLockValue = mutexLockAtomic.decrement();
        System.out.println(mutexLockValue.succeeded());
    }

}
