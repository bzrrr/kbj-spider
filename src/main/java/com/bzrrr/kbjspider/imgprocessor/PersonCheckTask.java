package com.bzrrr.kbjspider.imgprocessor;

import lombok.SneakyThrows;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.RecursiveTask;


/**
 * @Author: wangziheng
 * @Date: 2021/3/18
 */
public class PersonCheckTask extends RecursiveTask<Integer> {
    static final int THRESHOLD = 50;
    File[] array;
    File tarDir;

    public PersonCheckTask(File[] array, File tarDir) {
        this.array = array;
        this.tarDir = tarDir;
    }

    @SneakyThrows
    @Override
    protected Integer compute() {
        if (this.array.length <= THRESHOLD) {
            int count = 0;
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            CascadeClassifier faceAltDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
            CascadeClassifier faceDefaultDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml");
            CascadeClassifier eyeDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_eye.xml");
            CascadeClassifier lefteyeDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_lefteye_2splits.xml");
            CascadeClassifier righteyeDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_righteye_2splits.xml");
            CascadeClassifier glasseyeDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_eye_tree_eyeglasses.xml");
            CascadeClassifier fullbodyDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_fullbody.xml");
            CascadeClassifier lowerbodyDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_lowerbody.xml");
            CascadeClassifier upperbodyDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_upperbody.xml");
            CascadeClassifier profileDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_profileface.xml");
            for (File img : this.array) {
                if (img.getName().endsWith("mp4") || img.isDirectory()) {
                    continue;
                }
                boolean hasFace = personCheck(img.getAbsolutePath(), faceDefaultDetector,
                        faceAltDetector, fullbodyDetector, lowerbodyDetector, upperbodyDetector,
                        eyeDetector, lefteyeDetector, righteyeDetector, glasseyeDetector,
                        profileDetector);
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
        PersonCheckTask task1 = new PersonCheckTask(arr1, tarDir);
        PersonCheckTask task2 = new PersonCheckTask(arr2, tarDir);
        task1.fork();
        task2.fork();
        return task1.join() + task2.join();
    }

    public CascadeClassifier buildDetector(String xmlPath) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        return new CascadeClassifier(xmlPath);
    }

    public boolean personCheck(String imgPath,
                               CascadeClassifier faceDefaultDetector,
                               CascadeClassifier faceAltDetector,
                               CascadeClassifier fullbodyDetector,
                               CascadeClassifier lowerbodyDetector,
                               CascadeClassifier upperbodyDetector,
                               CascadeClassifier eyeDetector,
                               CascadeClassifier lefteyeDetector,
                               CascadeClassifier righteyeDetector,
                               CascadeClassifier glasseyeDetector,
                               CascadeClassifier profileDetector) {
//        imgPath = "D:\\pic\\instagram_ruda_s2\\2016-10-11_08-20-45_BLamyUpDTqdGBXD7nXae7aoXRGCOh6l5ZcKnBs0_14701353_1036956629746344_521975540215709696_n.jpg";
//        imgPath = "D:\\pic\\instagram_ruda_s2\\2016-03-18_04-05-03_BDFI-V6nhzo4BYkG34coEsuvh2deMwO7U4oouQ0_10354301_1284812041535346_1846862774_n.jpg";
        long startTime = System.currentTimeMillis();
        Mat image = Imgcodecs.imread(imgPath);
        if (image.empty()) {
            return false;
        }

        // 3 特征匹配
        MatOfRect faceAlt = new MatOfRect();
        faceAltDetector.detectMultiScale(image, faceAlt, 1.1, 0, 0);
        Rect[] faceAltRects = faceAlt.toArray();
        for (Rect rect : faceAltRects) {
//            Mat faceROI0 = new Mat(image, rect);
//            MatOfRect eyesDetections0 = new MatOfRect();
//            eyeDetector.detectMultiScale(faceROI0, eyesDetections0, 1.1, 1);
            Mat faceROI = new Mat(image, rect);
            MatOfRect eyesDetections = new MatOfRect();
            lefteyeDetector.detectMultiScale(faceROI, eyesDetections, 1.2, 0);
            if (/*eyesDetections0.toArray().length > 0 || */eyesDetections.toArray().length > 0) {
                return true;
            }
        }

//        Mat image2 = Imgcodecs.imread(imgPath);
//        MatOfRect faceDefault = new MatOfRect();
//        faceDefaultDetector.detectMultiScale(image2, faceDefault, 1.1, 1, 0,
//                new Size(20d, 20d));
//        Rect[] faceDefaultRects = faceDefault.toArray();
//        if (faceDefaultRects.length > 0) {
//            printCost(startTime);
//            return true;
//        }

        Mat image3 = Imgcodecs.imread(imgPath);
        MatOfRect fullbody = new MatOfRect();
        fullbodyDetector.detectMultiScale(image3, fullbody, 1.1, 1, 0);
        Rect[] fullbodyRects = fullbody.toArray();
        if (fullbodyRects.length > 0) {
            printCost(startTime);
            return true;
        }

//        Mat image4 = Imgcodecs.imread(imgPath);
//        MatOfRect lowerbody = new MatOfRect();
//        lowerbodyDetector.detectMultiScale(image4, lowerbody, 1.1, 2, 0,
//                new Size(20d, 20d));
//        Rect[] lowerbodyRects = lowerbody.toArray();
//        if (lowerbodyRects.length > 0) {
//            printCost(startTime);
//            return true;
//        }
//
//        Mat image5 = Imgcodecs.imread(imgPath);
//        MatOfRect upperbody = new MatOfRect();
//        upperbodyDetector.detectMultiScale(image5, upperbody, 1.1, 2, 0,
//                new Size(20d, 20d));
//        Rect[] upperbodyRects = upperbody.toArray();
//        if (upperbodyRects.length > 0) {
//            printCost(startTime);
//            return true;
//        }


//
//        Mat image7 = Imgcodecs.imread(imgPath);
//        MatOfRect righteye = new MatOfRect();
//        righteyeDetector.detectMultiScale(image7, righteye, 1.1, 1, CASCADE_SCALE_IMAGE);
//        Rect[] righteyeRects = lefteye.toArray();
//        if (righteyeRects.length > 0) {
//            printCost(startTime);
//            return true;
//        }

        Mat image8 = Imgcodecs.imread(imgPath);
        MatOfRect profile = new MatOfRect();
        profileDetector.detectMultiScale(image8, profile, 1.1, 1, 0);
        Rect[] profileRects = profile.toArray();
        if (profileRects.length > 0) {
            printCost(startTime);
            return true;
        }

        //行人检测
        Mat image7 = Imgcodecs.imread(imgPath);
        Mat gary7 = new Mat();
        Imgproc.cvtColor(image7, gary7, Imgproc.COLOR_BGR2GRAY);
        HOGDescriptor hog = new HOGDescriptor();
        hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        MatOfRect hogRect = new MatOfRect();
        hog.detectMultiScale(gary7, hogRect, new MatOfDouble());
        Rect[] hogRects = hogRect.toArray();
        if (hogRects.length > 0) {
            printCost(startTime);
            return true;
        }

        printCost(startTime);
        return false;
    }

    private void printCost(long startTime) {
        long endTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + "  耗时: " + (endTime - startTime) / 1000d + "s");
    }
}
