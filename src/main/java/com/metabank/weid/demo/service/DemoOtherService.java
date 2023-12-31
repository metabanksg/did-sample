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

package com.metabank.weid.demo.service;

import com.metabank.weid.demo.common.model.CreateCredentialPojoModel;
import com.metabank.weid.demo.common.model.CreateEvidenceModel;
import com.metabank.weid.demo.common.model.CreatePresentationModel;
import com.metabank.weid.demo.common.model.CreatePresentationPolicyEModel;
import com.metabank.weid.demo.common.model.CreateSelectiveCredentialModel;
import com.metabank.weid.demo.common.model.CredentialPoJoAddSignature;
import com.metabank.weid.demo.common.model.GetCredentialHashModel;
import com.metabank.weid.demo.common.model.JsonTransportationSerializeModel;
import com.metabank.weid.demo.common.model.JsonTransportationSpecifyModel;
import com.metabank.weid.demo.common.model.VerifyCredentialModel;
import com.metabank.weid.demo.common.model.VerifyCredentialPoJoModel;
import com.metabank.weid.protocol.base.CredentialPojo;
import com.metabank.weid.protocol.base.EvidenceInfo;
import com.metabank.weid.protocol.base.PresentationE;
import com.metabank.weid.protocol.base.PresentationPolicyE;
import com.metabank.weid.protocol.response.ResponseData;

/**
 * demo interface.
 * 
 * @author v_wbgyang
 *
 */
public interface DemoOtherService {

    /**
     * get credential hash.
     *
     * @param verifyCredentialModel 验证电子凭证模板
     * @return returns the create hash
     */
    ResponseData<String> getCredentialHash(VerifyCredentialModel verifyCredentialModel);

    /**
     * 创建电子凭证.
     * @param createCredentialPojoModel 创建电子凭证模板
     * @return
     */
    ResponseData<CredentialPojo> createCredentialPoJo(
        CreateCredentialPojoModel createCredentialPojoModel);


    /**
     * 通过原始凭证和披漏策略，创建选择性披露的Credential.
     * @param createSelectiveCredentialModel 创造选择性披露模板
     * @return
     */
    ResponseData<CredentialPojo> createSelectiveCredential(
        CreateSelectiveCredentialModel createSelectiveCredentialModel);

    /**
     * 验证credential.
     * @param verifyCredentialPoJoModel 验证电子凭证模板
     * @return
     */
    ResponseData<Boolean> verify(VerifyCredentialPoJoModel verifyCredentialPoJoModel);


    /**
     * 创建PresentationPolicyEModel.
     * @param createPresentationPolicyEModel 创建PresentationPolicy模板
     * @return
     */
    ResponseData<PresentationPolicyE> createPresentationPolicyE(
        CreatePresentationPolicyEModel createPresentationPolicyEModel);


    /**
     * 获取电子凭证hash.
     * @param getCredentialHashModel 获取电子凭证hash模板
     * @return
     */
    ResponseData<String> getCredentialPoJoHash(GetCredentialHashModel getCredentialHashModel);

    /**
     * 创建Presentation.
     * @param createPresentationModel 创建Presentation模板
     * @return
     */
    ResponseData<PresentationE> createPresentation(
        CreatePresentationModel createPresentationModel);

    /**
     * 多签，在原凭证列表的基础上，创建包裹成一个新的多签凭证，由传入的私钥所签名。此凭证的CPT为一个固定值.
     * 在验证一个多签凭证时，会迭代验证其包裹的所有子凭证。本接口不支持创建选择性披露的多签凭证.
     *
     * @param credentialPoJoAddSignature 电子凭证加签模板
     * @return
     */
    ResponseData<CredentialPojo> addSignatureCredentialPojo(
        CredentialPoJoAddSignature credentialPoJoAddSignature);

    /**
     * 创建存证.
     * @param createEvidenceModel 创建存证模板
     * @return
     */
    ResponseData<String> createEvidence(CreateEvidenceModel createEvidenceModel);


    /**
     * 根据传入的凭证存证地址，在链上查找凭证存证信息.
     * @param evidenceAddress 存证地址
     * @return
     */
    ResponseData<EvidenceInfo> getEvidence(String evidenceAddress);

    /**
     * 指定transportation的认证者,用于权限控制.
     * @param jsonTransportationSpecifyModel 参数模板
     * @return
     */
    ResponseData<String> specify(JsonTransportationSpecifyModel jsonTransportationSpecifyModel);


    /**
     * 用于序列化对象,要求对象实现JsonSerializer接口.
     * @param jsonTransportationSerializeModel 参数模板
     * @return
     */
    ResponseData<String> serialize(
        JsonTransportationSerializeModel jsonTransportationSerializeModel);
}
