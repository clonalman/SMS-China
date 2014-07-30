package com.netgao.sms.protocol.cmpp.message;

import com.netgao.sms.protocol.cmpp.util.ByteUtil;

/**
 * CMPP取消信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPCancelMessage  extends CMPPBaseMessage {

    private long msgId;              //信息标识

    public CMPPCancelMessage() {
        super(CMPPConstants.CMPP_CANCEL, 8);
    }


    @Override
    protected void setBody(byte[] bodyBytes) throws Exception {
        msgId = ByteUtil.Bytes8ToLong(bodyBytes);
        super.setBody(bodyBytes);
    }

    @Override
    protected byte[] getBody() throws Exception {
        byte[] bodyBytes = new byte[getCommandLength()];
        int offset = 0;
        ByteUtil.bytesCopy(ByteUtil.longToBytes8(msgId), bodyBytes, 0, 7, offset);
        offset += 8;
        return bodyBytes;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPCancelMessage:[sequenceId="+ sequenceString() +",");
        sb.append("msgId=" + msgId + "]");
        return sb.toString();
    }
}