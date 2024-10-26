package com.example.chatplatform.util;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Properties;

@Component
public class FastDFSClient {
    private final TrackerClient trackerClient;
    private final TrackerServer trackerServer;
    private final StorageClient storageClient;
    private final StorageServer storageServer;
    @Value("${nginx.host}")
    private String host;
    @Value("${nginx.port}")
    private String port;
    FastDFSClient() throws MyException, IOException {
        Properties properties = new Properties();
        properties.put(ClientGlobal.PROP_KEY_TRACKER_SERVERS, "121.37.188.103:22122");
        properties.put(ClientGlobal.CONF_KEY_HTTP_TRACKER_HTTP_PORT,"8888");
        properties.put(ClientGlobal.CONF_KEY_CONNECT_TIMEOUT,5);
        properties.put(ClientGlobal.PROP_KEY_CONNECTION_POOL_ENABLED,true);

//        properties.put(ClientGlobal.PROP_KEY_TRACKER_SERVERS, "192.168.13.156:22122");
        ClientGlobal.initByProperties(properties);
        trackerClient = new TrackerClient();//初始化tracker客户端
        trackerServer = trackerClient.getTrackerServer();//使用trackerClient获得trackerServer
        storageServer = trackerClient.getStoreStorage(trackerServer);
        storageClient = new StorageClient(trackerServer,storageServer);
    }

    /**
     * 上传MultipartFile类型的文件
     * @param file
     * @return
     */
    /**
     * 上传MultipartFile类型的文件
     *
     * @param file 文件
     * @return 文件在FastDFS中的路径
     */
    public String uploadMultipartFile(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String suffix = null;
            if (originalName != null) {
                suffix = getFileSuffix(originalName);
            }
            String[] uploadResults = storageClient.upload_file(file.getBytes(), suffix, null);
            if (uploadResults == null) {
                throw new IOException("文件上传失败");
            }
            String groupName = uploadResults[0];
            String remoteFileName = uploadResults[1];
            return "http://"+host+":"+port+"/"+groupName + "/" + remoteFileName;
            //return "/"+groupName + "/" + remoteFileName;
        } catch (IOException | MyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String uploadByteArray(byte[] fileBytes, String originalName) {
        try {
            String suffix = getFileSuffix(originalName);
            String[] uploadResults = storageClient.upload_file(fileBytes, suffix, null);
            if (uploadResults == null) {
                throw new IOException("文件上传失败");
            }
            String groupName = uploadResults[0];
            String remoteFileName = uploadResults[1];
            return "http://" + host + ":" + port + "/" + groupName + "/" + remoteFileName;
        } catch (IOException | MyException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除文件
     * @param filePath 文件在FastDFS中的路径
     * @return 删除操作结果
     */
    public boolean delete(String filePath) {
        try {
            int result = storageClient.delete_file(filePath.split("/")[0], filePath.split("/")[1]);
            return result == 0;
        } catch (IOException | MyException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getFileSuffix(String fileName) {
        if (fileName.isEmpty()) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return fileName.substring(index + 1);
    }
}
