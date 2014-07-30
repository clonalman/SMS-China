package com.netgao.sms.protocol.smgp.message;

import com.netgao.sms.protocol.smgp.tlv.TLVByte;
import com.netgao.sms.protocol.smgp.tlv.TLVString;
import com.netgao.sms.protocol.smgp.util.ByteUtil;

public class SMGPDeliverMessage extends SMGPBaseMessage {

	
	
	

	private byte[] msgId=new byte[10]; // 10

	private byte isReport; // 1

	private byte msgFmt; // 1

	private String recvTime; // 14

	private String srcTermId; // 21

	private String destTermId; // 21

	private int msgLength; // 1

	private byte[] bMsgContent; // msgLength

	private String msgContent;
	
	private String reserve=""; // 8
	

	
	private TLVByte     tpPid   =new TLVByte(SMGPConstants.OPT_TP_PID);
	private TLVByte     tpUdhi  =new TLVByte(SMGPConstants.OPT_TP_UDHI);
	private TLVString   linkId  =new TLVString(SMGPConstants.OPT_LINK_ID);
	private TLVByte     srcTermType=new TLVByte(SMGPConstants.OPT_SRC_TERM_TYPE);
	private TLVString   srcTermPseudo=new TLVString(SMGPConstants.OPT_SRC_TERM_PSEUDO);
	private TLVByte     submitMsgType=new TLVByte(SMGPConstants.OPT_SUBMIT_MSG_TYPE);
	private TLVByte     spDealResult=new TLVByte(SMGPConstants.OPT_SP_DEAL_RESULT);

	public SMGPDeliverMessage() {
		this.commandId = SMGPConstants.SMGP_DELIVER;
		registerOptional(tpPid);
		registerOptional(tpUdhi);
		registerOptional(linkId);
		registerOptional(srcTermType);
		registerOptional(srcTermPseudo);
		registerOptional(submitMsgType);
		registerOptional(spDealResult);		
	}

	
	
	public void setTpPid(byte value){
		tpPid.setValue(value);
	}
	public byte getTpPid(){
		return tpPid.getValue();
	}
	public void setTpUdhi(byte value){
		tpUdhi.setValue(value);
	}
	public byte getTpUdhi(){
		return tpUdhi.getValue();
	}
	public void setLinkId(String value){
		linkId.setValue(value);
	}
	public String getLinkId(){
		return linkId.getValue();
	}

	public void setSrcTermType(byte value){
		srcTermType.setValue(value);
	}
	public byte getSrcTermType(){
		return srcTermType.getValue();
	}
	
	public void setSrcTermPseudo(String value){
		srcTermPseudo.setValue(value);
	}
	public String getSrcTermPseudo(){
		return srcTermPseudo.getValue();
	}	



	
	public void setSubmitMsgType(byte value){
		submitMsgType.setValue(value);
	}
	public byte getSubmitMsgType(){
		return submitMsgType.getValue();
	}
	
	public void setSpDealResult(byte value){
		spDealResult.setValue(value);
	}
	public byte getSpDealResult(){
		return spDealResult.getValue();
	}


	
	@Override
	protected int setBody(byte[] bodyBytes) throws Exception {
		int offset = 0;
		byte[] tmp = null;

		msgId=new byte[10];
		System.arraycopy(bodyBytes, offset, msgId, 0, 10);
		offset += 10;

		isReport = bodyBytes[offset];
		offset += 1;

		msgFmt = bodyBytes[offset];
		offset += 1;

		tmp = new byte[14];
		System.arraycopy(bodyBytes, offset, tmp, 0, 14);
		recvTime = new String(ByteUtil.rtrimBytes(tmp));
		offset += 14;

		tmp = new byte[21];
		System.arraycopy(bodyBytes, offset, tmp, 0, 21);
		srcTermId = new String(ByteUtil.rtrimBytes(tmp));
		offset += 21;

		tmp = new byte[21];
		System.arraycopy(bodyBytes, offset, tmp, 0, 21);
		destTermId = new String(ByteUtil.rtrimBytes(tmp));
		offset += 21;

		byte b = bodyBytes[offset];
		offset += 1;

		msgLength = b >= 0 ? b : (256 + b); // byte 最大只有128，这种处理可以取得129-140的数据
		
		if(msgLength>0){
			tmp = new byte[msgLength];
			System.arraycopy(bodyBytes, offset, tmp, 0, msgLength);
			offset += msgLength;
			bMsgContent = tmp;
			try {
				if (msgFmt == 8)
					msgContent = new String(bMsgContent, "iso-10646-ucs-2");
				else if (msgFmt == 15)
					msgContent = new String(bMsgContent, "GBK");
				else
					msgContent = new String(bMsgContent, "iso-8859-1");
			} catch (Exception ex) {
				throw ex;
			}
		}

		tmp = new byte[8];
		System.arraycopy(bodyBytes, offset, tmp, 0, 8);
		reserve = new String(ByteUtil.rtrimBytes(tmp));
		offset += 8;
		
		return offset;

	}

	@Override
	protected byte[] getBody() throws Exception {
		int len = 10 + 1 + 1 + 14 + 21 + 21 + 1 + msgLength + 8;
		int offset = 0;
		byte[] bodyBytes = new byte[len];
		System.arraycopy(msgId, 0, bodyBytes, offset, 10);
		offset += 10;

		bodyBytes[offset] = isReport;
		offset += 1;

		bodyBytes[offset] = msgFmt;
		offset += 1;

		ByteUtil.rfillBytes(recvTime.getBytes(), 14, bodyBytes, offset);
		offset += 14;

		ByteUtil.rfillBytes(srcTermId.getBytes(), 21, bodyBytes, offset);
		offset += 21;

		ByteUtil.rfillBytes(destTermId.getBytes(), 21, bodyBytes, offset);
		offset += 21;

		bodyBytes[offset] = (byte)msgLength;
		offset += 1;

		if (bMsgContent == null && msgContent!=null) {
			try {
				if (msgFmt == 8)
					bMsgContent = msgContent.getBytes("iso-10646-ucs-2");
				else if (msgFmt == 15)
					bMsgContent = msgContent.getBytes("GBK");
				else
					bMsgContent = msgContent.getBytes("iso-8859-1");
			} catch (Exception ex) {
				throw ex;
			}
		}
		if (bMsgContent != null) {
			ByteUtil.rfillBytes(bMsgContent, msgLength, bodyBytes, offset);
		}
		offset+=msgLength;

		ByteUtil.rfillBytes(reserve.getBytes(), 8, bodyBytes, offset);
		offset += 8;

		return bodyBytes;
	}

	public byte[] getMsgId() {
		return this.msgId;
	}

	public void setMsgId(byte[] msgId) {
		this.msgId = msgId;
	}

	public byte getIsReport() {
		return this.isReport;
	}

	public void setIsReport(byte isReport) {
		this.isReport = isReport;
	}

	public byte getMsgFmt() {
		return this.msgFmt;
	}

	public void setMsgFmt(byte msgFmt) {
		this.msgFmt = msgFmt;
	}

	public String getRecvTime() {
		return this.recvTime;
	}

	public void setRecvTime(String recvTime) {
		this.recvTime = recvTime;
	}

	public String getSrcTermId() {
		return this.srcTermId;
	}

	public void setSrcTermId(String srcTermId) {
		this.srcTermId = srcTermId;
	}

	public String getDestTermId() {
		return this.destTermId;
	}

	public void setDestTermId(String destTermId) {
		this.destTermId = destTermId;
	}

	public int getMsgLength() {
		return this.msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}

	public byte[] getBMsgContent() {
		return this.bMsgContent;
	}

	public void setBMsgContent(byte[] msgContent) {
		bMsgContent = msgContent;
		msgLength = bMsgContent == null ? 0 : bMsgContent.length;
	}

	public String getMsgContent() {
		if(bMsgContent==null)return null;
		
		if(isReport==1){
			SMGPReportData report=new SMGPReportData();
			try {
				report.fromBytes(bMsgContent);
				return report.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}else{
			if(msgContent!=null)
				   return msgContent;
			
			String msg=null;
			try {
				if (msgFmt == 8) {
					msg = new String(bMsgContent, "iso-10646-ucs-2");
				} else if (msgFmt == 15) {
					msg = new String(bMsgContent, "GBK");
				} else {
					msg = new String(bMsgContent, "iso-8859-1");
				}
				msg=msg.trim();
			} catch (Exception ex) {
			}
			return msg;
			
		}
		
	}

	public void setMsgContent(String msgContent) {
		if (msgContent != null) {
			this.bMsgContent = msgContent.getBytes();
			this.msgContent = msgContent;
			if (msgContent.getBytes().length == msgContent.length()) {
				this.msgLength = msgContent.length();				
			} else {
				this.msgLength = msgContent.length() * 2;
			}
			this.isReport=(byte)0;
		} else {
			this.msgLength = 0;
			this.bMsgContent = null;
			this.msgContent = null;
		}

	}
	
	public SMGPReportData getReport(){
		if(isReport==0 || bMsgContent==null)return null;
		SMGPReportData report=new SMGPReportData();
		try {
			report.fromBytes(bMsgContent);
			return report;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setReport(SMGPReportData report){
		if(report==null)return;
		try {
			this.bMsgContent=report.toBytes();
			this.msgContent=report.toString();
			this.msgLength=SMGPReportData.LENGTH;
			this.isReport=1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public String getReserve() {
		return this.reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public String msgIdString(){
		return ByteUtil.byteArrayToHexString(msgId);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SMGPDeliverMessage:[sequenceNumber=").append(
				sequenceString()).append(",");
		buffer.append("msgId=").append(msgIdString()).append(",");
		buffer.append("isReport=").append(isReport).append(",");
		buffer.append("msgFmt=").append(msgFmt).append(",");
		buffer.append("recvTime=").append(recvTime).append(",");
		buffer.append("srcTermId=").append(srcTermId).append(",");
		buffer.append("destTermId=").append(destTermId).append(",");
		buffer.append("msgLength=").append(msgLength).append(",");
		buffer.append("msgContent=").append(getMsgContent()).append("]");
		
		return buffer.toString();
	}
}