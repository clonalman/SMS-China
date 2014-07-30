package com.netgao.sms.protocol.smgp.message;

import java.util.Vector;

import com.netgao.sms.protocol.*;
import com.netgao.sms.protocol.smgp.tlv.TLV;
import com.netgao.sms.protocol.smgp.util.ByteUtil;

public class SMGPBaseMessage implements Message {

	public static final int SZ_HEADER = 12;

	protected int commandLength = 0;

	protected int commandId = 0;

	protected int sequenceNumber =0; 
	
	protected Vector optionalParameters = new Vector(10, 2);

	public boolean fromBytes(byte[] bytes) throws Exception {
		if (bytes == null) {
			return false;
		}
		if (bytes.length < SZ_HEADER) {
			return false;
		}
        int offset=0;
		commandLength = ByteUtil.byte2int(bytes, offset);
		offset+=4;
		commandId = ByteUtil.byte2int(bytes,offset);
		offset+=4;
		sequenceNumber =ByteUtil.byte2int(bytes, offset);
		offset+=4;
		
		byte[] bodyBytes = new byte[commandLength - SZ_HEADER];
		System.arraycopy(bytes, offset, bodyBytes, 0, bodyBytes.length);
		int bodyLength=setBody(bodyBytes);
		
		if (bodyLength < bodyBytes.length) {
			byte[] optBytes = new byte[bodyBytes.length - bodyLength];
			System.arraycopy(bodyBytes, bodyLength, optBytes, 0, optBytes.length);
			setOptionalBody(optBytes);
		}
		
		return true;
	}

	public byte[] toBytes() throws Exception {
		byte[] bodyBytes = getBody();
		byte[] optBytes = getOptionalBody();
		
		commandLength = SZ_HEADER + bodyBytes.length+optBytes.length;
		int offset=0;
		byte[] bytes = new byte[commandLength];

		ByteUtil.int2byte(commandLength, bytes, offset);
		offset+=4;
		ByteUtil.int2byte(commandId, bytes, offset);
		offset+=4;
		ByteUtil.int2byte(sequenceNumber, bytes, offset);
		offset+=4;
		
		System.arraycopy(bodyBytes, 0, bytes, offset, bodyBytes.length);
		offset += bodyBytes.length;

		System.arraycopy(optBytes, 0, bytes, offset, optBytes.length);
		offset += optBytes.length;
		return bytes;
	}

	protected int setBody(byte[] bodyBytes) throws Exception {
         return 0;
	}

	protected byte[] getBody() throws Exception {
		return new byte[0];
	}

	
	private void setOptionalBody(byte[] buffer) throws Exception {
		short tag;
		short length;

		int offset = 0;
		TLV tlv = null;
		while (offset < buffer.length) {
			// we prepare buffer with one parameter

			tag = ByteUtil.byte2short(buffer, offset);
			offset += 2;
			tlv = findOptional(tag);
			if(tlv==null)break; //ignore error
			length = ByteUtil.byte2short(buffer, offset);
			offset += 2;
			byte[] valueBytes = new byte[length];
			System.arraycopy(buffer, offset, valueBytes, 0, length);
			offset += length;
			tlv.setValueData(valueBytes);

		}
	}

	private byte[] getOptionalBody() throws Exception {
		int size = optionalParameters.size();
		TLV tlv = null;
		int len = 0;
		for (int i = 0; i < size; i++) {
			tlv = (TLV) optionalParameters.get(i);
			if(tlv.hasValue())
			  len += 4 + tlv.getLength();

		}
		byte[] bytes = new byte[len];
		int offset = 0;
		for (int i = 0; i < size; i++) {
			tlv = (TLV) optionalParameters.get(i);
			offset = tlv.toBytes(bytes, offset);
		}
		if (offset == 0) {
			return new byte[0];
		}
		byte[] result = new byte[offset];
		System.arraycopy(bytes, 0, result, 0, offset);
		return result;
	}

	protected void registerOptional(TLV tlv) {
		if (tlv != null) {
			optionalParameters.add(tlv);
		}
	}

	private TLV findOptional(short tag) {
		int size = optionalParameters.size();
		TLV tlv = null;
		for (int i = 0; i < size; i++) {
			tlv = (TLV) optionalParameters.get(i);
			if (tlv != null) {
				if (tlv.getTag() == tag) {
					return tlv;
				}
			}
		}
		return null;
	}
	
	protected String plus86(String mobile){
		if(mobile==null||mobile.trim().length()==0)return "";
		if(mobile.startsWith("86"))return mobile;
		if(mobile.startsWith("+86"))return mobile.substring(1);
		return "86"+mobile;
	}
	
	protected String minus86(String mobile){
		if(mobile==null||mobile.trim().length()==0)return "";
		if(mobile.startsWith("86"))return mobile.substring(2);
		if(mobile.startsWith("+86"))return mobile.substring(3);
		return mobile;
		
	}
	
	
	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public int getCommandLength() {
		return commandLength;
	}

	public void setCommandLength(int commandLength) {
		this.commandLength = commandLength;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}


	public String sequenceString(){
		StringBuffer buffer=new StringBuffer();
		int offset=0;
		byte[] seqBytes=new byte[8];
		System.arraycopy(ByteUtil.int2byte(sequenceNumber), offset, seqBytes, 4, 4);
		buffer.append(ByteUtil.byte2long(seqBytes));
		return buffer.toString();
		
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SMGPBaseMessage:[sequenceNumber=").append(sequenceString()).append(",").append(
			"commandId=").append(commandId).append("]");

		return buffer.toString();
	}

}
