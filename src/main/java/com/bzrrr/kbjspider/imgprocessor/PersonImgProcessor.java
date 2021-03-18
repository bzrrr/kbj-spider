package com.bzrrr.kbjspider.imgprocessor;

import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * @Author: wangziheng
 * @Date: 2021/3/17
 */
public class PersonImgProcessor {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String pathname = "D:\\pic\\instagram_ruda_s2";
        File dir = new File(pathname);
        File tarDir = new File(pathname + "\\nature");
        if (!tarDir.exists()) {
            tarDir.mkdirs();
        }
        File[] imgs = dir.listFiles();
        PersonCheckTask task = new PersonCheckTask(imgs, tarDir);
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1,
                new MyWorkerThreadFactory(), null, true);
        Integer total = pool.invoke(task);
        System.out.println(total);
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时: " + (endTime - startTime) / 1000d + "s");
        pool.shutdown();
    }

    static class MyWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new MyWorkerThread(pool);

        }
    }

    static class MyWorkerThread extends ForkJoinWorkerThread {

        protected MyWorkerThread(ForkJoinPool pool) {
            super(pool);
        }
    }
}
