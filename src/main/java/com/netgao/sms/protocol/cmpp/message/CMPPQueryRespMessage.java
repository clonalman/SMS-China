package com.netgao.sms.protocol.cmpp.message;

import com.netgao.sms.protocol.cmpp.util.ByteUtil;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * 查询返回信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPQueryRespMessage extends CMPPBaseMessage {

    private String  time        = null;
    private int     queryType   = 0;
    private String  queryCode   = null;
    private int     mtTLMsg     = 0;
    private int     mtTLUsr     = 0;
    private int     mtScs       = 0;
    private int     mtWT        = 0;
    private int     mtFL        = 0;
    private int     moScs       = 0;
    private int     moWT        = 0;
    private int     moFL        = 0;

    public CMPPQueryRespMessage() {
        super(CMPPConstants.CMPP_QUERY_RESP, 51);
    }

    // getter
    public int getMoFL() {
        return moFL;
    }
    public void setMoFL(int mofl) {
        this.moFL = mofl;
    }
    public int getMoScs() {
        return moScs;
    }
    public void setMoScs(int scs) {
        this.moScs = scs;
    }
    public int getMoWT() {
        return moWT;
    }
    public void setMoWT(int mowt) {
        this.moWT = mowt;
    }
    public int getMtFL() {
        return mtFL;
    }
    public void setMtFL(int mtfl) {
        this.mtFL = mtfl;
    }
    public int getMtScs() {
        return mtScs;
    }
    public void setMtScs(int scs) {
        this.mtScs = scs;
    }
    public int getMtTLMsg() {
        return mtTLMsg;
    }
    public void setMtTLMsg(int msg) {
        this.mtTLMsg = msg;
    }
    public int getMtTLUsr() {
        return mtTLUsr;
    }
    public void setMtTLUsr(int usr) {
        this.mtTLUsr = usr;
    }
    public int getMtWT() {
        return mtWT;
    }
    public void setMtWT(int mtwt) {
        this.mtWT = mtwt;
    }
    public String getQueryCode() {
        return queryCode;
    }
    public void setQueryCode(String queryCode) {
        this.queryCode = queryCode;
    }
    public int getQueryType() {
        return queryType;
    }
    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    /**
     *
     */
    protected void setBody(byte[] bodyBytes) {

        byte[] abyte0 = new byte[21];
        int off = 0;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 7, 0);
        time = new String(abyte0, 0, 8);
        off += 8;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        queryType = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 9, 0);
        queryCode = new String(abyte0, 0, 10);
        off += 10;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 3, 0);
        mtTLMsg = ByteUtil.Bytes4ToInt(abyte0);
        off += 4;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 3, 0);
        mtTLUsr = ByteUtil.Bytes4ToInt(abyte0);
        off += 4;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 3, 0);
        mtScs = ByteUtil.Bytes4ToInt(abyte0);
        off += 4;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 3, 0);
        mtWT = ByteUtil.Bytes4ToInt(abyte0);
        off += 4;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 3, 0);
        mtFL = ByteUtil.Bytes4ToInt(abyte0);
        off += 4;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 3, 0);
        moScs = ByteUtil.Bytes4ToInt(abyte0);
        off += 4;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 3, 0);
        moWT = ByteUtil.Bytes4ToInt(abyte0);
        off += 4;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 3, 0);
        moFL = ByteUtil.Bytes4ToInt(abyte0);
        off += 4;

    }

    /**
     *
     */
    protected byte[] getBody() {

        // make bodybytes
        int bodyLength = getCommandLength();
        byte[] bodyBytes  = new byte[bodyLength];
        Arrays.fill(bodyBytes,(byte)0);

        // make parameter
        if( time == null ) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int timestamp = calendar.get(Calendar.YEAR) * 10000
                    + (calendar.get(Calendar.MONTH) + 1) * 100
                    + calendar.get(Calendar.DAY_OF_MONTH);
            time = String.valueOf(timestamp);
        }
        if( queryCode == null ) {
            queryCode = "";
        }

        // make body
        int off = 0;
        ByteUtil.bytesCopy(time.getBytes(), bodyBytes, 0, 7, off);
        off += 8;
        bodyBytes[off] = ByteUtil.intToByte(queryType);
        off += 1;
        ByteUtil.bytesCopy(queryCode.getBytes(), bodyBytes, 0, 9, off);
        off += 10;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(mtTLMsg), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(mtTLUsr), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(mtScs), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(mtWT), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(mtFL), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(moScs), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(moWT), bodyBytes, 0, 3, off);
        off += 4;
        ByteUtil.bytesCopy(ByteUtil.intToBytes4(moFL), bodyBytes, 0, 3, off);
        off += 4;

        return bodyBytes;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPQueryRespMessage:[sequenceId="+ sequenceString() +",");
        sb.append("time=" + time + ",");
        sb.append("queryType=" + queryType + ",");
        sb.append("queryCode=" + queryCode + ",");
        sb.append("mtTLMsg=" + mtTLMsg + ",");
        sb.append("mtTLUsr=" + mtTLUsr + ",");
        sb.append("mtScs=" + mtScs + ",");
        sb.append("mtWT=" + mtWT + ",");
        sb.append("mtFL=" + mtFL + ",");
        sb.append("moScs=" + moScs + ",");
        sb.append("moWT=" + moWT + ",");
        sb.append("moFL=" + moFL + "]");

        return sb.toString();
    }
}