package com.bzrrr.kbjspider.imgprocessor;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

import static java.lang.System.getProperty;


/**
 * @Author: wangziheng
 * @Date: 2021/3/18
 */
public class PersonCheckTask2 extends RecursiveTask<Integer> {
    static final int THRESHOLD = 50;
    File[] array;
    File tarDir;

    public PersonCheckTask2(File[] array, File tarDir) {
        this.array = array;
        this.tarDir = tarDir;
    }

    @SneakyThrows
    @Override
    protected Integer compute() {
        if (this.array.length <= THRESHOLD) {
            int count = 0;
            for (File img : this.array) {
                if (img.getName().endsWith("mp4")) {
                    continue;
                }
                boolean hasFace = personCheck();
                if (hasFace) {
                    count++;
                } else {
                    System.out.println(img.getName());
//                    img.renameTo(new File(tarDir.getAbsolutePath() + "\\" + img.getName()));
                    Files.copy(img.toPath(), new File(tarDir.getAbsolutePath() + "\\" + img.getName()).toPath());
                }
            }
            return count;
        }
        int middle = this.array.length / 2;
        File[] arr1 = Arrays.copyOfRange(this.array, 0, middle);
        File[] arr2 = Arrays.copyOfRange(this.array, middle, this.array.length);
        System.out.println("task-length -> " + arr1.length + " -- " + arr2.length);
        PersonCheckTask2 task1 = new PersonCheckTask2(arr1, tarDir);
        PersonCheckTask2 task2 = new PersonCheckTask2(arr2, tarDir);
        task1.fork();
        task2.fork();
        return task1.join() + task2.join();
    }

    public boolean personCheck() {
//        imgPath = "D:\\pic\\instagram_ruda_s2\\2016-10-11_08-20-45_BLamyUpDTqdGBXD7nXae7aoXRGCOh6l5ZcKnBs0_14701353_1036956629746344_521975540215709696_n.jpg";
        long startTime = System.currentTimeMillis();


        printCost(startTime);
        return false;
    }

    private void printCost(long startTime) {
        long endTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + "  耗时: " + (endTime - startTime) / 1000d + "s");
    }
}
