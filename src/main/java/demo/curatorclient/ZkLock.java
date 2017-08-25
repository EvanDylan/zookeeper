package demo.curatorclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class ZkLock {

    public static void main(String[] args) {

        //multiPrintTime();
        multiSafePrintTime();
    }


    public static void printTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss SSS");
        System.out.println(dateFormat.format(new Date()));
    }

    public static void multiPrintTime() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                printTime();
            }).start();
        }
    }

    public static void multiSafePrintTime() {
        CuratorFramework client = ConnectUtil.connect();
        client.start();
        String path = "/lock";
        InterProcessLock lock = new InterProcessMutex(client, path);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    lock.acquire();
                    printTime();
                    lock.release();
                } catch (Exception e) {

                }
            }).start();
        }
    }
}
