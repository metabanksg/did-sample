/*
 *       Copyright© (2019-2020) metabank Co., Ltd.
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

import com.metabank.weid.protocol.base.CredentialPojo;
import com.metabank.weid.protocol.base.CredentialPojoList;
import com.metabank.weid.protocol.base.WeIdAuthentication;
import com.metabank.weid.protocol.base.WeIdPrivateKey;
import com.metabank.weid.protocol.response.CreateWeIdDataResult;
import com.metabank.weid.protocol.response.ResponseData;
import com.metabank.weid.rpc.CredentialPojoService;
import com.metabank.weid.rpc.WeIdService;
import com.metabank.weid.service.impl.CredentialPojoServiceImpl;
import com.metabank.weid.service.impl.WeIdServiceImpl;
import com.metabank.weid.suite.api.transportation.TransportationFactory;
import com.metabank.weid.suite.api.transportation.params.TransportationType;

public class TransportationVerifier {

    /**
     * verifier程序入口.
     * @param args 参数
     */
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        // TODO Auto-generated method stub
        // 创建weid
        WeIdService weidService = new WeIdServiceImpl();
        CreateWeIdDataResult result = weidService.createWeId().getResult();
        System.out.println(result);
        // verifier WeId信息
        String weId = "did:weid:101:0xa7af4461084b76aecfd9361a49e21cc34e6c4074";
        WeIdPrivateKey privateKey = new WeIdPrivateKey();
        privateKey.setPrivateKey(
            "89119831904759961853601218812211426488644695651785968136169259389312366556150");
        
        // 构造verifier的身份信息
        WeIdAuthentication weIdAuthentication = 
            new WeIdAuthentication(weId, privateKey.getPrivateKey());

        ResponseData<CredentialPojoList> deserialize = 
            TransportationFactory.build(TransportationType.QR_CODE)
                .deserialize(
                      weIdAuthentication, 
                     "2|1000orgB|993ecb3ffdbd47e5a3e2e7a798060ef1",//issuer通过serialize生成的条码编码
                     CredentialPojoList.class
                 );
        System.out.println("根据条码编码获取到的凭证:");
        System.out.println(deserialize);
        
        // 验证获取到凭证
        CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
        CredentialPojoList credentialPojoList = deserialize.getResult();
        for (CredentialPojo credentialPojo2 : credentialPojoList) {
            ResponseData<Boolean> verify = 
                credentialPojoService.verify(credentialPojo2.getIssuer(), credentialPojo2);
            System.out.println("凭证验证结果:");
            System.out.println(verify);
        }
    }
}
