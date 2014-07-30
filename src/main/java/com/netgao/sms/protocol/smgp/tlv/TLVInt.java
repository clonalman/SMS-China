package com.netgao.sms.protocol.smgp.tlv;

import com.netgao.sms.protocol.smgp.util.ByteUtil;

public class TLVInt extends TLV {
	private int value = 0;

	public TLVInt() {
		super(4, 4);
	}

	public TLVInt(short p_tag) {
		super(p_tag, 4, 4);
	}

	public TLVInt(short p_tag, int p_value) {
		super(p_tag, 4, 4);
		value = p_value;
		markValueSet();
	}

	public void setValue(int p_value) {
		value = p_value;
		markValueSet();
	}

	public int getValue() {
		return value;

	}

	@Override
	public byte[] getValueData() throws Exception {

		return ByteUtil.int2byte(value);
	}

	@Override
	public void setValueData(byte[] buffer) throws Exception {
		value = ByteUtil.byte2int(buffer);
		markValueSet();
	}

}