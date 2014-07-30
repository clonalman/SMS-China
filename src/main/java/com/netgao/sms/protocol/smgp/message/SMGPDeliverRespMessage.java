package com.netgao.sms.protocol.smgp.message;

import com.netgao.sms.protocol.smgp.util.ByteUtil;

public class SMGPDeliverRespMessage extends SMGPBaseMessage {

	public SMGPDeliverRespMessage() {
		this.commandId = SMGPConstants.SMGP_DELIVER_RESP;
	}

	private byte[] msgId=new byte[10]; // 10

	private int status; // 4

	@Override
	protected int setBody(byte[] bodyBytes) throws Exception {
		int offset = 0;
		
		msgId=new byte[10];
		System.arraycopy(bodyBytes, offset, msgId, 0, 10);
		offset += 10;

		status = ByteUtil.byte2int(bodyBytes, offset);
		offset += 4;
		return offset;
	}

	@Override
	protected byte[] getBody() throws Exception {
		int len = 10 + 4;
		int offset = 0;
		byte[] bodyBytes = new byte[len];
		System.arraycopy(msgId, 0, bodyBytes, offset, 10);
		offset += 10;

		ByteUtil.int2byte(status, bodyBytes, offset);
		offset += 4;

		return bodyBytes;
	}

	public byte[] getMsgId() {
		return this.msgId;
	}

	public void setMsgId(byte[] msgId) {
		this.msgId = msgId;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String msgIdString(){
		return ByteUtil.byteArrayToHexString(msgId);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SMGPDeliverRespMessage:[sequenceNumber=").append(
				sequenceString()).append(",");
		buffer.append("msgId=").append(msgIdString()).append(",");
		buffer.append("status=").append(status);
		buffer.append("]");
		return buffer.toString();
	}
}