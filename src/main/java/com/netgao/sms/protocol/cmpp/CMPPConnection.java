package com.netgao.sms.protocol.cmpp;

import com.netgao.sms.protocol.Connection;
import com.netgao.sms.protocol.Reader;
import com.netgao.sms.protocol.Session;
import com.netgao.sms.protocol.Writer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public class CMPPConnection  extends Connection {

    public static final byte MT=0;
    public static final byte MO=1;
    public static final byte MT_MO=2;

    private String sourceAddr;
    private String password;
    private byte loginMode;
    private byte version;

    public CMPPConnection(){
        super();
        setLoginMode(MT_MO);
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String value) {
        this.sourceAddr = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(byte loginMode) {
        this.loginMode = loginMode;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    @Override
    protected Session createSession() {
        return  new CMPPSession(this, false);
    }

    @Override
    protected Writer createWriter(OutputStream output) {
        return new CMPPWriter(output);
    }

    @Override
    protected Reader createReader(InputStream input) {
        return new CMPPReader(input);
    }

    @Override
    public String toString(){
        StringBuffer buffer=new StringBuffer();
        buffer.append("cmpp:[sourceAddr=").append(sourceAddr).append(",")
                .append("host=").append(getHost()).append(",")
                .append("port=").append(getPort()).append(",")
                .append("password=").append(password).append(",")
                .append("loginMode=").append(loginMode).append("]");
        return buffer.toString();
    }
}