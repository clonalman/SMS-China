package com.netgao.sms.protocol.cmpp.message;

import com.netgao.sms.protocol.cmpp.util.ByteUtil;
import com.netgao.sms.protocol.cmpp.util.MD5;

import java.util.Arrays;

/**
 * 连接回复信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPConnectRespMessage extends CMPPBaseMessage {

    private  int    status              = 0;
    private  byte[] authenticatorISMG   = null;
    private  int    version = CMPPConstants.CMPP_VERSION;

    private  byte[] authenticatorSource = null;
    private  String sharedSecret        = null;

    public CMPPConnectRespMessage() {
        super(CMPPConstants.CMPP_CONNECT_RESP, 18);
    }

    /**
     * getters
     * @return
     */
    public int getStatus()
    {
        return status;
    }
    public byte[] getAuthenticatorISMG()
    {
        return authenticatorISMG;
    }
    public int getVersion()
    {
        return version;
    }

    /**
     * setters
     * @param status
     */
    public void setStatus(int status)  {
        this.status = status;
    }
    public void setAuthenticatorSource(byte[] authenticatorSource) {
        this.authenticatorSource = authenticatorSource;
    }
    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public boolean checkSharedSecret(byte[] authenticatorSource, String localSecret)
    {
        byte[] localAuthenticator = getAuthenticator(status, authenticatorSource, localSecret);
        if(Arrays.equals(localAuthenticator,authenticatorISMG)) {
            return true;
        } else {
            return false;
        }
    }

    protected void setBody(byte[] bodyBytes) {

        int off = 0;
        //status
        status = bodyBytes[off];
        off += 1;
        //authenticatorISMG
        authenticatorISMG = new byte[16];
        ByteUtil.bytesCopy(bodyBytes, authenticatorISMG, off, off + 15, 0);
        off += 16;
        //version
        version = bodyBytes[off];
        off += 1;

    }

    /**
     *
     */
    protected byte[] getBody() {

        // make bodybytes
        int bodyLength = getCommandLength();
        byte[] bodyBytes  = new byte[bodyLength];
        Arrays.fill(bodyBytes,(byte)0);

        //make authenticatorSource
        if( authenticatorSource == null ) {
            authenticatorSource = new byte[0];
        }
        //make sharedSecret
        if( sharedSecret == null ){
            sharedSecret = "";
        }
        //make authenticatorISMG
        authenticatorISMG = getAuthenticator(status, authenticatorSource, sharedSecret);

        // make body
        int off = 0;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(status), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtil.bytesCopy(authenticatorISMG, bodyBytes, 0, 15, off);
        off += 16;
        bodyBytes[off] = ByteUtil.intToByte(version);
        off += 1;

        return bodyBytes;
    }

    /**
     *
     * @param status
     * @param authenticatorSource
     * @param secret
     * @return
     */
    private byte[] getAuthenticator(int status, byte[] authenticatorSource, String secret) {

        if( authenticatorSource == null )
            authenticatorSource =  new byte[0];
        if( secret == null )
            secret = "";

        byte[] buffer = new byte[ 1 + authenticatorSource.length + secret.length()];
        Arrays.fill(buffer, (byte)0);

        int off =  0;
        buffer[off] = ByteUtil.intToByte(status);
        off += 1;
        ByteUtil.bytesCopy(authenticatorSource, buffer, 0, authenticatorSource.length - 1, off);
        off += authenticatorSource.length;
        ByteUtil.bytesCopy(secret.getBytes(), buffer, 0, secret.length() - 1, off);
        off += secret.length();

        return MD5.md5(buffer);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPConnectRespMessage:[sequenceId="+ sequenceString() +",");
        sb.append("status=" + status + ",");
        sb.append("authenticatorISMG=" + authenticatorISMG + ",");
        sb.append("version=" + version + "]");
        return sb.toString();
    }
}