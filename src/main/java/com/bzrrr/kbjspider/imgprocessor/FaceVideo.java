package com.bzrrr.kbjspider.imgprocessor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.*;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

/**
 * @Author: wangziheng
 * @Date: 2021/3/17
 */
public class FaceVideo {
    // 初始化人脸探测器
//    static CascadeClassifier faceDetector;
//    static CascadeClassifier faceDetector2;
//    static CascadeClassifier fullbodyDetector;
//    static CascadeClassifier lowerbodyDetector;
//    static CascadeClassifier upperbodyDetector;

    static int i = 0;

    static volatile int count = 0;

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        faceDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml");
//        faceDetector2 = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt2.xml");
//        fullbodyDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_fullbody.xml");
//        lowerbodyDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_lowerbody.xml");
//        upperbodyDetector = new CascadeClassifier("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_upperbody.xml");
//    }

    public static CascadeClassifier buildDetector(String xmlPath) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        return new CascadeClassifier(xmlPath);
    }

    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("face-pool-%d").build();
    ExecutorService singleThreadPool = new ThreadPoolExecutor(5, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(5);

        File dir = new File("D:\\pic\\2km__2km");
        File[] imgs = dir.listFiles();
        if (imgs != null && imgs.length > 0) {
            int len = imgs.length / 5;
            File[] imgs1 = Arrays.copyOfRange(imgs, 0, len);
            File[] imgs2 = Arrays.copyOfRange(imgs, len, len * 2);
            File[] imgs3 = Arrays.copyOfRange(imgs, len * 2, len * 3);
            File[] imgs4 = Arrays.copyOfRange(imgs, len * 3, len * 4);
            File[] imgs5 = Arrays.copyOfRange(imgs, len * 4, imgs.length - 1);
            new Thread(() -> th(imgs1, latch, count)).start();
            new Thread(() -> th(imgs2, latch, count)).start();
            new Thread(() -> th(imgs3, latch, count)).start();
            new Thread(() -> th(imgs4, latch, count)).start();
            new Thread(() -> th(imgs5, latch, count)).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(count);
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时: " + (endTime - startTime) / 1000d + "s");
    }

    private static void th(File[] imgs1, CountDownLatch latch, int count) {
        CascadeClassifier faceDetector = buildDetector("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml");
        CascadeClassifier faceDetector2 = buildDetector("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml");
        CascadeClassifier fullbodyDetector = buildDetector("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml");
        CascadeClassifier lowerbodyDetector = buildDetector("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml");
        CascadeClassifier upperbodyDetector = buildDetector("D:\\develop_tools\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml");
        for (File img : imgs1) {
            if (img.getName().endsWith("mp4")) {
                continue;
            }
            boolean hasFace = personCheck(img.getAbsolutePath(), faceDetector, faceDetector2, fullbodyDetector, lowerbodyDetector, upperbodyDetector);
            if (hasFace) {
                count++;
            } else {
                System.out.println(img.getName());
            }
        }
        latch.countDown();
    }

    /**
     * OpenCV-4.1.1 从摄像头实时读取
     *
     * @return: void
     * @date: 2019年8月19日 17:20:13
     */
//    public static void getVideoFromCamera() {
//        //1 如果要从摄像头获取视频 则要在 VideoCapture 的构造方法写 0
//        VideoCapture capture = new VideoCapture(0);
//        Mat video = new Mat();
//        int index = 0;
//        if (capture.isOpened()) {
//            while (i < 3) {// 匹配成功3次退出
//                capture.read(video);
//                HighGui.imshow("实时人脸识别", getFace(video));
//                index = HighGui.waitKey(100);
//                if (index == 27) {
//                    capture.release();
//                    break;
//                }
//            }
//        } else {
//            System.out.println("摄像头未开启");
//        }
//        try {
//            capture.release();
//            Thread.sleep(1000);
//            System.exit(0);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return;
//    }

    /**
     * OpenCV-4.1.1 从视频文件中读取
     *
     * @return: void
     * @date: 2019年8月19日 17:20:20
     */
//    public static void getVideoFromFile() {
//        VideoCapture capture = new VideoCapture();
//        capture.open("C:\\Users\\Administrator\\Desktop\\1.avi");//1 读取视频文件的路径
//
//        if (!capture.isOpened()) {
//            System.out.println("读取视频文件失败！");
//            return;
//        }
//        Mat video = new Mat();
//        int index = 0;
//        while (capture.isOpened()) {
//            capture.read(video);//2 视频文件的视频写入 Mat video 中
//            HighGui.imshow("本地视频识别人脸", getFace(video));//3 显示图像
//            index = HighGui.waitKey(100);//4 获取键盘输入
//            if (index == 27) {//5 如果是 Esc 则退出
//                capture.release();
//                return;
//            }
//        }
//    }

    /**
     * OpenCV-4.1.1 人脸识别
     *
     * @param image 待处理Mat图片(视频中的某一帧)
     * @return 处理后的图片
     * @date: 2019年8月19日 17:19:36
     */
//    public static Mat getFace(Mat image) {
//        // 1 读取OpenCV自带的人脸识别特征XML文件(faceDetector)
////        CascadeClassifier facebook=new CascadeClassifier("D:\\Sofeware\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
//        // 2  特征匹配类
//        MatOfRect face = new MatOfRect();
//        // 3 特征匹配
//        faceDetector.detectMultiScale(image, face);
//        Rect[] rects = face.toArray();
//        System.out.println("匹配到 " + rects.length + " 个人脸");
//        if (rects != null && rects.length >= 1) {
//
//            // 4 为每张识别到的人脸画一个圈
//            for (int i = 0; i < rects.length; i++) {
//                Imgproc.rectangle(image, new Point(rects[i].x, rects[i].y), new Point(rects[i].x + rects[i].width, rects[i].y + rects[i].height), new Scalar(0, 255, 0));
//                Imgproc.putText(image, "Human", new Point(rects[i].x, rects[i].y), Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX, 1.0, new Scalar(0, 255, 0), 1, Imgproc.LINE_AA, false);
//                //Mat dst=image.clone();
//                //Imgproc.resize(image, image, new Size(300,300));
//            }
//            i++;
//            if (i == 3) {// 获取匹配成功第10次的照片
//                Imgcodecs.imwrite("D:\\Documents\\Pictures\\" + "face.png", image);
//            }
//        }
//        return image;
//    }

    /**
     * OpenCV-4.1.1 图片人脸识别
     *
     * @return: void
     * @date: 2019年5月7日12:16:55
     */
//    public static void face() {
//        // 1 读取OpenCV自带的人脸识别特征XML文件
//        //OpenCV 图像识别库一般位于 opencv\sources\data 下面
////        CascadeClassifier facebook=new CascadeClassifier("D:\\Sofeware\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
//        // 2 读取测试图片
//        String imgPath = "D:\\Documents\\Pictures\\he.png";
//        Mat image = Imgcodecs.imread(imgPath);
//        if (image.empty()) {
//            System.out.println("image 内容不存在！");
//            return;
//        }
//        // 3 特征匹配
//        MatOfRect face = new MatOfRect();
//        faceDetector.detectMultiScale(image, face);
//        // 4 匹配 Rect 矩阵 数组
//        Rect[] rects = face.toArray();
//        System.out.println("匹配到 " + rects.length + " 个人脸");
//        // 5 为每张识别到的人脸画一个圈
//        int i = 1;
//        for (Rect rect : face.toArray()) {
//            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//                    new Scalar(0, 255, 0), 3);
//            imageCut(imgPath, "D:\\Documents\\Pictures\\" + i + ".jpg", rect.x, rect.y, rect.width, rect.height);// 进行图片裁剪
//            i++;
//        }
//        // 6 展示图片
//        HighGui.imshow("人脸识别", image);
//        HighGui.waitKey(0);
//    }
    public static boolean personCheck(String imgPath, CascadeClassifier faceDetector, CascadeClassifier faceDetector2, CascadeClassifier fullbodyDetector, CascadeClassifier lowerbodyDetector, CascadeClassifier upperbodyDetector) {
        long startTime = System.currentTimeMillis();
        Mat image = Imgcodecs.imread(imgPath);
        if (image.empty()) {
            return false;
        }

        // 3 特征匹配
        Mat gary1 = new Mat();
        Imgproc.cvtColor(image, gary1, Imgproc.COLOR_BGR2GRAY);
        MatOfRect face = new MatOfRect();
        faceDetector.detectMultiScale(gary1, face, 1.03, 2, CASCADE_SCALE_IMAGE,
                new Size(20d, 20d));
        Rect[] faceRects = face.toArray();
        if (faceRects.length > 0) {
            printCost(startTime);
            return true;
        }

        Mat image2 = Imgcodecs.imread(imgPath);
        Mat gary2 = new Mat();
        Imgproc.cvtColor(image2, gary2, Imgproc.COLOR_BGR2GRAY);
        MatOfRect face2 = new MatOfRect();
        faceDetector2.detectMultiScale(gary2, face2, 1.03, 2, CASCADE_SCALE_IMAGE,
                new Size(20d, 20d));
        Rect[] faceRects2 = face.toArray();
        if (faceRects2.length > 0) {
            printCost(startTime);
            return true;
        }

        Mat image3 = Imgcodecs.imread(imgPath);
        Mat gary3 = new Mat();
        Imgproc.cvtColor(image3, gary3, Imgproc.COLOR_BGR2GRAY);
        MatOfRect fullbody = new MatOfRect();
        fullbodyDetector.detectMultiScale(gary3, fullbody, 1.03, 2, CASCADE_SCALE_IMAGE,
                new Size(20d, 20d));
        Rect[] fullbodyRects = fullbody.toArray();
        if (fullbodyRects.length > 0) {
            printCost(startTime);
            return true;
        }

        Mat image4 = Imgcodecs.imread(imgPath);
        Mat gary4 = new Mat();
        Imgproc.cvtColor(image4, gary4, Imgproc.COLOR_BGR2GRAY);
        MatOfRect lowerbody = new MatOfRect();
        lowerbodyDetector.detectMultiScale(gary4, lowerbody, 1.03, 2, CASCADE_SCALE_IMAGE,
                new Size(20d, 20d));
        Rect[] lowerbodyRects = lowerbody.toArray();
        if (lowerbodyRects.length > 0) {
            printCost(startTime);
            return true;
        }

        Mat image5 = Imgcodecs.imread(imgPath);
        Mat gary5 = new Mat();
        Imgproc.cvtColor(image5, gary5, Imgproc.COLOR_BGR2GRAY);
        MatOfRect upperbody = new MatOfRect();
        upperbodyDetector.detectMultiScale(gary5, upperbody, 1.03, 2, CASCADE_SCALE_IMAGE,
                new Size(20d, 20d));
        Rect[] upperbodyRects = upperbody.toArray();
        if (upperbodyRects.length > 0) {
            printCost(startTime);
            return true;
        }

        //行人检测
        Mat image6 = Imgcodecs.imread(imgPath);
        Mat gary6 = new Mat();
        Imgproc.cvtColor(image6, gary6, Imgproc.COLOR_BGR2GRAY);
        HOGDescriptor hog = new HOGDescriptor();
        hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        MatOfRect hogRect = new MatOfRect();
        hog.detectMultiScale(gary6, hogRect, new MatOfDouble(), 0, new Size(8, 8), new Size(0, 0));
        Rect[] hogRects = hogRect.toArray();
        if (hogRects.length > 0) {
            printCost(startTime);
            return true;
        }

        printCost(startTime);
        return false;
    }

    private static void printCost(long startTime) {
        long endTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + "  耗时: " + (endTime - startTime) / 1000d + "s");
    }

    /**
     * 裁剪人脸
     *
     * @param imagePath
     * @param outFile
     * @param posX
     * @param posY
     * @param width
     * @param height
     */
    public static void imageCut(String imagePath, String outFile, int posX, int posY, int width, int height) {
        // 原始图像
        Mat image = Imgcodecs.imread(imagePath);
        // 截取的区域：参数,坐标X,坐标Y,截图宽度,截图长度
        Rect rect = new Rect(posX, posY, width, height);
        // 两句效果一样
        Mat sub = image.submat(rect); // Mat sub = new Mat(image,rect);
        Mat mat = new Mat();
        Size size = new Size(width, height);
        Imgproc.resize(sub, mat, size);// 将人脸进行截图并保存
        Imgcodecs.imwrite(outFile, mat);
        System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
    }

    /**
     * 人脸比对
     *
     * @param img_1
     * @param img_2
     * @return
     */
//    public static double compare_image(String img_1, String img_2) {
//        Mat mat_1 = conv_Mat(img_1);
//        Mat mat_2 = conv_Mat(img_2);
//        Mat hist_1 = new Mat();
//        Mat hist_2 = new Mat();
//
//        //颜色范围
//        MatOfFloat ranges = new MatOfFloat(0f, 256f);
//        //直方图大小， 越大匹配越精确 (越慢)
//        MatOfInt histSize = new MatOfInt(1000);
//
//        Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
//        Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);
//
//        // CORREL 相关系数
//        double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
//        return res;
//    }

    /**
     * 灰度化人脸
     *
     * @param img
     * @return
     */
//    public static Mat conv_Mat(String img) {
//        Mat image0 = Imgcodecs.imread(img);
//
//        Mat image1 = new Mat();
//        // 灰度化
//        Imgproc.cvtColor(image0, image1, Imgproc.COLOR_BGR2GRAY);
//        // 探测人脸
//        MatOfRect faceDetections = new MatOfRect();
//        faceDetector.detectMultiScale(image1, faceDetections);
//        // rect中人脸图片的范围
//        for (Rect rect : faceDetections.toArray()) {
//            Mat face = new Mat(image1, rect);
//            return face;
//        }
//        return null;
//    }

    /**
     * OpenCV-4.1.1 将摄像头拍摄的视频写入本地
     *
     * @return: void
     * @date: 2019年8月19日 17:20:48
     */
    public static void writeVideo() {
        //1 如果要从摄像头获取视频 则要在 VideoCapture 的构造方法写 0
        VideoCapture capture = new VideoCapture(0);
        Mat video = new Mat();
        int index = 0;
        Size size = new Size(capture.get(Videoio.CAP_PROP_FRAME_WIDTH), capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        VideoWriter writer = new VideoWriter("D:/a.mp4", VideoWriter.fourcc('D', 'I', 'V', 'X'), 15.0, size, true);
        while (capture.isOpened()) {
            capture.read(video);//2 将摄像头的视频写入 Mat video 中
            writer.write(video);
            HighGui.imshow("像头获取视频", video);//3 显示图像
            index = HighGui.waitKey(100);//4 获取键盘输入
            if (index == 27) {//5 如果是 Esc 则退出
                capture.release();
                writer.release();
                return;
            }
        }
    }

}
