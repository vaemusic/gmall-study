package com.mallstudy.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PmsUploadUtil {

    public static String uploadImage(MultipartFile multipartFile) {
        String imgUrl = "http://192.168.56.101";

        //上传图片到服务器
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();//获得配置文件的路径
        try {
            ClientGlobal.init(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();
        //获得trackerServer的实例
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //通过tracker获得一个Storage连接客户端
        StorageClient storageClient = new StorageClient(trackerServer, null);

        try {
            byte[] bytes = multipartFile.getBytes();//获得上传的二进制对象
            //获得文件后缀名
            String originalFilename = multipartFile.getOriginalFilename();//文件全名a.jpg
            int i = originalFilename.lastIndexOf(".");
            String extName = originalFilename.substring(i + 1);

            String[] uploadInfos = uploadInfos = storageClient.upload_file(bytes, "png", null);
            for (String uploadInfo : uploadInfos) {
                imgUrl += "/" + uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgUrl;
    }
}
