package org.rpc.util;


import java.util.Queue;
import java.util.concurrent.*;

/**
 * provide a thread pool executor to deal with the remote calling method
 *
 * */
public class MyThreadPoolExecutor {

    private static ThreadPoolExecutor threadPoolExecutor;

    public static void init(int coreThreadSize, int maxThreadSize, ThreadFactory threadFactory, int keepAliveTime, BlockingQueue<Runnable> queue, TimeUnit timeUnit) {
        threadPoolExecutor = new ThreadPoolExecutor(coreThreadSize,maxThreadSize,keepAliveTime,timeUnit,queue,threadFactory, new ThreadPoolExecutor.AbortPolicy());
        System.out.println("ThreadPoolExecutor is started");
    }

    public static void init(int coreThreadSize,ThreadFactory threadFactory,BlockingQueue<Runnable> queue){
        threadPoolExecutor = new ThreadPoolExecutor(coreThreadSize,coreThreadSize,60,TimeUnit.SECONDS,queue, new ThreadPoolExecutor.AbortPolicy());
        System.out.println("ThreadPoolExecutor is started");
    }

    public static int handler(Runnable runnable){

        try {
            threadPoolExecutor.execute(runnable);
        }catch (RejectedExecutionException e){
            System.out.println("the blockingqueue is full");
            return -1;
        }
        return 1;
    }
}
