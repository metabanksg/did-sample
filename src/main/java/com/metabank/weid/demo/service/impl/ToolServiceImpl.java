package com.metabank.weid.demo.service.impl;

import java.math.BigInteger;

import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.springframework.stereotype.Service;

import com.metabank.weid.constant.ErrorCode;
import com.metabank.weid.demo.service.ToolService;
import com.metabank.weid.protocol.response.ResponseData;

/**
 * 工具类实现.
 * @author darwindu
 * @date 2020/1/8
 **/
@Service
public class ToolServiceImpl implements ToolService {

    @Override
    public ResponseData<String> getPublicKey(String privateKey) {

        ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
        return new ResponseData<>(keyPair.getPublicKey().toString(), ErrorCode.SUCCESS);
    }
}
