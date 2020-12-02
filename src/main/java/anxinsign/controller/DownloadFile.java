package anxinsign.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import cfca.trustsign.common.util.CommonUtil;

@Controller
public class DownloadFile {
	@ResponseBody
	@PostMapping(value = "/downloadFile")
    public static void downloadFile(String contractNo) throws IOException {
		HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        byte[] fileBtye = httpConnector.getFile("platId/" + Request.PLAT_ID + "/contractNo/" + contractNo + "/downloading");
        if (CommonUtil.isEmpty(fileBtye)) {
            return;
        }

        String filePath = "C:/file";
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        Files.write(Paths.get(filePath + File.separator + contractNo + ".pdf"), fileBtye);
    }
}
