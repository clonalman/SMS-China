package com.netgao.sms.protocol.cmpp.message;

import com.netgao.sms.protocol.cmpp.util.ByteUtil;

/**
 * CMPP测试返回信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPActiveTestRespMessage extends CMPPBaseMessage {

    private int  reserved = 0;

    public CMPPActiveTestRespMessage() {
        super(CMPPConstants.CMPP_ACTIVE_TEST_RESP, 1);
    }

    public int  getReserved() {
        return reserved;
    }

    public void setReserved(int  reserved) {
        this.reserved = reserved;
    }

    @Override
    protected void setBody(byte[] bodyBytes) throws Exception {
        int offset = 0;
        reserved = ByteUtil.byteToInt(bodyBytes[offset]);
        offset += 1;
        super.setBody(bodyBytes);
    }

    @Override
    protected byte[] getBody() throws Exception {
        byte[] bodyBytes = new byte[getCommandLength()];
        int offset = 0;
        bodyBytes[offset] = ByteUtil.intToByte(reserved);
        offset += 1;
        return bodyBytes;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPActiveTestRespMessage:[sequenceId="+ sequenceString() +",");
        sb.append("reserved=" + reserved + "]");
        return sb.toString();
    }
}