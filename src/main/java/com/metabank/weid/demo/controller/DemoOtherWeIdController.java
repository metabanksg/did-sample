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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.metabank.weid.constant.ErrorCode;
import com.metabank.weid.demo.common.model.CreateWeIdModel;
import com.metabank.weid.demo.service.DemoService;
import com.metabank.weid.protocol.response.ResponseData;

/**
 * Demo Controller.
 *
 * @author darwindu
 */
@RestController
@Api(description = "WeId其他相关接口。",
    tags = {"其他相关接口-WeId"}, hidden = false)
public class DemoOtherWeIdController {

    @Autowired
    private DemoService demoService;

    /**
     * create weId without parameters and call the settings property method.
     *
     * @return returns weId and public key
     */
    @ApiOperation(value = "通过公私钥对创建WeId。")
    @PostMapping("/step1/createWeId")
    public ResponseData<String> createWeId(
        @ApiParam(name = "credentialModel", value = "公私钥对模板")
        @RequestBody CreateWeIdModel createWeIdModel) {

        if (null == createWeIdModel
            || StringUtils.isBlank(createWeIdModel.getPublicKey())
            || StringUtils.isBlank(createWeIdModel.getPrivateKey())) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        return demoService.createWeIdAndSetAttr(
            createWeIdModel.getPublicKey(),
            createWeIdModel.getPrivateKey());
    }
}
