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
import cfca.trustsign.common.vo.cs.CreateContractVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3201ReqVO;

@Controller
public class TxCode3201 {
	@ResponseBody
	@PostMapping(value = "/3201")
    public static String txCode3201(@RequestBody JSONObject json) throws PKIException, IOException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();
        JSONObject createContractJSON = json.getJSONObject("createContract");
        Tx3201ReqVO tx3201ReqVO = new Tx3201ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

//        CreateContractVO createContract = new CreateContractVO();
//
//         createContract.setIsSign(createContractJSON.getIntValue("isSign"));
//         createContract.setSealId(createContractJSON.getString("sealId"));
//        createContract.setTemplateId(createContractJSON.getString("templateId"));
//        createContract.setContractName(createContractJSON.getString("contractName"));
//        createContract.setSealColor(createContractJSON.getIntValue("sealColor"));
//         createContract.setSignLocation(createContractJSON.getString("signLocation"));
//
//        // setInvestmentInfo方法废弃，使用setTextValueInfo代替
//        // createContract.setInvestmentInfo(fieldMap);
//         if(createContractJSON.getJSONObject("textValueInfo")!=null) {
//        	 createContract.setTextValueInfo(JSONObject.parseObject(createContractJSON.getJSONObject("textValueInfo").toJSONString(), new TypeReference<Map<String, String>>(){}));
//         }
//        createContract.setIsSaveTextValue(createContractJSON.getIntValue("isSaveTextValue"));
//        createContract.setIsFillInContractNo(createContractJSON.getIntValue("isFillInContractNo"));
//        createContract.setIsFillInFont(createContractJSON.getIntValue("isFillInFont"));
//        createContract.setExpiredDate(createContractJSON.getString("expiredDate"));
//
//        JSONArray signInfosArr = createContractJSON.getJSONArray("signInfos");
//        if(signInfosArr.size()>0) {
//            SignInfoVO[] signInfos = new SignInfoVO[signInfosArr.size()];
//        	for (int i = 0; i < signInfosArr.size(); i++) {
//            	JSONObject signInfosJSON = signInfosArr.getJSONObject(i);
//            	SignInfoVO signInfoVO0 = new SignInfoVO();
//                signInfoVO0.setUserId(signInfosJSON.getString("userId"));
//                signInfoVO0.setIsProxySign(signInfosJSON.getIntValue("isProxySign"));
//                signInfoVO0.setLocation(signInfosJSON.getString("location"));
//                signInfoVO0.setProjectCode(signInfosJSON.getString("signLocation"));
//                 signInfoVO0.setIsCheckProjectCode(signInfosJSON.getIntValue("projectCode"));
//                signInfoVO0.setSignLocation(signInfosJSON.getString("isCheckProjectCode"));
//                signInfoVO0.setAuthorizationTime(signInfosJSON.getString("authorizationTime"));
//                signInfoVO0.setIsCopy(signInfosJSON.getIntValue("isCopy"));
//                 signInfoVO0.setSealId(signInfosJSON.getString("sealId"));
//                 signInfoVO0.setSealColor(signInfosJSON.getIntValue("sealColor"));
//                signInfos[i] = signInfoVO0;
//    		}
//            createContract.setSignInfos(signInfos);
//        }
//
//        // 使用水印时用到
//        // WatermarkVO watermark = new WatermarkVO();
//        // watermark.setWatermarkOnPage("1");
//        // watermark.setWatermarkLBX("100");
//        // watermark.setWatermarkLBY("100");
//        // watermark.setWatermarkWidth("100");
//        // watermark.setWatermarkHeight("100");
//        // watermark.setWatermarkData(Base64.toBase64String(Files.readAllBytes(Paths.get("./image/王五.png"))));
//        // createContract.setWatermark(watermark);
//
//        // 使用附件时用到
//        JSONObject attachmentJSON = createContractJSON.getJSONObject("attachment");
//        if(attachmentJSON!=null) {
//        	AttachmentVO attachment = new AttachmentVO();
//            attachment.setAttachmentName(attachmentJSON.getString("attachmentName"));
//            attachment.setAttachmentContent(attachmentJSON.getString("attachmentContent"));
//            createContract.setAttachment(attachment);
//        }

        tx3201ReqVO.setHead(head);
//        tx3201ReqVO.setCreateContract(createContract);
        tx3201ReqVO.setCreateContract(JSON.parseObject(createContractJSON.toString(), CreateContractVO.class));

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3201ReqVO);
        System.out.println("3201-req:" + req);

        String txCode = "3201";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("3201-res:" + res);
		return res;
    }
}
