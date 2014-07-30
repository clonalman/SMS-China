package com.netgao.sms.protocol.cmpp.message;

import com.netgao.sms.protocol.cmpp.util.ByteUtil;

/**
 * 取消返回信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPCancelRespMessage extends CMPPBaseMessage {

    private int successId;                 //成功标识 0：成功 1：失败

    public CMPPCancelRespMessage() {
        super(CMPPConstants.CMPP_CANCEL_RESP, 1);
    }

    public int getSuccessId() {
        return successId;
    }

    public void setSuccessId(int successId) {
        this.successId = successId;
    }

    @Override
    protected void setBody(byte[] bodyBytes) throws Exception {
        int offset = 0;
        successId = ByteUtil.byteToInt(bodyBytes[offset]);
        offset += 1;
        super.setBody(bodyBytes);
    }

    @Override
    protected byte[] getBody() throws Exception {
        byte[] bodyBytes = new byte[getCommandLength()];
        int offset = 0;
        bodyBytes[offset] = ByteUtil.intToByte(successId);
        offset += 1;
        return bodyBytes;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPCancelRespMessage:[sequenceId="+ sequenceString() +",");
        sb.append("successId=" + successId + "]");
        return sb.toString();
    }
}