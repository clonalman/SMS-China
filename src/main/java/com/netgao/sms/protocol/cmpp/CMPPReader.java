package com.netgao.sms.protocol.cmpp;

import com.netgao.sms.protocol.Message;
import com.netgao.sms.protocol.Reader;
import com.netgao.sms.protocol.cmpp.message.*;
import com.netgao.sms.protocol.cmpp.util.ByteUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: gaudi.gao
 * Date: 14-6-16
 * Time: 下午5:20
 * To change this template use File | Settings | File Templates.
 */
public class CMPPReader implements Reader {

    protected DataInputStream in;

    public CMPPReader(InputStream is) {
        this.in = new DataInputStream(is);
    }

    @Override
    public Message read() throws IOException {
        byte[] header = new byte[CMPPBaseMessage.SZ_HEADER];
        byte[] cmdBytes = null;
        try {
            readBytes(header, 0, CMPPBaseMessage.SZ_HEADER);
            int cmdLen = ByteUtil.Bytes4ToInt(header);

            if (cmdLen > 8096 || cmdLen < CMPPBaseMessage.SZ_HEADER) {
                throw new IOException("read stream error,cmdLen="+ cmdLen + ",close connection");
            }
            cmdBytes = new byte[cmdLen];
            System.arraycopy(header, 0, cmdBytes, 0, CMPPBaseMessage.SZ_HEADER);
            readBytes(cmdBytes, CMPPBaseMessage.SZ_HEADER, cmdLen - CMPPBaseMessage.SZ_HEADER);
        } catch (IOException e) {
            throw e;
        }

        try {
            CMPPBaseMessage baseMsg = CMPPConstants.fromBytes(cmdBytes);
            return baseMsg;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("build CMPPBaseMessage error:"+e.getMessage());
        }
    }

    private void readBytes(byte[] bytes,int offset, int len) throws IOException{
        in.readFully(bytes, offset,  len);
    }
}
