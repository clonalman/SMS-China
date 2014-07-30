package com.netgao.sms.protocol.cmpp.message;

/**
 * CMPP连接测试信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPActiveTestMessage extends CMPPBaseMessage {

    public CMPPActiveTestMessage() {
        super(CMPPConstants.CMPP_ACTIVE_TEST, 0);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CMPPActiveTestMessage:[sequenceId="+sequenceString()+"]");
        return buffer.toString();
    }
}