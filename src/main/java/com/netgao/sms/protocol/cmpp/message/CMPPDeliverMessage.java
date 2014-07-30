package com.netgao.sms.protocol.cmpp.message;

import com.netgao.sms.protocol.cmpp.util.ByteUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 服务器下发信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPDeliverMessage  extends CMPPBaseMessage {

    private  final int maxMsgLength0    = 160;
    private  final int maxMsgLength1    = 140;

    private long    msgId       = 0;
    private String  destId      = null;
    private String  serviceId   = null;
    private int     tpPid       = 0;
    private int     tpUdhi      = 0;
    private int     msgFmt      = 0;
    private String  srcTerminalId   = null;
    private int     registeredDelivery = 0;
    private int     msgLength   = 0;
    private byte[]  msgContent  = null;
    private String  reserved    = null;

    private long    reportMsgId = 0;
    private String  reportStat  = null;
    private String  reportSubmitTime=null;
    private String  reportDoneTime=null;
    private String  reportDestTerminalId=null;
    private int    reportSMSCSequence=0;

    public CMPPDeliverMessage() {
        super(CMPPConstants.CMPP_DELIVER, 73);
    }

    public String getReportDestTerminalId() {
        return reportDestTerminalId;
    }

    public void setReportDestTerminalId(String reportDestTerminalId) {
        this.reportDestTerminalId = reportDestTerminalId;
    }

    public String getReportDoneTime() {
        return reportDoneTime;
    }

    public void setReportDoneTime(String reportDoneTime) {
        this.reportDoneTime = reportDoneTime;
    }

    public long getReportMsgId() {
        return reportMsgId;
    }

    public void setReportMsgId(long reportMsgId) {
        this.reportMsgId = reportMsgId;
    }

    public int getReportSMSCSequence() {
        return reportSMSCSequence;
    }

    public void setReportSMSCSequence(int reportSMSCSequence) {
        this.reportSMSCSequence = reportSMSCSequence;
    }

    public String getReportStat() {
        return reportStat;
    }

    public void setReportStat(String reportStat) {
        this.reportStat = reportStat;
    }

    public String getReportSubmitTime() {
        return reportSubmitTime;
    }

    public void setReportSubmitTime(String reportSubmitTime) {
        this.reportSubmitTime = reportSubmitTime;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public byte[] getMsgContent() {
        return msgContent;
    }

    public int getMsgFmt() {
        return msgFmt;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public int getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(int registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
        if( registeredDelivery == 1 ) {
            msgLength = 60;
            msgFmt = 4;
        }
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSrcTerminalId() {
        return srcTerminalId;
    }

    public void setSrcTerminalId(String srcTerminalId) {
        this.srcTerminalId = srcTerminalId;
    }

    public int getTpPid() {
        return tpPid;
    }

    public void setTpPid(int pid) {
        tpPid = pid;
    }

    public int getTpUdhi() {
        return tpUdhi;
    }

    public void setTpUdhi(int udhi) {
        this.tpUdhi = udhi;
    }

    /**
     * 得到消息的16进制文本。
     */
    public String getMsgHexText() {
        String msgText = null;

        if( msgContent == null )
            return null;

        //0：纯ASCII字符串
        //3：写卡操作
        //4：二进制编码
        //8：UCS2编码
        //15: GBK编码
        switch( msgFmt ) {
            case 0:
                msgText = byteToHexString(msgContent,"US-ASCII");
                break;
            case 3:
                msgText = byteToHexString(msgContent,"US-ASCII");
                break;
            case 4:
                msgText = byteToHexString(msgContent,"BIN");
                break;
            case 8:
                msgText = byteToHexString(msgContent,"ISO-10646-UCS-2");
                break;
            case 15:
                msgText = byteToHexString(msgContent,"GBK");
                break;
            default:
                msgText = byteToHexString(msgContent, null);
        }
        return msgText;
    }

    /**
     * 根据当前的MsgFmt得到MsgContent文本
     */
    public String getMsgText() {
        String msgText = null;

        if( msgContent == null )
            return null;

        //0：纯ASCII字符串
        //3：写卡操作
        //4：二进制编码
        //8：UCS2编码
        //15: GBK编码
        try {
            switch( msgFmt ) {
                case 0:
                    msgText = new String(msgContent, "US-ASCII");
                    break;
                case 3:
                    msgText = new String(msgContent, "US-ASCII");
                    break;
                case 4:
                    msgText = toPrintableString(msgContent);
                    break;
                case 8:
                    msgText = new String(msgContent, "ISO-10646-UCS-2");
                    break;
                case 15:
                    msgText = new String(msgContent, "GBK");
                    break;
                default:
                    msgText = toPrintableString(msgContent);
            }
        } catch (UnsupportedEncodingException e) {
            msgText = toPrintableString(msgContent);
        }
        return msgText;
    }


    /**
     * 设置二进制内容
     */
    public void setMsgContent(byte[] msgContent, int msgFmt) {
        // set
        this.msgFmt     = msgFmt;
        this.msgContent = msgContent;
        if( msgContent == null ) {
            msgLength = 0;
        } else {
            if( msgFmt == 0 ) {
                if( msgContent.length > maxMsgLength0 ) {
                    msgContent = new byte[maxMsgLength0];
                    ByteUtil.bytesCopy(msgContent, msgContent, 0, maxMsgLength0 - 1, 0);
                }
            } else {
                if( msgContent.length > maxMsgLength1 ) {
                    msgContent = new byte[maxMsgLength1];
                    ByteUtil.bytesCopy(msgContent, msgContent, 0, maxMsgLength1 - 1, 0);
                }
            }
            msgLength  = msgContent.length;
        }
    }
    /**
     * 设置文本内容
     * 信息格式: 0：ASCII串 3：短信写卡操作 4：二进制信息 8：UCS2编码 15：含GB汉字
     */
    public void setMsgText(String msgText, int msgFmt) {

        byte[]  binCnt = null;
        //0：纯ASCII字符串
        //3：写卡操作
        //4：二进制编码
        //8：UCS2编码
        //15: GBK编码
        try {
            switch( msgFmt ) {
                case 0:
                    binCnt = msgText.getBytes("US-ASCII");
                    break;
                case 3:
                    binCnt = msgText.getBytes("US-ASCII");
                case 4:
                    binCnt = msgText.getBytes("US-ASCII");
                case 8:
                    binCnt = msgText.getBytes("ISO-10646-UCS-2");
                    break;
                case 15:
                    binCnt = msgText.getBytes("GBK");
                    break;
                default:
                    binCnt = msgText.getBytes();
            }
        } catch (UnsupportedEncodingException e) {
            binCnt = msgText.getBytes();
        }
        // set
        setMsgContent(binCnt,msgFmt);
    }


    /**
     * 字节数组转换成16进制可打印字符串
     * @param b
     * @param charset
     * @return
     */
    private String byteToHexString(byte[] b, String charset) {
        String  str = null;

        if( b == null ) {
            return null;
        }
        if( charset != null ) {
            try {
                str = new String(b, charset);
            } catch (UnsupportedEncodingException e1) {
                StringBuffer sb = new StringBuffer();
                for( int i=0; i<b.length; i++ ) {        // con't change, change to binary
                    char chHi  = Character.forDigit((b[i]&0xF0)>>4    ,16);
                    char chLow = Character.forDigit((b[i]&0x0F) ,16);
                    sb.append(chHi);
                    sb.append(chLow);
                    sb.append(' ');
                }
                str = sb.toString().toUpperCase();
            }
        } else {
            str = new String(b);
        }
        return str;
    }

    /**
     * to protableString
     * @param b
     * @return
     */
    private String toPrintableString(byte[] b) {

        if( b == null ) return null;

        StringBuffer sb = new StringBuffer();
        byte[] t = new byte[1];
        for( int i=0; i<b.length; i++ ) {        // con't change, change to binary
            if( b[i]>=0x20 && b[i]<=0x7e ) {  // printable char
                t[0] = b[i];
                sb.append(new String(t));
            } else {                            //non-pritable char
                sb.append(".");
            }
        }
        return sb.toString();
    }

    @Override
    protected void setBody(byte[] bodyBytes) {

        byte[] abyte0 = new byte[21];
        int off = 0;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 7, 0);
        msgId = ByteUtil.Bytes8ToLong(abyte0);
        off += 8;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 20, 0);
        destId = new String(abyte0,0,21);
        off += 21;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 9, 0);
        serviceId = new String(abyte0, 0, 10);
        off += 10;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        tpPid = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        tpUdhi = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0, (byte) 0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        msgFmt = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 20, 0);
        srcTerminalId = new String(abyte0,0,21);
        off += 21;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        registeredDelivery = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        msgLength = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        msgContent = new byte[msgLength];
        ByteUtil.bytesCopy(bodyBytes, msgContent, off, off + msgLength - 1, 0);
        off += msgLength;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 7, 0);
        reserved = new String(abyte0,0,8);
        off += 8;

        if( registeredDelivery == 1 ) {
            off = 0;

            Arrays.fill(abyte0,(byte)0);
            ByteUtil.bytesCopy(msgContent, abyte0, off, off + 7, 0);
            reportMsgId = ByteUtil.Bytes8ToLong(abyte0);
            off += 8;

            Arrays.fill(abyte0,(byte)0);
            ByteUtil.bytesCopy(msgContent, abyte0, off, off + 6, 0);
            reportStat = new String(abyte0,0,7);
            off += 7;

            Arrays.fill(abyte0,(byte)0);
            ByteUtil.bytesCopy(msgContent, abyte0, off, off + 9, 0);
            reportSubmitTime = new String(abyte0,0,10);
            off += 10;

            Arrays.fill(abyte0,(byte)0);
            ByteUtil.bytesCopy(msgContent, abyte0, off, off + 9, 0);
            reportDoneTime = new String(abyte0,0,10);
            off += 10;

            Arrays.fill(abyte0,(byte)0);
            ByteUtil.bytesCopy(msgContent, abyte0, off, off + 20, 0);
            reportDestTerminalId = new String(abyte0,0,21);
            off += 21;

            Arrays.fill(abyte0,(byte)0);
            ByteUtil.bytesCopy(msgContent, abyte0, off, off + 3, 0);
            reportSMSCSequence = ByteUtil.Bytes4ToInt(abyte0);
            off += 4;
        }
    }

    protected byte[] getBody() {

        // make bodybytes
        byte[] bodyBytes  = new byte[73 + msgLength];
        Arrays.fill(bodyBytes,(byte)0);
        // make parameter
        if( msgId == 0 ) {
            msgId = 123456;
        }
        if( destId == null ) {
            destId = "";
        }
        if( serviceId == null ) {
            serviceId = "";
        }
        if( srcTerminalId == null ) {
            srcTerminalId = "";
        }
        if( reserved == null ) {
            reserved = "";
        }
        if( reportStat == null ) {
            reportStat = "";
        }
        if( reportSubmitTime == null ) {
            reportSubmitTime = "";
        }
        if( reportDoneTime == null ) {
            reportDoneTime = "";
        }
        if( reportDestTerminalId == null ) {
            reportDestTerminalId = "";
        }

        // make body
        int off = 0;
        ByteUtil.bytesCopy(ByteUtil.longToBytes8(msgId), bodyBytes, 0, 7, off);
        off += 8;
        ByteUtil.bytesCopy(destId.getBytes(), bodyBytes, 0, 20, off);
        off += 21;
        ByteUtil.bytesCopy(serviceId.getBytes(), bodyBytes, 0, 9, off);
        off += 10;
        bodyBytes[off] = ByteUtil.intToByte(tpPid);
        off += 1;
        bodyBytes[off] = ByteUtil.intToByte(tpUdhi);
        off += 1;
        bodyBytes[off] = ByteUtil.intToByte(msgFmt);
        off += 1;
        ByteUtil.bytesCopy(srcTerminalId.getBytes(), bodyBytes, 0, 20, off);
        off += 21;
        bodyBytes[off] = ByteUtil.intToByte(registeredDelivery);
        off += 1;
        bodyBytes[off] = ByteUtil.intToByte(msgLength);
        off += 1;

        if( registeredDelivery == 1 ) {
            int off1 = 0;
            msgContent = new byte[msgLength];

            ByteUtil.bytesCopy(ByteUtil.longToBytes8(reportMsgId), msgContent, 0, 7, off1);
            off1 += 8;
            ByteUtil.bytesCopy(reportStat.getBytes(), msgContent, 0, 6, off1);
            off1 += 7;
            ByteUtil.bytesCopy(reportSubmitTime.getBytes(), msgContent, 0, 9, off1);
            off1 += 10;
            ByteUtil.bytesCopy(reportDoneTime.getBytes(), msgContent, 0, 9, off1);
            off1 += 10;
            ByteUtil.bytesCopy(reportDestTerminalId.getBytes(), msgContent, 0, 20, off1);
            off1 += 21;
            ByteUtil.bytesCopy(ByteUtil.intToBytes4(reportSMSCSequence), msgContent, 0, 3, off1);
            off1 += 4;
        }

        ByteUtil.bytesCopy(msgContent, bodyBytes, 0, msgLength - 1, off);
        off += msgLength;
        ByteUtil.bytesCopy(reserved.getBytes(), bodyBytes, 0, 7, off);
        off += 8;

        return bodyBytes;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPDeliverMessage:[sequenceId="+ sequenceString() +",");
        sb.append("msgId=" + msgId + ",");
        sb.append("destId=" + destId + ",");
        sb.append("serviceId=" + serviceId + ",");
        sb.append("tpPid=" + tpPid + ",");
        sb.append("tpUdhi=" + tpUdhi + ",");
        sb.append("msgFmt=" + msgFmt + ",");
        sb.append("srcTerminalId=" + srcTerminalId + ",");
        sb.append("registeredDelivery=" + registeredDelivery + ",");
        sb.append("msgLength=" + msgLength + ",");
        sb.append("msgContent=" + msgContent + ",");
        sb.append("reserved=" + reserved + "]");

        return sb.toString();
    }
}