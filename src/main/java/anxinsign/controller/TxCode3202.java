package anxinsign.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import anxinsign.connector.HttpConnector;
import anxinsign.constant.Request;
import anxinsign.converter.JsonObjectMapper;
import anxinsign.util.SecurityUtil;
import anxinsign.util.TimeUtil;
import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.CreateContractVO;
import cfca.trustsign.common.vo.cs.HeadVO;
import cfca.trustsign.common.vo.request.tx3.Tx3202ReqVO;

@Controller
public class TxCode3202 {
	@ResponseBody
	@PostMapping(value = "/3202")
    public static String txCode3202(@RequestBody JSONObject json) throws PKIException {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.init();

        Tx3202ReqVO tx3202ReqVO = new Tx3202ReqVO();
        HeadVO head = new HeadVO();
        head.setTxTime(TimeUtil.getCurrentTime(TimeUtil.FORMAT_14));

//        List<CreateContractVO> createContractlist = new ArrayList<CreateContractVO>();
//
//        JSONArray createContracts = json.getJSONArray("createContracts");
//        for (int i = 0; i < createContracts.size(); i++) {
//			JSONObject createContractJSON = createContracts.getJSONObject(i);
//			
//			CreateContractVO createContract = new CreateContractVO();
//
//	         createContract.setIsSign(createContractJSON.getIntValue("isSign"));
//	         createContract.setSealId(createContractJSON.getString("sealId"));
//	        createContract.setTemplateId(createContractJSON.getString("templateId"));
//	        createContract.setContractName(createContractJSON.getString("contractName"));
//	        createContract.setSealColor(createContractJSON.getIntValue("sealColor"));
//	         createContract.setSignLocation(createContractJSON.getString("signLocation"));
//
//	        // setInvestmentInfo方法废弃，使用setTextValueInfo代替
//	        // createContract.setInvestmentInfo(fieldMap);
//	         if(createContractJSON.getJSONObject("textValueInfo")!=null) {
//	        	 createContract.setTextValueInfo(JSONObject.parseObject(createContractJSON.getJSONObject("textValueInfo").toJSONString(), new TypeReference<Map<String, String>>(){}));
//	         }
//	        createContract.setIsSaveTextValue(createContractJSON.getIntValue("isSaveTextValue"));
//	        createContract.setIsFillInContractNo(createContractJSON.getIntValue("isFillInContractNo"));
//	        createContract.setIsFillInFont(createContractJSON.getIntValue("isFillInFont"));
//	        createContract.setExpiredDate(createContractJSON.getString("expiredDate"));
//
//	        JSONArray signInfosArr = createContractJSON.getJSONArray("signInfos");
//	        if(signInfosArr.size()>0) {
//	            SignInfoVO[] signInfos = new SignInfoVO[signInfosArr.size()];
//	        	for (int i1 = 0; i1 < signInfosArr.size(); i1++) {
//	            	JSONObject signInfosJSON = signInfosArr.getJSONObject(i1);
//	            	SignInfoVO signInfoVO0 = new SignInfoVO();
//	                signInfoVO0.setUserId(signInfosJSON.getString("userId"));
//	                signInfoVO0.setIsProxySign(signInfosJSON.getIntValue("isProxySign"));
//	                signInfoVO0.setLocation(signInfosJSON.getString("location"));
//	                signInfoVO0.setProjectCode(signInfosJSON.getString("signLocation"));
//	                 signInfoVO0.setIsCheckProjectCode(signInfosJSON.getIntValue("projectCode"));
//	                signInfoVO0.setSignLocation(signInfosJSON.getString("isCheckProjectCode"));
//	                signInfoVO0.setAuthorizationTime(signInfosJSON.getString("authorizationTime"));
//	                signInfoVO0.setIsCopy(signInfosJSON.getIntValue("isCopy"));
//	                 signInfoVO0.setSealId(signInfosJSON.getString("sealId"));
//	                 signInfoVO0.setSealColor(signInfosJSON.getIntValue("sealColor"));
//	                signInfos[i1] = signInfoVO0;
//	    		}
//	            createContract.setSignInfos(signInfos);
//	        }
//
//	        // 使用水印时用到
//	        // WatermarkVO watermark = new WatermarkVO();
//	        // watermark.setWatermarkOnPage("1");
//	        // watermark.setWatermarkLBX("100");
//	        // watermark.setWatermarkLBY("100");
//	        // watermark.setWatermarkWidth("100");
//	        // watermark.setWatermarkHeight("100");
//	        // watermark.setWatermarkData(Base64.toBase64String(Files.readAllBytes(Paths.get("./image/王五.png"))));
//	        // createContract.setWatermark(watermark);
//
//	        // 使用附件时用到
//	        JSONObject attachmentJSON = createContractJSON.getJSONObject("attachment");
//	        if(attachmentJSON!=null) {
//	        	AttachmentVO attachment = new AttachmentVO();
//	            attachment.setAttachmentName(attachmentJSON.getString("attachmentName"));
//	            attachment.setAttachmentContent(attachmentJSON.getString("attachmentContent"));
//	            createContract.setAttachment(attachment);
//	        }
//	        createContractlist.add(createContract);
//		}

        tx3202ReqVO.setHead(head);
        tx3202ReqVO.setBatchNo(json.getString("batchNo"));
        
        JSONArray createContracts = json.getJSONArray("createContracts");
        CreateContractVO[] createContractVO = new CreateContractVO[createContracts.size()];
      for (int i = 0; i < createContracts.size(); i++) {
    	  createContractVO[i] = JSON.parseObject(createContracts.getJSONObject(i).toString(), CreateContractVO.class);
      }
        
//        tx3202ReqVO.setCreateContracts(createContractlist.toArray(new CreateContractVO[0]));
        tx3202ReqVO.setCreateContracts(createContractVO);

        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String req = jsonObjectMapper.writeValueAsString(tx3202ReqVO);
        System.out.println("req:" + req);

        String txCode = "3202";
        String signature = SecurityUtil.p7SignMessageDetach(HttpConnector.JKS_PATH, HttpConnector.JKS_PWD, HttpConnector.ALIAS, req);
        String res = httpConnector.post("platId/" + Request.PLAT_ID + "/txCode/" + txCode + "/transaction", req, signature);
        System.out.println("res:" + res);
		return res;
    }
}
