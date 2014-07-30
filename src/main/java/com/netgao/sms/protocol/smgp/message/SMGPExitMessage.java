package com.netgao.sms.protocol.smgp.message;



public class SMGPExitMessage extends SMGPBaseMessage {

	public SMGPExitMessage() {
		this.commandId = SMGPConstants.SMGP_EXIT_TEST;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SMGPExitMessage:[sequenceNumber=")
				.append(sequenceString()).append("]");
		return buffer.toString();
	}
}