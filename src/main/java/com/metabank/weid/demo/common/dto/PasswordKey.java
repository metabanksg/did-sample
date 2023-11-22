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

package com.metabank.weid.demo.common.dto;

/**
 * entity classes that store public and private keys.
 * 
 * @author v_wbgyang
 *
 */
public class PasswordKey {

    /**
     * private key.
     */
    private String privateKey;

    /**
     * public key.
     */
    private String publicKey;

    /**
     * get private key.
     * @return private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * set private key.
     * @param privateKey private key
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * get public key.
     * @return public key
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * set public key.
     * @param publicKey public key
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    } 
}
