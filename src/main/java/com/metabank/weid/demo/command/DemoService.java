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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metabank.weid.constant.ErrorCode;
import com.metabank.weid.demo.exception.BusinessException;
import com.metabank.weid.protocol.base.AuthorityIssuer;
import com.metabank.weid.protocol.base.Challenge;
import com.metabank.weid.protocol.base.CptBaseInfo;
import com.metabank.weid.protocol.base.CredentialPojo;
import com.metabank.weid.protocol.base.PresentationE;
import com.metabank.weid.protocol.base.PresentationPolicyE;
import com.metabank.weid.protocol.base.PublicKeyProperty;
import com.metabank.weid.protocol.base.WeIdAuthentication;
import com.metabank.weid.protocol.base.WeIdDocument;
import com.metabank.weid.protocol.base.WeIdPrivateKey;
import com.metabank.weid.protocol.request.AuthenticationArgs;
import com.metabank.weid.protocol.request.CptMapArgs;
import com.metabank.weid.protocol.request.CptStringArgs;
import com.metabank.weid.protocol.request.CreateCredentialPojoArgs;
import com.metabank.weid.protocol.request.PublicKeyArgs;
import com.metabank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.metabank.weid.protocol.request.ServiceArgs;
import com.metabank.weid.protocol.response.CreateWeIdDataResult;
import com.metabank.weid.protocol.response.ResponseData;
import com.metabank.weid.rpc.AuthorityIssuerService;
import com.metabank.weid.rpc.CptService;
import com.metabank.weid.rpc.CredentialPojoService;
import com.metabank.weid.rpc.WeIdService;
import com.metabank.weid.service.impl.AuthorityIssuerServiceImpl;
import com.metabank.weid.service.impl.CptServiceImpl;
import com.metabank.weid.service.impl.CredentialPojoServiceImpl;
import com.metabank.weid.service.impl.WeIdServiceImpl;
import com.metabank.weid.suite.api.transportation.TransportationFactory;
import com.metabank.weid.suite.api.transportation.params.EncodeType;
import com.metabank.weid.suite.api.transportation.params.ProtocolProperty;

/**
 * the service for command.
 * @author v_wbgyang
 *
 */
public class DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    private AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();

    private CptService cptService = new CptServiceImpl();

    private WeIdService weIdService = new WeIdServiceImpl();
    
    private CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();

    /**
     * Create a WeIdentity DID with null input param.
     *
     * @return the result of createWeId
     */
    public CreateWeIdDataResult createWeId() throws BusinessException {

        // create WeIdentity DID,publicKey,privateKey
        ResponseData<CreateWeIdDataResult> responseCreate = weIdService.createWeId();
        BaseBean.print("createWeId result:");
        BaseBean.print(responseCreate);

        // throw an exception if it does not succeed.
        if (responseCreate.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("failed to call createWeId method, code={}, message={}",
                responseCreate.getErrorCode(),
                responseCreate.getErrorMessage()
            );
            throw new BusinessException(responseCreate.getErrorMessage());
        }
        return responseCreate.getResult();
    }

    /**
     * Set Public Key For WeIdentity DID Document.
     *
     * @param createResult the Object of CreateWeIdDataResult
     */
    public void addPublicKey(CreateWeIdDataResult createResult)
        throws BusinessException {

        PublicKeyArgs publicKeyArgs = new PublicKeyArgs();
        publicKeyArgs.setPublicKey(createResult.getUserWeIdPublicKey().getPublicKey());
        ResponseData<Integer> responseSetPub = weIdService.addPublicKey(
                createResult.getWeId(), publicKeyArgs, createResult.getUserWeIdPrivateKey());

        BaseBean.print("setPublicKey result:");
        BaseBean.print(responseSetPub);

        if (responseSetPub.getErrorCode() != ErrorCode.SUCCESS.getCode()
            && responseSetPub.getErrorCode() 
                == ErrorCode.WEID_PUBLIC_KEY_ALREADY_EXISTS.getCode()) {
            logger.info("the publicKey already exist!");
        } else if (responseSetPub.getErrorCode() != ErrorCode.SUCCESS.getCode()
                || responseSetPub.getResult() == -1) {
            logger.error("failed to call setPublicKey method, code={}, message={}",
                    responseSetPub.getErrorCode(),
                    responseSetPub.getErrorMessage()
            );
            throw new BusinessException(responseSetPub.getErrorMessage());
        }
    }

    /**
     * Set Service For WeIdentity DID Document.
     *
     * @param createResult the Object of CreateWeIdDataResult
     * @param serviceType the set service args
     * @param serviceEnpoint the set service args
     */
    public void setService(
        CreateWeIdDataResult createResult,
        String serviceType,
        String serviceEnpoint)
        throws BusinessException {

        ServiceArgs serviceArgs = new ServiceArgs();
        serviceArgs.setType(serviceType);
        serviceArgs.setServiceEndpoint(serviceEnpoint);

        ResponseData<Boolean> responseSetSer = weIdService.setService(
                createResult.getWeId(),
                serviceArgs,
                createResult.getUserWeIdPrivateKey());

        BaseBean.print("setService result:");
        BaseBean.print(responseSetSer);

        // throw an exception if it does not succeed.
        if (responseSetSer.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetSer.getResult()) {
            logger.error("failed to call setService method, code={}, message={}",
                responseSetSer.getErrorCode(),
                responseSetSer.getErrorMessage()
            );
            throw new BusinessException(responseSetSer.getErrorMessage());
        }
    }

    /**
     * Set Authentication for WeIdentity DID Document.
     *
     * @param createResult the Object of CreateWeIdDataResult
     */
    public void setAuthentication(CreateWeIdDataResult createResult)
        throws BusinessException {

        AuthenticationArgs authenticationArgs = new AuthenticationArgs();
        authenticationArgs.setPublicKey(createResult.getUserWeIdPublicKey().getPublicKey());

        ResponseData<Boolean> responseSetAuth = weIdService.setAuthentication(
                createResult.getWeId(),
                authenticationArgs,
                createResult.getUserWeIdPrivateKey());

        BaseBean.print("setAuthentication result:");
        BaseBean.print(responseSetAuth);

        // throw an exception if it does not succeed.
        if (responseSetAuth.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetAuth.getResult()) {
            logger.error("failed to call setAuthentication method, code={}, message={}",
                responseSetAuth.getErrorCode(),
                responseSetAuth.getErrorMessage()
            );
            throw new BusinessException(responseSetAuth.getErrorMessage());
        }
    }

    /**
     * Build WeIdPrivateKey object.
     * 
     * @param privateKey private key
     * @return return WeIdPrivateKey object
     */
    private WeIdPrivateKey buildWeIdPrivateKey(String privateKey) {
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(privateKey);
        return weIdPrivateKey;
    }

    /**
     * Get a WeIdentity DID Document.
     *
     * @param weId the WeIdentity DID
     * @return the WeIdentity DID document
     */
    public WeIdDocument getWeIdDom(String weId) throws BusinessException {

        // getting weIdDOM by weId on chain.
        ResponseData<WeIdDocument> responseResult = weIdService.getWeIdDocument(weId);
        BaseBean.print("getWeIdDocument result:");
        BaseBean.print(responseResult);

        // throw an exception if it does not succeed.
        if (responseResult.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || null == responseResult.getResult()) {
            logger.error("failed to call getWeIdDocument method, code={}, message={}",
                responseResult.getErrorCode(),
                responseResult.getErrorMessage()
            );
            throw new BusinessException(responseResult.getErrorMessage());
        }
        return responseResult.getResult();
    }

    /**
     * register a new CPT on chain.
     * 
     * @param weIdResult weId information containing private keys
     * @param cptJsonSchema the String data is a JSON schema for registered CPT
     * @return the data of CPT Base Information
     * @throws BusinessException throw a exception when register fail
     */
    public CptBaseInfo registCpt(
        CreateWeIdDataResult weIdResult, 
        String cptJsonSchema, 
        Integer cptId
    ) throws BusinessException {

        logger.info("regist CPT with CptStringArgs");

        // build CptStringArgs for registerCpt.
        CptStringArgs cptStringArgs = new CptStringArgs();
        cptStringArgs.setWeIdAuthentication(this.buildWeIdAuthentication(weIdResult));
        cptStringArgs.setCptJsonSchema(cptJsonSchema);

        // call the registerCpt on chain.
        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptStringArgs, cptId);
        BaseBean.print("registerCpt result:");
        BaseBean.print(response);

        // throw an exception if it does not succeed.
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode() 
            || null == response.getResult()) {

            if (response.getErrorCode() == ErrorCode.CPT_ALREADY_EXIST.getCode()) {
                logger.info("The cpt is already register");
            } else {
                logger.error("failed to call registerCpt method, code={}, message={}",
                    response.getErrorCode(),
                    response.getErrorMessage()
                );
                throw new BusinessException(response.getErrorMessage());
            }
        }
        return response.getResult();
    }

    /**
     * register a new CPT on chain.
     * 
     * @param weIdResult weId information containing private keys
     * @param cptJsonSchemaMap schema of map type
     * @return return CPT information
     * @throws BusinessException throw a exception when register fail
     */
    public CptBaseInfo registCpt(
        CreateWeIdDataResult weIdResult,
        Map<String,Object> cptJsonSchemaMap)
        throws BusinessException {

        logger.info("regist CPT with CptMapArgs");

        // build CptMapArgs for registerCpt.
        CptMapArgs cptMapArgs = new CptMapArgs();
        cptMapArgs.setWeIdAuthentication(this.buildWeIdAuthentication(weIdResult));
        cptMapArgs.setCptJsonSchema(cptJsonSchemaMap);

        // call the registerCpt on chain.
        ResponseData<CptBaseInfo> responseMap = cptService.registerCpt(cptMapArgs);
        BaseBean.print("registerCpt result:");
        BaseBean.print(responseMap);

        // throw an exception if it does not succeed.
        if (responseMap.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || null == responseMap.getResult()) {
            logger.error("failed to call registerCpt method, code={}, message={}",
                responseMap.getErrorCode(),
                responseMap.getErrorMessage()
            );
            throw new BusinessException(responseMap.getErrorMessage());
        }
        return responseMap.getResult();
    }

    /**
     * build WeIdAuthentication object by CreateWeIdDataResult object.
     * 
     * @param weIdResult CreateWeIdDataResult object
     * @return return WeIdAuthentication object
     */
    private WeIdAuthentication buildWeIdAuthentication(CreateWeIdDataResult weIdResult) {
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(weIdResult.getWeId());
        weIdAuthentication.setWeIdPrivateKey(
            this.buildWeIdPrivateKey(weIdResult.getUserWeIdPrivateKey().getPrivateKey())
        );
        return weIdAuthentication;
    }

    /**
     * Register a new Authority Issuer on Chain.
     * 
     * @param weIdResult the object of CreateWeIdDataResult
     * @param name this is Authority name
     * @param accValue this is accValue
     * @throws BusinessException throw a exception when register fail
     */
    public void registerAuthorityIssuer(
        CreateWeIdDataResult weIdResult,
        String name,
        String accValue)
        throws BusinessException {

        // build AuthorityIssuer object.
        AuthorityIssuer authorityIssuerResult = new AuthorityIssuer();
        authorityIssuerResult.setWeId(weIdResult.getWeId());
        authorityIssuerResult.setName(name);
        authorityIssuerResult.setAccValue(accValue);

        // build RegisterAuthorityIssuerArgs for registerAuthorityIssuer.
        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new RegisterAuthorityIssuerArgs();
        registerAuthorityIssuerArgs.setAuthorityIssuer(authorityIssuerResult);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(
            this.buildWeIdPrivateKey(DemoUtil.SDK_PRIVATE_KEY)
        );

        // call the registerAuthorityIssuer on chain.
        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        BaseBean.print("registerAuthorityIssuer result:");
        BaseBean.print(response);

        // throw an exception if it does not succeed.
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode() 
            || !response.getResult()) {
            logger.error("failed to call registerAuthorityIssuer method, code={}, message={}",
                response.getErrorCode(),
                response.getErrorMessage()
            );
            throw new BusinessException(response.getErrorMessage());
        }
    }
    
    /**
     * Register a new Authority Issuer on Chain.
     * 
     * @param weIdResult the object of CreateWeIdDataResult
     * @throws BusinessException throw a exception when register fail
     */
    public void recognizeAuthorityIssuer(CreateWeIdDataResult weIdResult) throws BusinessException {
        WeIdPrivateKey weIdPrivateKey = this.buildWeIdPrivateKey(DemoUtil.SDK_PRIVATE_KEY);
        // call the registerAuthorityIssuer on chain.
        ResponseData<Boolean> response =
            authorityIssuerService.recognizeAuthorityIssuer(weIdResult.getWeId(), weIdPrivateKey);
        BaseBean.print("recognizeAuthorityIssuer result:");
        BaseBean.print(response);

        // throw an exception if it does not succeed.
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode() 
            || !response.getResult()) {
            logger.error("failed to call recognizeAuthorityIssuer method, code={}, message={}",
                response.getErrorCode(),
                response.getErrorMessage()
            );
            throw new BusinessException(response.getErrorMessage());
        }
    }

    /**
     * create a credential for user.
     * @param arg the CreateCredentialPojoArgs
     * @return
     */
    public <T> CredentialPojo createCredential(CreateCredentialPojoArgs<T> arg) {
        ResponseData<CredentialPojo> response = 
                credentialPojoService.createCredential(arg);
        BaseBean.print("createCredential result:");
        BaseBean.print(response);
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("credentialPojoService.createCredential failed,error:{}",response);
            throw new BusinessException(response.getErrorMessage());
        }
        return response.getResult();
    }
    
    /**
     *  create presentation with policy.
     * @param credentialList the credentialList of user
     * @param presentationPolicyE the policy for create selective credential 
     * @param challenge the challenge form verifier
     * @param createWeId the weId information of user 
     * @return the object of PresentationE 
     */
    public PresentationE createPresentation(
        List<CredentialPojo> credentialList,
        PresentationPolicyE presentationPolicyE,
        Challenge challenge,
        CreateWeIdDataResult createWeId) {
        
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeId.getWeId());
        weIdAuthentication.setWeIdPublicKeyId(createWeId.getWeId() + "#keys-0");
        weIdAuthentication.setWeIdPrivateKey(createWeId.getUserWeIdPrivateKey());
        ResponseData<PresentationE> response = 
            credentialPojoService.createPresentation(
                credentialList, 
                presentationPolicyE, 
                challenge, 
                weIdAuthentication
            );
        BaseBean.print("createPresentation result:");
        BaseBean.print(response);
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("credentialPojoService.createPresentation failed,error:{}",response);
            throw new BusinessException(response.getErrorMessage());
        }
        return response.getResult();
    }
    
    /**
     * serialize presentation to String by JsonTransportation.
     * @param weIds specifyWeIds to verifier
     * @param presentationE the presentationE
     * @return serialize string
     */
    public String presentationEToJson(List<String> weIds, PresentationE presentationE) {
        
        ResponseData<String> response = 
            TransportationFactory
                .newJsonTransportation()
                .specify(weIds)
                .serialize(presentationE,new ProtocolProperty(EncodeType.ORIGINAL));
        BaseBean.print(response);
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "jsonTransportation.serialize failed,responseData:{}",
                response
            );
            throw new BusinessException(response.getErrorMessage());
        }
        return response.getResult();
    }
    
    /**
     * serialize presentation to String by QrCodeTransportation.
     * @param weIds specifyWeIds to verifier
     * @param presentationE the presentationE
     * @return serialize string
     */
    public String presentationEToQrCode(List<String> weIds, PresentationE presentationE) {
        
        ResponseData<String> response = 
            TransportationFactory
                .newQrCodeTransportation()
                .specify(weIds)
                .serialize(presentationE,new ProtocolProperty(EncodeType.ORIGINAL));
        BaseBean.print(response);
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "QRCodeTransportation.serialize failed,responseData:{}",
                response
            );
            throw new BusinessException(response.getErrorMessage());
        }
        return response.getResult();
    }
    
    /**
     * deserialize transString by JsonTransportation.
     * @param presentationJson JSON String
     * @param createWeId the weId information of verifier 
     * @return PresentationE
     */
    public PresentationE deserializePresentationJson(
        String presentationJson, 
        CreateWeIdDataResult createWeId) {
        
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeId.getWeId());
        weIdAuthentication.setWeIdPublicKeyId(createWeId.getWeId() + "#keys-0");
        weIdAuthentication.setWeIdPrivateKey(createWeId.getUserWeIdPrivateKey());
        
        ResponseData<PresentationE> response = 
            TransportationFactory
                .newJsonTransportation()
                .deserialize(weIdAuthentication, presentationJson, PresentationE.class);
        BaseBean.print("JsonTransportation.deserialize result:");
        BaseBean.print(response);
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("jsonTransportation.deserialize failed,responseData:{}",
                response);
            throw new BusinessException(response.getErrorMessage());
        }
        return response.getResult();
    }
    
    /**
     * deserialize transString by QrCodeTransportation.
     * @param presentationJson QrCode String
     * @param createWeId the weId information of verifier
     * @return PresentationE
     */
    public PresentationE deserializePresentationQrCode(
        String presentationJson, 
        CreateWeIdDataResult createWeId) {
        
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeId.getWeId());
        weIdAuthentication.setWeIdPublicKeyId(createWeId.getWeId() + "#keys-0");
        weIdAuthentication.setWeIdPrivateKey(createWeId.getUserWeIdPrivateKey());
        
        ResponseData<PresentationE> response = 
            TransportationFactory
                .newQrCodeTransportation()
                .deserialize(weIdAuthentication, presentationJson, PresentationE.class);
        BaseBean.print("QrCodeTransportation.deserialize result:");
        BaseBean.print(response);
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("QrCodeTransportation.deserialize failed,responseData:{}",
                response);
            throw new BusinessException(response.getErrorMessage());
        }
        return response.getResult();
    }
    
    /**
     * verify the presentation.
     * @param weId the weId of userAgent
     * @param presentationPolicyE the policy of verifier
     * @param challenge the challenge of verifier
     * @param presentationE the presentation of userAgent
     * @return success if true, others fail
     */
    public boolean verifyPresentationE(
        String weId, 
        PresentationPolicyE presentationPolicyE, 
        Challenge challenge,
        PresentationE presentationE) {
        
        ResponseData<Boolean> response = 
            credentialPojoService.verify(weId, presentationPolicyE, challenge, presentationE);
        BaseBean.print("verify result:");
        BaseBean.print(response);
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("credentialPojoService.verify failed,responseData:{}",
                response);
            throw new BusinessException(response.getErrorMessage());
        }
        return response.getResult();
    }
    
    /**
     *  verify CredentialPojo. 
     * @param credentialPojo the CredentialPojo
     * @return success if true, others fail
     */
    public boolean verifyCredentialPojo(CredentialPojo credentialPojo) {
        ResponseData<Boolean> verify = 
            credentialPojoService.verify(credentialPojo.getIssuer(), credentialPojo);
        if (verify.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("credentialPojoService.verify failed,responseData:{}",
                    verify);
            throw new BusinessException(verify.getErrorMessage());
        }
        return verify.getResult();
    }
    
    /**
     * get the public key ID.
     * @param weId the weId
     * @return the publicKeyId
     */
    public String getPublicKeyId(String weId) {
        ResponseData<WeIdDocument> weIdDocumentRes = weIdService.getWeIdDocument(weId);
        String publicKeyId = null;
        for (PublicKeyProperty publicKey : weIdDocumentRes.getResult().getPublicKey()) {
            if (publicKey.getOwner().equals(weId)) {
                publicKeyId = publicKey.getId();
                break;
            }
        }
        return publicKeyId;
    }
}
