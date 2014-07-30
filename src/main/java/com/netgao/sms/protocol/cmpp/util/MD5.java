package com.netgao.sms.protocol.cmpp.util;

import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-26
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class MD5 {
    public static byte[] md5(byte[] source){
        byte[] tmp = null;

        java.security.MessageDigest md;
        try {
            md = java.security.MessageDigest.getInstance( "MD5" );
            md.update(source);
            tmp = md.digest();          // MD5 的计算结果是一个 128 位的长整数， 用字节表示就是 16 个字节
        } catch (NoSuchAlgorithmException e) {
            tmp = null;
        }
        return tmp;
    }

}
