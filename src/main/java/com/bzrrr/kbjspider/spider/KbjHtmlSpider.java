package com.bzrrr.kbjspider.spider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bzrrr.kbjspider.model.dto.KbjDto;
import com.bzrrr.kbjspider.service.KbjService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class KbjHtmlSpider implements Runnable {
    private static Set<String> fileUrls = new HashSet<>();
    private static Set<String> imgUrls = new HashSet<>();
    private static int index = 1326;//1326
    private static Pattern imgNameReg = Pattern.compile("\\d{11,}");
    private static Pattern imgDirReg = Pattern.compile("\\d{4}/\\d{2}");
    private static long sleepTime = 5000L;

    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("kbj-pool-%d").build();
    ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private KbjService service;

//    @PostConstruct
    public void init() {
        singleThreadPool.execute(this);
//        initFileNames();
//        List<KbjDto> list = new ArrayList<>();
//        for (String url : fileUrls) {
//            KbjDto dto = new KbjDto();
//            dto.setLink(url);
//            dto.setSaved(true);
//            String[] split = url.split("/");
//            dto.setFilename(split[split.length - 1]);
//            list.add(dto);
//        }
//        service.saveBatch(list);
    }

    private boolean printLinks(int page) {
        try {
            Document doc = Jsoup.connect("http://www.kav1004.com/page/" + page + "/").get();
            Elements elements = doc.getElementsByTag("article");
            QueryWrapper<KbjDto> wrapper = new QueryWrapper<>();
            for (Element e : elements) {
                wrapper.clear();
                Element titleTag = e.selectFirst("header > h2 > a");
                String title = titleTag.text();

                Element linkTag = e.selectFirst("a[href*=uploadgig]");
                String href = linkTag.attr("href");

                wrapper.eq("link", href);
                KbjDto dto = service.getOne(wrapper);
                if (dto == null) {
                    dto = new KbjDto();
                } else {
                    System.out.println(title);
                }
                dto.setTitle(title);
                dto.setLink(href);
                String[] split = href.split("/");
                dto.setFilename(split[split.length - 1]);
                service.saveOrUpdate(dto);
            }
            Elements nextPageElement = doc.getElementsByClass("next page-numbers");

//            List<KbjDto> list = new ArrayList<>();
//            for (String link : links) {
//                if (!checkFile(link)) {
////                    System.out.println(link);
//                    KbjDto dto = new KbjDto();
//                    dto.setLink(link);
//                    dto.setSaved(true);
////                    String[] split = url.split("/");
////                    dto.setFilename(split[split.length - 1]);
//                    list.add(dto);
//                } else {
//                    wrapper.clear();
//                    wrapper.eq("link", link);
//                    KbjDto dto = service.getOne(wrapper);
////                    dto.setTitle();
//                }
//            }
//            service.saveBatch(list);
//            System.out.println(links.size());
            return nextPageElement != null && nextPageElement.size() > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkFile(String link) {
        QueryWrapper<KbjDto> wrapper = new QueryWrapper<>();
        wrapper.eq("link", link);
        int count = service.count(wrapper);
        return count > 0;
    }

    private boolean checkImage(String imgLink) {
        return imgUrls.contains(imgLink);
    }

    private void initFileNames() {
        try (FileInputStream inputStream = new FileInputStream("D:\\pic\\kbj\\downloadFiles.txt");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                fileUrls.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
//        initImgUrls();
        boolean hasNext = true;
        while (hasNext) {
            log.info("kbj: page -- " + index);
            hasNext = printLinks(index);
            index++;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printLinks(index);
    }

    private void initImgUrls() {
        try (FileInputStream inputStream = new FileInputStream("D:\\pic\\kbj_img\\downloadImgs.txt");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                imgUrls.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImage(String imgPath) {
        InputStream inputStream = null;
        OutputStreamWriter osw = null;
        FileOutputStream fos = null;
        try {
            String proxyHost = "0.0.0.0";
            String proxyPort = "1080";
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
            URL url = new URL(imgPath);
            URLConnection conn = url.openConnection(proxy);
            inputStream = conn.getInputStream();
            Matcher imgDirMatcher = imgDirReg.matcher(imgPath);
            String imgDir = "";
            if (imgDirMatcher.find()) {
                imgDir = imgDirMatcher.group().replace("/", "-") + "\\";
            }

            String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
            Matcher imgNameMatcher = imgNameReg.matcher(imgName);
            if (imgNameMatcher.find()) {
                String tName = imgNameMatcher.group();
                String pre = tName.substring(0, 10);
                String post = "(" + tName.substring(10) + ")";
                imgName = imgName.replace(tName, pre + post);
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream("D:\\pic\\kbj_img\\" + imgDir + imgName);
            } catch (FileNotFoundException e) {
                File dir = new File("D:\\pic\\kbj_img\\" + imgDir);
                dir.mkdir();
                out = new FileOutputStream("D:\\pic\\kbj_img\\" + imgDir + imgName);
            }
            int j = 0;
            while ((j = inputStream.read()) != -1) {
                out.write(j);
            }
            fos = new FileOutputStream(new File("D:\\pic\\kbj_img\\downloadImgs.txt"), true);
            osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            osw.write(imgPath + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                osw.close();
                fos.close();
                inputStream.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
