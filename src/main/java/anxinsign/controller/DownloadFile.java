package anxinsign.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
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
    public static String downloadFile(String contractNo,String fnumber,int isJPG) throws IOException {
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
        StringBuffer jpgPath = new StringBuffer();
        jpgPath.append(",\"jpgPaths\":[");
        if(isJPG==1) {
        	try (PDDocument document = PDDocument.load(fileBtye)) {
                PDFRenderer renderer = new PDFRenderer(document);
                for (int i = 0; i < document.getNumberOfPages(); ++i) {
                    BufferedImage bufferedImage = renderer.renderImageWithDPI(i, 100);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", out);
                    Files.write(Paths.get(filePath + "/" + contractNo + "-" + (i+1) + ".jpg"), out.toByteArray());
                    if(i!=0) {
                    	jpgPath.append(",");
                    }
                    jpgPath.append("{\"jpgPath\":\"http://110.16.84.155:8090/customers/" + fnumber + "/" + "contract" + "/" + contractNo + "-" + (i+1) + ".jpg\"}");
                }
            }
        }
        jpgPath.append("]");
        
		return "{\"head\":{\"retCode\":\"60000000\",\"retMessage\":\"OK\"},\"filePath\":\"http://110.16.84.155:8090/customers/" + fnumber + "/" + "contract" + "/" + contractNo + ".pdf\"" + jpgPath.toString() + "}";
    }
}
