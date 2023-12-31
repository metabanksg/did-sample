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

package com.metabank.weid.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.metabank.weid.constant.ErrorCode;
import com.metabank.weid.demo.common.model.AuthorityIssuerModel;
import com.metabank.weid.demo.common.model.RecognizeAuthorityIssuerModel;
import com.metabank.weid.demo.service.DemoService;
import com.metabank.weid.protocol.response.CreateWeIdDataResult;
import com.metabank.weid.protocol.response.ResponseData;

/**
 * Demo Controller.
 *
 * @author darwindu
 */
@RestController
@Api(description = "Committee Member: "
        + "委员会机构成员，管理Authority Issuer的委员会机构的成员",
    tags = {"CommitteeMember相关接口"})
public class DemoMemberController {

    private static final Logger logger = LoggerFactory.getLogger(DemoMemberController.class);

    @Autowired
    private DemoService demoService;


    /**
     * create weId without parameters and call the settings property method.
     *
     * @return returns weId and public key
     */
    @ApiOperation(value = "创建WeId")
    @PostMapping("/step1/member/createWeId")
    public ResponseData<CreateWeIdDataResult> createWeId() {
        return demoService.createWeId();
    }


    /**
     * registered on the chain of institutions as authoritative bodies.
     *
     * @return true is success, false is failure.
     */
    @ApiOperation(value = "注册成为权威机构")
    @PostMapping("/step2/registerAuthorityIssuer")
    public ResponseData<Boolean> registerAuthorityIssuer(
        @ApiParam(name = "authorityIssuerModel", value = "注册权威机构模板")
        @RequestBody AuthorityIssuerModel authorityIssuerModel) {

        if (null == authorityIssuerModel) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        String issuer = authorityIssuerModel.getIssuer();
        String authorityName = authorityIssuerModel.getOrgId();

        logger.info("param,issuer:{},orgId:{}", issuer, authorityName);

        // call method registered as authority.
        return demoService.registerAuthorityIssuer(issuer, authorityName);
    }

    /**
     * recognize the issuer on chain.
     *
     * @return true is success, false is failure.
     */
    @ApiOperation(value = "认证权威机构")
    @PostMapping("/step3/recognizeAuthorityIssuer")
    public ResponseData<Boolean> recognizeAuthorityIssuer(
        @ApiParam(name = "recognizeAuthorityIssuerModel", value = "认证权威机构模板")
        @RequestBody RecognizeAuthorityIssuerModel recognizeAuthorityIssuerModel) {
        
        if (null == recognizeAuthorityIssuerModel) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        String issuer = recognizeAuthorityIssuerModel.getIssuer();

        logger.info("param,issuer:{}", issuer);

        // call method recognize.
        return demoService.recognizeAuthorityIssuer(issuer);
    }
}
