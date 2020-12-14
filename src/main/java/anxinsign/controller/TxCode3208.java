package anxinsign.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;
import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.cs.SignContractByCoordinateVO;
import cfca.trustsign.common.vo.request.tx3.Tx3208ReqVO;

@Controller
public class TxCode3208 {
	@ResponseBody
	@PostMapping(value = "/3208")
    public static String txCode3208(@RequestBody JSONObject json) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3208ReqVO tx3208ReqVO = new Tx3208ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

//        SignContractByCoordinateVO signContractByCoordinate = new SignContractByCoordinateVO();
//        signContractByCoordinate.setContractNo("JK20180518000000001");

//        SignLocationVO[] signLocations0 = new SignLocationVO[1];
//        SignLocationVO signLocation0 = new SignLocationVO();
//        signLocation0.setSignOnPage("1");
//        signLocation0.setSignLocationLBX("85");
//        signLocation0.setSignLocationLBY("550");
//        signLocation0.setSignLocationRUX("240");
//        signLocation0.setSignLocationRUY("675");
//        signLocations0[0] = signLocation0;
//        signContractByCoordinate.setSignLocations(signLocations0);

//        SignInfoVO signInfo = new SignInfoVO();
//        signInfo.setUserId("9C9E731AEE444B498F7B5DCFBA0CD0E8");
//        signInfo.setLocation("211.94.108.226");
//        signInfo.setAuthorizationTime("20160801095509");
//        signInfo.setProjectCode("003");
//        signInfo.setIsCheckProjectCode(1);
//        signInfo.setCertType(1);

        // 传图片或传sealId的方式任选其一，传图片优先级高
        // signInfo.setImageData(Base64.toBase64String(Files.readAllBytes(Paths.get("./image/赵六.png"))));
        // signInfo.setSealId("8C5E69F26A7E45F4977687301E120F83");
//        signContractByCoordinate.setSignInfo(signInfo);

        tx3208ReqVO.setHead(head);
        JSONObject signContractByCoordinate = json.getJSONObject("signContractByCoordinate");
        tx3208ReqVO.setSignContractByCoordinate(JSON.parseObject(signContractByCoordinate.toString(), SignContractByCoordinateVO.class));

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3208ReqVO);
        System.out.println("3208-req:" + req);

        String txCode = "3208";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3208-res:" + res);
		return res;
    }
}
