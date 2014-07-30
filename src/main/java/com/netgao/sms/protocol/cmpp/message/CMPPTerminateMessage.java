package com.netgao.sms.protocol.cmpp.message;

/**
 * Created with IntelliJ IDEA.
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午4:11
 * To change this template use File | Settings | File Templates.
 */

public class CMPPTerminateMessage extends CMPPBaseMessage {

    public CMPPTerminateMessage() {
        super(CMPPConstants.CMPP_TERMINATE, 0);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CMPPTerminateMessage:[sequenceId=")
                .append(sequenceString()).append("]");
        return buffer.toString();
    }
}