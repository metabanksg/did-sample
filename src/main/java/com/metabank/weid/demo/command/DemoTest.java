/*
 *       Copyright© (2019) metabank Co., Ltd.
 *
 *       This file is part of did-sample.
 *
 *       did-sample is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       did-sample is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with did-sample.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.metabank.weid.demo.command;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.metabank.weid.constant.CredentialType;
import com.metabank.weid.constant.JsonSchemaConstant;
import com.metabank.weid.protocol.base.CptBaseInfo;
import com.metabank.weid.protocol.base.CredentialPojo;
import com.metabank.weid.protocol.base.WeIdAuthentication;
import com.metabank.weid.protocol.base.WeIdDocument;
import com.metabank.weid.protocol.request.CreateCredentialPojoArgs;
import com.metabank.weid.protocol.response.CreateWeIdDataResult;

/**
 * WeIdentity DID demo.
 *
 * @author v_wbgyang
 */
public class DemoTest extends DemoBase {

    /**
     * main of demo.
     * @throws ParseException the parseException
     * @throws RuntimeException the runtimeException
     */
    public static void main(String[] args) throws RuntimeException, ParseException {

        // create weId
        CreateWeIdDataResult createWeId = demoService.createWeId();
        BaseBean.print(createWeId);

        demoService.setAuthentication(createWeId);

        // get WeId DOM.
        WeIdDocument weIdDom = demoService.getWeIdDom(createWeId.getWeId());
        BaseBean.print(weIdDom);

        // registered authority issuer.
        demoService.registerAuthorityIssuer(
            createWeId, String.valueOf(System.currentTimeMillis()), "0");
        // recognize AuthorityIssuer
        demoService.recognizeAuthorityIssuer(createWeId);
        
        // registered CPT.
        CptBaseInfo cptResult =
            demoService.registCpt(
                createWeId,
                DemoTest.buildCptJsonSchema()
            );
        BaseBean.print(cptResult);

        // create Credential.
        String publicKeyId = demoService.getPublicKeyId(createWeId.getWeId());
        WeIdAuthentication weIdAuthentication = buildWeIdAuthority(createWeId, publicKeyId);
        CreateCredentialPojoArgs<Map<String, Object>>  createCredentialPojoArgs = 
            buildCreateCredentialPojoArgs(cptResult.getCptId(), weIdAuthentication);
        CredentialPojo credential = 
            demoService.createCredential(createCredentialPojoArgs);
        BaseBean.print(credential);

        // verify the credential.
        boolean result = demoService.verifyCredentialPojo(credential);
        if (result) {
            BaseBean.print("verify success");
        } else {
            BaseBean.print("verify fail");
        }
    }

    /**
     * build cpt json schema.
     * @return HashMap
     */
    public static HashMap<String, Object> buildCptJsonSchema() {

        HashMap<String, Object> cptJsonSchemaNew = new HashMap<String, Object>(3);
        cptJsonSchemaNew.put(JsonSchemaConstant.TITLE_KEY, "cpt template");
        cptJsonSchemaNew.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is a cpt template");

        HashMap<String, Object> propertitesMap1 = new HashMap<String, Object>(2);
        propertitesMap1.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
        propertitesMap1.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is name");

        String[] genderEnum = {"F", "M"};
        HashMap<String, Object> propertitesMap2 = new HashMap<String, Object>(2);
        propertitesMap2.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
        propertitesMap2.put(JsonSchemaConstant.DATA_TYPE_ENUM, genderEnum);

        HashMap<String, Object> propertitesMap3 = new HashMap<String, Object>(2);
        propertitesMap3.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_NUMBER);
        propertitesMap3.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is age");

        HashMap<String, Object> propertitesMap4 = new HashMap<String, Object>(2);
        propertitesMap4.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
        propertitesMap4.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is weid");

        HashMap<String, Object> cptJsonSchema = new HashMap<String, Object>(3);
        cptJsonSchema.put("name", propertitesMap1);
        cptJsonSchema.put("gender", propertitesMap2);
        cptJsonSchema.put("age", propertitesMap3);
        cptJsonSchema.put("weid", propertitesMap4);

        cptJsonSchemaNew.put(JsonSchemaConstant.PROPERTIES_KEY, cptJsonSchema);

        String[] genderRequired = {"name", "gender"};
        cptJsonSchemaNew.put(JsonSchemaConstant.REQUIRED_KEY, genderRequired);

        return cptJsonSchemaNew;
    }

    /**
     * build cpt json schemaData.
     * @return HashMap
     */
    public static HashMap<String, Object> buildCptJsonSchemaData() {

        HashMap<String, Object> cptJsonSchemaData = new HashMap<String, Object>(3);
        cptJsonSchemaData.put("name", "zhang san");
        cptJsonSchemaData.put("gender", "F");
        cptJsonSchemaData.put("age", 18);
        cptJsonSchemaData.put("weid", "did:weid:0x566a07b553804266133f130c8c0bf6fede406984");
        return cptJsonSchemaData;
    }
    
    /**
     * 构建创建凭证参数.
     * @param weIdAuthentication weId身份信息
     * @return 返回创建凭证参数
     */
    public static CreateCredentialPojoArgs<Map<String, Object>> buildCreateCredentialPojoArgs(
        Integer cptId,
        WeIdAuthentication weIdAuthentication
    ) {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            new CreateCredentialPojoArgs<Map<String, Object>>();

        createCredentialPojoArgs.setIssuer(weIdAuthentication.getWeId());
        createCredentialPojoArgs.setExpirationDate(
            System.currentTimeMillis() + (1000 * 60 * 60 * 24));
        createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);
        Map<String, Object> claimMap = new HashMap<String, Object>();
        claimMap.put("name", "zhang san");
        claimMap.put("gender", "F");
        claimMap.put("age", 23);
        claimMap.put("id", weIdAuthentication.getWeId());
        createCredentialPojoArgs.setClaim(claimMap);
        createCredentialPojoArgs.setType(CredentialType.ORIGINAL);
        createCredentialPojoArgs.setCptId(cptId);
        return createCredentialPojoArgs;
    }
    
    /**
     * build weId authority.
     */
    public static WeIdAuthentication buildWeIdAuthority(
        CreateWeIdDataResult createWeId, 
        String publicKeyId
    ) {
        return new WeIdAuthentication(
            createWeId.getWeId(), 
            createWeId.getUserWeIdPrivateKey().getPrivateKey(),
            publicKeyId
        );
    }
}
