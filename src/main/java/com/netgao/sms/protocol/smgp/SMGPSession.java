package com.netgao.sms.protocol.smgp;

import com.netgao.sms.protocol.Message;
import com.netgao.sms.protocol.Session;
import com.netgao.sms.protocol.smgp.message.*;
import com.netgao.sms.protocol.smgp.util.MD5;
import com.netgao.sms.protocol.smgp.util.SequenceGenerator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 登陆会话
 * User: gaodi.gao
 * Date: 14-6-18
 * Time: 下午4:15
 * To change this template use File | Settings | File Templates.
 */
public class SMGPSession implements Session {

    private static final Logger log = LogManager.getLogger(SMGPSession.class);

    private SMGPConnection connection;
    private String sessionId;
    private boolean authenticated;
    private Object lock = new Object();

    public SMGPSession(SMGPConnection connection, boolean authenticated){
        super();
        this.connection = connection;
        this.sessionId = java.util.UUID.randomUUID().toString();
        this.authenticated = authenticated;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void submit(String content, String spNumber, String userNumber){
        SMGPSubmitMessage submit = new SMGPSubmitMessage();
        submit.setSrcTermId(spNumber);
        submit.setDestTermIdArray(new String[] { userNumber });
        submit.setMsgFmt((byte) 8);

        byte[] bContent = null;
        try {
            bContent = content.getBytes("iso-10646-ucs-2");
        } catch (UnsupportedEncodingException e) {}

        if (bContent != null && bContent.length <= 140) {
            submit.setBMsgContent(bContent);
            submit.setMsgFmt((byte) 8);
            submit.setNeedReport((byte) 1);
            submit.setServiceId("");
            submit.setAtTime("");
            submit.setNeedReport((byte) 1);
            submit.setSequenceNumber(SequenceGenerator.nextSequence());
            send(submit);
        }
    }

    @Override
    public void heartbeat(){
        if(isAuthenticated()) {
            SMGPActiveTestMessage activeTest=new SMGPActiveTestMessage();
            activeTest.setSequenceNumber(SequenceGenerator.nextSequence());
            send(activeTest);
        }
    }

    @Override
    public boolean authenticate() {

        SMGPLoginMessage loginMsg=new SMGPLoginMessage();
        loginMsg.setClientId(connection.getClientId());
        loginMsg.setLoginMode(connection.getLoginMode());
        loginMsg.setVersion(connection.getVersion());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
        String tmp=dateFormat.format(calendar.getTime());
        loginMsg.setTimestamp(Integer.parseInt(tmp));
        loginMsg.setClientAuth(MD5.md5(connection.getClientId(), connection.getPassword(), tmp));
        loginMsg.setSequenceNumber(SequenceGenerator.nextSequence());
        send(loginMsg);
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex){
                setAuthenticated(false);
            }
        }
        return isAuthenticated();
    }

    @Override
    public void close() throws IOException {
        //保存数据
        if(isAuthenticated() ) {
            SMGPExitMessage exit = new SMGPExitMessage();
            exit.setSequenceNumber(SequenceGenerator.nextSequence());
            send(exit);
            synchronized (lock) {
                try {
                    lock.wait(6000);
                } catch (InterruptedException ex){
                    setAuthenticated(false);
                }
            }
        }
        connection.close();
    }

    @Override
    public void send(Message message){
        connection.send(message);
    }

    @Override
    public void process(Message message) throws IOException {
        if(message instanceof SMGPBaseMessage){
            SMGPBaseMessage baseMsg = (SMGPBaseMessage)message;
            if(isAuthenticated()){
                if (baseMsg instanceof SMGPActiveTestMessage) {
                    process((SMGPActiveTestMessage)baseMsg);
                } else if (baseMsg instanceof SMGPActiveTestRespMessage) {
                    // do nothing
                } else if(baseMsg instanceof SMGPExitRespMessage){
                    process((SMGPExitRespMessage)baseMsg);
                } else if(message instanceof SMGPSubmitRespMessage) {
                    process((SMGPSubmitRespMessage)message);
                } else if(message instanceof SMGPDeliverMessage) {
                    process((SMGPDeliverMessage)message);
                }
            } else if(baseMsg instanceof SMGPLoginRespMessage){
                process((SMGPLoginRespMessage)baseMsg);
            } else {
                throw new IOException("the first packet was not SMGPBindRespMessage:" + baseMsg);
            }
        }
    }

    private void process(SMGPActiveTestMessage msg) throws IOException {
        SMGPActiveTestRespMessage resp = new SMGPActiveTestRespMessage();
        resp.setSequenceNumber(msg.getSequenceNumber());
        send(resp);
    }

    private void process(SMGPLoginRespMessage rsp) throws IOException {
        synchronized (lock) {
            if(rsp.getStatus()==0){
                setAuthenticated(true);
                log.info("smgp login success host=" + connection.getHost() + ",port=" + connection.getPort() + ",clientId=" + connection.getClientId());
            } else {
                setAuthenticated(false);
                log.error("smgp login failure, host=" + connection.getHost() + ",port=" + connection.getPort() + ",clientId=" + connection.getClientId() + ",status=" + rsp.getStatus());
            }
            lock.notifyAll();
        }
    }

    private void process(SMGPExitRespMessage msg) throws IOException {
        synchronized (lock) {
            setAuthenticated(false);
            lock.notifyAll();
        }
        log.info("smgp exist success host=" + connection.getHost() + ",port=" + connection.getPort() + ",clientId=" + connection.getClientId());
    }

    private void process(SMGPSubmitRespMessage rsp) throws IOException {
        switch (rsp.getStatus())   {
            case 0:{   //发送成功

            } break;
            case 103:{  //平台流控,发送速度过快

            } break;
            default: break;
        }
    }

    private void process(SMGPDeliverMessage msg) throws IOException {
        if (msg.getIsReport() == 1) {
            //下行信息(平台送达报告)
            //SMGPDeliverMessage:[
            // sequenceNumber=0,
            // msgId=42010619162037055400,
            // isReport=1,
            // msgFmt=0,
            // recvTime=20140619162037,
            // srcTermId=18917768619,
            // destTermId=1065902100612,
            // msgLength=122,
            // msgContent={
            //  msgId=42010619162030053100,
            //  sub=001,dlvrd=001,
            //  subTime=1406191620,
            //  doneTime=1406191620,
            //  stat=DELIVRD,
            //  err=000,
            //  text=076?????̡��??�
            // }]
            SMGPReportData report = msg.getReport();
        } else {
            //下行信息(用户回复短信)
            //SMGPDeliverMessage:[
            // sequenceNumber=0,
            // msgId=42010619162054061000,
            // isReport=0,
            // msgFmt=15,
            // recvTime=20140619162054,
            // srcTermId=18917768619,
            // destTermId=1065902100612,
            // msgLength=10,
            // msgContent=哈哈哈哈哈
            // ]

        }
        SMGPDeliverRespMessage rsp = new SMGPDeliverRespMessage();
        rsp.setSequenceNumber(msg.getSequenceNumber());
        rsp.setMsgId(msg.getMsgId());
        rsp.setStatus(0);
        send(rsp);
    }

    private void setAuthenticated(boolean value) {
        this.authenticated = value;
    }
}

