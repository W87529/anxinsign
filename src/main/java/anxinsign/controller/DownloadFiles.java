package anxinsign.controller;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.util.TimeUtil;

@Controller
public class DownloadFiles {
	@ResponseBody
	@PostMapping(value = "/downloadFiles")
    public static String downloadFiles(String contractNos) {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        byte[] fileBtye = httpConnector.getFile("platId/" + Request.PLAT_ID + "/contractNos/" + contractNos + "/batchDownloading");
        if (fileBtye == null || fileBtye.length == 0) {
            return "{\"errorCode\":\"50030301\",\"errorMessage\":\"ID为B3F40CDD852438CCE05312016B0A71AC的用户合同编号为"+contractNos+"的合同信息不存在\"}";
        }

        String name = TimeUtil.getCurrentTime(TimeUtil.FORMAT_14);
        try {
        	String filePath = "C:/file";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(filePath + File.separator + name + ".zip");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileBtye);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return "{\"head\":{\"retCode\":\"60000000\",\"retMessage\":\"OK\"},\"filePath\":\"C:/file" + File.separator + name + ".zip\"}";
    }
}
