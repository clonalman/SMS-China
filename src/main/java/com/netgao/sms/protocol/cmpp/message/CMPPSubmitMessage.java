package com.netgao.sms.protocol.cmpp.message;

import com.netgao.sms.protocol.cmpp.util.ByteUtil;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 提交信息
 * User: gaudi.gao
 * Date: 14-6-20
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */

public class CMPPSubmitMessage extends CMPPBaseMessage {

    private  final int maxMsgLength0    = 160;
    private  final int maxMsgLength1    = 140;

    private long    msgId               = 0;
    private int     pkTotal             = 1;
    private int     pkNumber            = 1;
    private int     registeredDelivery  = 0;
    private int     msgLevel            = 0;
    private String  serviceId           = null;
    private int     feeUserType         = 0;
    private String  feeTerminalId       = null;
    private int     tpPid               = 0;
    private int     tpUdhi              = 0;
    private int     msgFmt              = 0;
    private String  msgSrc              = null;
    private String  feeType             = null;
    private String  feeCode             = null;
    private String  validTime           = null;
    private String  atTime              = null;
    private String  srcId               = null;
    private int     destUsrTl           = 0;
    private List    destTerminalId      = new ArrayList();
    private int     msgLength           = 0;
    private byte[]  msgContent          = null;
    private String  reserve             = null;

    public CMPPSubmitMessage() {
        super(CMPPConstants.CMPP_SUBMIT, 126);
    }

    // atTime
    public String getAtTime() {
        return atTime;
    }
    public void setAtTime(String atTime) {
        this.atTime = atTime;
    }

    //destTerminalId
    public List getDestTerminalId() {
        return destTerminalId;
    }
    public void addDestTerminalId(String destTerminalId) {
        if( destTerminalId == null )
            return;

        this.destTerminalId.add(destTerminalId);
        destUsrTl = this.destTerminalId.size();
    }
    public void removeDestTerminalId(String destTerminalId) {
        if( destTerminalId == null )
            return;

        this.destTerminalId.remove(destTerminalId);
        destUsrTl = this.destTerminalId.size();
    }

    //destUsrTl
    public int getDestUsrTl() {
        return destUsrTl;
    }

    //feeCode
    public String getFeeCode() {
        return feeCode;
    }
    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    //feeTerminalId
    public String getFeeTerminalId() {
        return feeTerminalId;
    }
    public void setFeeTerminalId(String feeTerminalId) {
        this.feeTerminalId = feeTerminalId;
    }

    //feeUserType
    public int getFeeUserType() {
        return feeUserType;
    }
    public void setFeeUserType(int feeUserType) {
        this.feeUserType = feeUserType;
    }

    //reserve
    public String getReserve() {
        return reserve;
    }
    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    //msgContent
    /**
     * 根据当前的二进制内容
     */
    public byte[] getMsgContent() {
        return msgContent;
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

    //msgFmt
    public int getMsgFmt() {
        return msgFmt;
    }

    //msgId
    public long getMsgId() {
        return msgId;
    }
    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    //msgLength
    public int getMsgLength() {
        return msgLength;
    }

    //msgLevel
    public int getMsgLevel() {
        return msgLevel;
    }
    public void setMsgLevel(int msgLevel) {
        this.msgLevel = msgLevel;
    }

    //msgSrc
    public String getMsgSrc() {
        return msgSrc;
    }
    public void setMsgSrc(String msgSrc) {
        this.msgSrc = msgSrc;
    }

    // pkNumber
    public int getPkNumber() {
        return pkNumber;
    }
    public void setPkNumber(int pkNumber) {
        this.pkNumber = pkNumber;
    }

    //pkTotal
    public int getPkTotal() {
        return pkTotal;
    }
    public void setPkTotal(int pkTotal) {
        this.pkTotal = pkTotal;
    }

    //registeredDelivery
    public int getRegisteredDelivery() {
        return registeredDelivery;
    }
    public void setRegisteredDelivery(int registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    //serviceId
    public String getServiceId() {
        return serviceId;
    }
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    //srcId
    public String getSrcId() {
        return srcId;
    }
    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    //tpPid
    public int getTpPid() {
        return tpPid;
    }
    public void setTpPid(int pid) {
        this.tpPid = pid;
    }

    //tpUdhi
    public int getTpUdhi() {
        return tpUdhi;
    }
    public void setTpUdhi(int udhi) {
        this.tpUdhi = udhi;
    }

    //validTime
    public String getValidTime() {
        return validTime;
    }
    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    //feeType
    public String getFeeType() {
        return feeType;
    }
    public void setFeeType(String feeType) {
        this.feeType = feeType;
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
    protected void setBody(byte[] bodyBytes)  {


        byte[] abyte0 = new byte[21];

        int off = 0;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 7, 0);
        msgId = ByteUtil.Bytes8ToLong(abyte0);
        off += 8;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        pkTotal = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        pkNumber = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        registeredDelivery = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        msgLevel = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 9, 0);
        serviceId = new String(abyte0,0,10);
        off += 10;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        feeUserType = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 20, 0);
        feeTerminalId = new String(abyte0,0,21);
        off += 21;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        tpPid = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        tpUdhi = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        msgFmt = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 5, 0);
        msgSrc = new String(abyte0,0,6);
        off += 6;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 1, 0);
        feeType = new String(abyte0,0,2);
        off += 2;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 5, 0);
        feeCode = new String(abyte0,0,6);
        off += 6;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 16, 0);
        validTime = new String(abyte0,0,17);
        off += 17;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 16, 0);
        atTime = new String(abyte0,0,17);
        off += 17;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 20, 0);
        srcId = new String(abyte0,0,21);
        off += 21;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        destUsrTl = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        for( int i=0; i<destUsrTl; i++ ) {
            Arrays.fill(abyte0,(byte)0);
            ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 20, 0);
            destTerminalId.add(new String(abyte0,0,21));
            off += 21;
        }

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off, 0);
        msgLength = ByteUtil.byteToInt(abyte0[0]);
        off += 1;

        msgContent = new byte[msgLength];
        ByteUtil.bytesCopy(bodyBytes, msgContent, off, off + msgLength - 1, 0);
        off += msgLength;

        Arrays.fill(abyte0,(byte)0);
        ByteUtil.bytesCopy(bodyBytes, abyte0, off, off + 7, 0);
        reserve = new String(abyte0,0,8);
        off += 8;

    }

    /**
     * makeBody
     */
    protected int makeBody()  {

        // make bodybytes
        int bodyLength = getCommandLength() + 21 * destUsrTl + msgLength;
        byte[] bodyBytes  = new byte[bodyLength];
        Arrays.fill(bodyBytes,(byte)0);

        // make parameter
        if( serviceId == null ) {
            serviceId = "";
        }
        if( feeTerminalId == null ) {
            feeTerminalId = "";
        }
        if( msgSrc == null ) {
            msgSrc = "";
        }
        if( feeType == null ) {
            feeType = "";
        }
        if( feeCode == null ) {
            feeCode = "";
        }
        if( validTime == null ) {
            validTime = "";
        }
        if( atTime == null ) {
            atTime = "";
        }
        if( srcId == null ) {
            srcId = "";
        }
        if( reserve == null ) {
            reserve = "";
        }
        if( msgContent == null ) {
            msgContent = new byte[0];
        }

        // make body
        int off = 0;
        ByteUtil.bytesCopy(ByteUtil.longToBytes8(msgId), bodyBytes, 0, 7, off);
        off += 8;
        bodyBytes[off] = ByteUtil.intToByte(pkTotal);
        off += 1;
        bodyBytes[off] = ByteUtil.intToByte(pkNumber);
        off += 1;
        bodyBytes[off] = ByteUtil.intToByte(registeredDelivery);
        off += 1;
        bodyBytes[off] = ByteUtil.intToByte(msgLevel);
        off += 1;
        ByteUtil.bytesCopy(serviceId.getBytes(), bodyBytes, 0, 9, off);
        off += 10;
        bodyBytes[off] = ByteUtil.intToByte(feeUserType);
        off += 1;
        ByteUtil.bytesCopy(feeTerminalId.getBytes(), bodyBytes, 0, 20, off);
        off += 21;
        bodyBytes[off] = ByteUtil.intToByte(tpPid);
        off += 1;
        bodyBytes[off] = ByteUtil.intToByte(tpUdhi);
        off += 1;
        bodyBytes[off] = ByteUtil.intToByte(msgFmt);
        off += 1;
        ByteUtil.bytesCopy(msgSrc.getBytes(), bodyBytes, 0, 5, off);
        off += 6;
        ByteUtil.bytesCopy(feeType.getBytes(), bodyBytes, 0, 1, off);
        off += 2;
        ByteUtil.bytesCopy(feeCode.getBytes(), bodyBytes, 0, 5, off);
        off += 6;
        ByteUtil.bytesCopy(validTime.getBytes(), bodyBytes, 0, 16, off);
        off += 17;
        ByteUtil.bytesCopy(atTime.getBytes(), bodyBytes, 0, 16, off);
        off += 17;
        ByteUtil.bytesCopy(srcId.getBytes(), bodyBytes, 0, 20, off);
        off += 21;
        bodyBytes[off] = ByteUtil.intToByte(destUsrTl);
        off += 1;

        for( int i=0; i<destTerminalId.size(); i++ ) {
            String id = (String)destTerminalId.get(i);
            ByteUtil.bytesCopy(id.getBytes(), bodyBytes, 0, 20, off);
            off += 21;
        }

        bodyBytes[off] = ByteUtil.intToByte(msgLength);
        off += 1;
        ByteUtil.bytesCopy(msgContent, bodyBytes, 0, msgLength - 1, off);
        off += msgContent.length;
        ByteUtil.bytesCopy(reserve.getBytes(), bodyBytes, 0, 7, off);
        off += 8;

        return 0;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPSubmitMessage:[sequenceId="+sequenceString()+",");
        sb.append("msgId=" + msgId + ",");
        sb.append("pkTotal=" + pkTotal+",");
        sb.append("pkNumber=" + pkNumber+",");
        sb.append("registeredDelivery=" + registeredDelivery+",");
        sb.append("msgLevel=" + msgLevel+",");
        sb.append("serviceId=" + serviceId +",");
        sb.append("feeUserType=" + feeUserType+",");
        sb.append("feeTerminalId=" + feeTerminalId+",");
        sb.append("tpPid=" + tpPid+",");
        sb.append("tpUdhi=" + tpUdhi+",");
        sb.append("msgFmt=" + msgFmt+",");
        sb.append("msgSrc=" + msgSrc+",");
        sb.append("feeType=" + feeType+",");
        sb.append("feeCode=" + feeCode+",");
        sb.append("validTime=" + validTime+",");
        sb.append("atTime=" + atTime+",");
        sb.append("srcId=" + srcId+",");
        sb.append("destUsrTl=" + destUsrTl +",");
        sb.append("destTerminalId=" + destTerminalId+",");
        sb.append("msgLength=" + msgLength+",");
        sb.append("msgContent=" + msgContent+",");
        sb.append("reserve=" + reserve+"]");
        return sb.toString();
    }
}