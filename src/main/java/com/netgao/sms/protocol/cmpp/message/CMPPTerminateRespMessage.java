package com.netgao.sms.protocol.cmpp.message;

/**
 * Created with IntelliJ IDEA.
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午4:12
 * To change this template use File | Settings | File Templates.
 */
public class CMPPTerminateRespMessage extends CMPPBaseMessage{

    public CMPPTerminateRespMessage() {
        super(CMPPConstants.CMPP_TERMINATE_RESP, 0);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CMPPTerminateRespMessage:[sequenceId=")
                .append(sequenceString()).append("]");
        return buffer.toString();
    }
}