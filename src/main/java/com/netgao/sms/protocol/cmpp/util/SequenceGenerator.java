package com.netgao.sms.protocol.cmpp.util;



public class SequenceGenerator {

	private static int seqId = 1;//RandomGenerator.getAbsInt();

	//取值范围为 0x00000001-0x7FFFFFFF
	public static synchronized int nextSequence() {
		//循环计数
		if (seqId == Integer.MAX_VALUE) {
			seqId = 0;
		}

		return seqId++;
	}


}
