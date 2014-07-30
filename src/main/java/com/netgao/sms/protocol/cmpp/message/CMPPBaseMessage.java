package com.netgao.sms.protocol.cmpp.message;

import com.netgao.sms.protocol.Message;
import com.netgao.sms.protocol.cmpp.util.ByteUtil;

import java.util.Arrays;

/**
 * CMPP头信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPBaseMessage implements Message {

    public static final int SZ_HEADER = 12;

    private int totalLength;            //消息总长度
    private int commandId;              //命令或响应类型
    private int commandLength;         //命令长度
    private int sequenceId;             //消息流水号

    public CMPPBaseMessage(int commandId, int commandLength) {
       this.commandId =  commandId;
        this.commandLength =commandLength;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public int getCommandId() {
        return commandId;
    }

    public int getCommandLength() {
        return commandLength;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int value) {
        this.sequenceId = value;
    }

    public boolean fromBytes(byte[] bytes) throws Exception {
        if (bytes == null) {
            return false;
        }
        if (bytes.length < SZ_HEADER) {
            return false;
        }

        byte headBytes[] = new byte[4];
        int offset = 0;
        ByteUtil.bytesCopy(bytes, headBytes, offset, offset + 3, 0);
        totalLength = ByteUtil.Bytes4ToInt(headBytes);
        offset += 4;
        ByteUtil.bytesCopy(bytes, headBytes, offset, offset + 3, 0);
        commandId = ByteUtil.Bytes4ToInt(headBytes);
        offset += 4;
        ByteUtil.bytesCopy(bytes, headBytes, offset, offset + 3, 0);
        sequenceId = ByteUtil.Bytes4ToInt(headBytes);

        if(totalLength - SZ_HEADER < commandLength){  //length too short.
            return false;
        } else {
            byte[] bodyBytes = new byte[totalLength - SZ_HEADER];
            ByteUtil.bytesCopy(bytes, bodyBytes, SZ_HEADER, bodyBytes.length - 1, 0);
            setBody(bodyBytes);
            return true;
        }
    }

    public byte[] toBytes() throws Exception {

        byte[] bodyBytes = getBody();
        totalLength = SZ_HEADER + bodyBytes.length;
        byte[] bytes = new byte[totalLength];
        Arrays.fill(bytes, (byte) 0);
        int offset = 0;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(totalLength), bytes, 0, 3, offset);
        offset += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(commandId), bytes, 0, 3, offset);
        offset += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(sequenceId), bytes, 0, 3, offset);
        offset += 4;
        ByteUtil.bytesCopy(bodyBytes, bytes, 0, bodyBytes.length - 1, offset);

        return bytes;
    }

    protected void setBody(byte[] bodyBytes) throws Exception {

    }

    protected byte[] getBody() throws Exception {
        return new byte[0];
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

    public String sequenceString(){
        StringBuffer buffer=new StringBuffer();
        int offset=0;
        byte[] seqBytes=new byte[8];
        System.arraycopy(ByteUtil.intToBytes4(sequenceId), offset, seqBytes, 4, 4);
        buffer.append(ByteUtil.Bytes8ToLong(seqBytes));
        return buffer.toString();
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CMPPBaseMessage:[sequenceId=").append(sequenceString()).append(",").append(
                "commandId=").append(commandId).append("]");

        return buffer.toString();
    }
}