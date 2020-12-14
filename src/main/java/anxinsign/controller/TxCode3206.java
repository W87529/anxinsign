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
import cfca.trustsign.common.vo.cs.SignContractVO;
import cfca.trustsign.common.vo.request.tx3.Tx3206ReqVO;

@Controller
public class TxCode3206 {
	@ResponseBody
	@PostMapping(value = "/3206")
    public static String txCode3206(@RequestBody JSONObject json) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3206ReqVO tx3206ReqVO = new Tx3206ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

//        SignContractVO signContract = new SignContractVO();
//        signContract.setContractNo("JK20180426000000001");
//        SignInfoVO signInfo = new SignInfoVO();
//        signInfo.setUserId("67BBC2B9929C6DAAE055000000000001");
//        signInfo.setLocation("211.94.108.226");
//        signInfo.setProjectCode("003");
//        signInfo.setIsCheckProjectCode(1);
//        signInfo.setSignLocation("Signature1");
//        signInfo.setAuthorizationTime("20160801095509");

        // 传图片或传sealId的方式任选其一，传图片优先级高
        // signInfo.setImageData(Base64.toBase64String(Files.readAllBytes(Paths.get("./image/赵六.png"))));
        // signInfo.setSealId("8C5E69F26A7E45F4977687301E120F83");
//        signContract.setSignInfo(signInfo);

        tx3206ReqVO.setHead(head);
//        tx3206ReqVO.setSignContract(signContract);
        JSONObject signContract = json.getJSONObject("signContract");
        tx3206ReqVO.setSignContract(JSON.parseObject(signContract.toString(), SignContractVO.class));

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3206ReqVO);
        System.out.println("3206-req:" + req);

        String txCode = "3206";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3206-res:" + res);
		return res;
    }
}
