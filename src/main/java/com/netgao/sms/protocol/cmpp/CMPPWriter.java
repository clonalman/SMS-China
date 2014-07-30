package com.netgao.sms.protocol.cmpp;

import com.netgao.sms.protocol.Message;
import com.netgao.sms.protocol.Writer;
import com.netgao.sms.protocol.cmpp.message.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: gaudi.gao
 * Date: 14-6-17
 * Time: 下午12:50
 * To change this template use File | Settings | File Templates.
 */
public class CMPPWriter implements Writer {

    protected DataOutputStream out;

    public CMPPWriter(OutputStream os) {
        this.out = new DataOutputStream(os);
    }

    @Override
    public void write(Message message) throws IOException {
        if(message instanceof CMPPBaseMessage){
            byte[] bytes = null;
            try {
                bytes = ((CMPPBaseMessage) message).toBytes();
            } catch (Exception ex){ }
            if(bytes != null){
                writeBytes(bytes);
            }
        }
    }

    private void writeBytes(byte[] bytes) throws IOException {
        out.write(bytes);
        out.flush();
    }
}
