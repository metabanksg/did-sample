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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.metabank.weid.demo.common.model.JsonTransportationSerializeModel;
import com.metabank.weid.demo.common.model.JsonTransportationSpecifyModel;
import com.metabank.weid.demo.service.DemoOtherService;
import com.metabank.weid.protocol.response.ResponseData;

/**
 * Demo Controller.
 *
 * @author darwindu
 */
@RestController
@Api(description = "传输其他相关接口。",
    tags = {"其他相关接口-Transportation"})
public class DemoOtherTransportationController {

    @Autowired
    private DemoOtherService demoOtherService;

    @ApiOperation(value = "指定transportation的认证者,用于权限控制。")
    @PostMapping("/step1/jsonTransportationSpecify")
    public ResponseData<String> specify(
        @ApiParam(name = "jsonTransportationSpecifyModel", value = "指定transportation的认证者模板")
        @RequestBody JsonTransportationSpecifyModel jsonTransportationSpecifyModel) {
        return demoOtherService.specify(jsonTransportationSpecifyModel);
    }

    @ApiOperation(value = "用于序列化对象,要求对象实现JsonSerializer接口。")
    @PostMapping("/step2/jsonTransportationSerialize")
    public ResponseData<String> serialize(
        @ApiParam(name = "jsonTransportationSerializeModel",
            value = "用于序列化对象,要求对象实现JsonSerializer模板")
        @RequestBody JsonTransportationSerializeModel jsonTransportationSerializeModel) {
        return demoOtherService.serialize(jsonTransportationSerializeModel);
    }
}
