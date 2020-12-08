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
    public static String downloadFile(String contractNo,String fnumber) throws IOException {
		HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        byte[] fileBtye = httpConnector.getFile("platId/" + Request.PLAT_ID + "/contractNo/" + contractNo + "/downloading");
        if (CommonUtil.isEmpty(fileBtye)) {
            return "{\"errorCode\":\"50030301\",\"errorMessage\":\"ID为B3F40CDD852438CCE05312016B0A71AC的用户合同编号为"+contractNo+"的合同信息不存在\"}";
        }

        String filePath = "D:\\FTP\\Customers\\"+fnumber+"\\contract";
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        Files.write(Paths.get(filePath + "/" + contractNo + ".pdf"), fileBtye);
		return "{\"head\":{\"retCode\":\"60000000\",\"retMessage\":\"OK\"},\"filePath\":\"http://110.16.84.155:8090/customers/"+fnumber + "/" + "contract" + "/" + contractNo + ".pdf\"}";
    }
}
