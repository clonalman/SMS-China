package com.netgao.sms.protocol.smgp.message;


public class SMGPActiveTestMessage extends SMGPBaseMessage {

	public SMGPActiveTestMessage() {
		this.commandId = SMGPConstants.SMGP_ACTIVE_TEST;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SMGPActiveTestMessage:[sequenceNumber=")
			  .append(sequenceString()).append("]");
		return buffer.toString();
	}
}