package com.netgao.sms.protocol;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 短信连接字符串
 * User: gaudi.gao
 * Date: 14-6-17
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
public abstract class Connection implements java.io.Closeable {

    protected static final Logger log = LogManager.getLogger(Connection.class);
    private String host;
    private int port;

    private Socket socket;
    private SafeThread heartbeat;
    private SafeThread receiver;
    private boolean autoReconnect;
    private boolean keepAlive;
    private int keepAliveInterval;
    private int sendInterval;
    private Reader in;
    private Writer out;
    private Queue<Message> queue;
    private Session session;

    public Connection(){
        super();
        this.autoReconnect =
        this.keepAlive = true;
        this.keepAliveInterval = 9000;
        this.sendInterval = 50;
        this.queue = new LinkedBlockingQueue<Message>();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }

    public Boolean getKeepAlive(){
        return keepAlive;
    }

    public void setKeepAlive(Boolean value){
        keepAlive = value;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(int value) {
        this.keepAliveInterval = value;
    }

    public int getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(int sendInterval) {
        this.sendInterval = sendInterval;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public Session getSession() {
        return session;
    }

    public void send(Message message) {

        if(!isConnected()) {
             queue.offer(message);
        } else {
            Message msg = (Message)queue.poll();
            if(msg != null) {
                send(msg);
            }
            try {
                out.write(message);
                onSend(message);
            } catch (IOException ex) {
                queue.offer(message);
                disconnect();
                onError("socket connection send msg fail,retry:" + message, ex);
            }
        }
    }

    public void connect(String host, int port) {
        this.host = host;
        this.port = port;
        this.connect();
    }

    public void connect() {
        try {
            if ((this.port <= 0) || (this.port > 65535)) {
                log.error(String.format("port error:%d", this.port));
                throw new IndexOutOfBoundsException(String.format("port error:%d", this.port));
            }
            this.socket = new Socket();
            this.socket.setKeepAlive(keepAlive);
            this.socket.connect(new InetSocketAddress(host, port));
            this.out = createWriter(this.socket.getOutputStream());
            this.in = createReader(this.socket.getInputStream());
            this.startThreads();
            this.onConnect();
        } catch (Exception ex) {
            onError("socket connect failure", ex);
        }
    }

    public void disconnect() {

        killThreads();

        if (socket != null) {
            try {
                socket.shutdownInput();
            } catch (IOException ex) { /* do nothing */ }
            try {
                socket.shutdownOutput();
            } catch (IOException ex) { /* do nothing */ }
            try {
                socket.close();
                socket = null;
                in = null;
                out = null;
            } catch (IOException ex) { /* do nothing */ }
        }

        onDisconnect();
    }

    @Override
    public void close() {
        queue.clear();
        autoReconnect = false;
        if(isConnected()){
            disconnect();
        }
        onClose();
    }

    protected abstract Session createSession();
    protected abstract Writer createWriter(OutputStream output);
    protected abstract Reader createReader(InputStream input);

    protected void heartbeat() throws IOException {
        Session session = getSession();
        if(session != null && session.isAuthenticated()){
            session.heartbeat();
        }
    }

    protected void onReceive(Message message) throws IOException {
        log.info("recv: " + message);
        if(message != null){
            Session session = getSession();
            if(session != null){
                session.process(message);
            }
        }
    }

    protected void onSend(Message message) throws IOException {
        log.info("send: " + message);
    }

    protected void onError(String message) {
        log.error(String.format("%s host=%s,port=%d", message, this.getHost(), this.getPort()));
    }

    protected void onError(String message, Exception error) {
        log.error(String.format("%s host=%s,port=%d", message, this.getHost(), this.getPort()), error);
    }

    protected void onConnect() {
        log.info(String.format("socket connect success host=%s,port=%d %tc%n", this.getHost(), this.getPort(), new Date()));
        if(session == null){
            session = createSession();
        }
        if(session.authenticate()){
            sendQueue();
        }
    }

    protected void onDisconnect() {
        log.info(String.format("socket disconnect success host=%s,port=%d %tc%n", this.getHost(), this.getPort(), new Date()));
        if(autoReconnect){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) { }
            connect();
        }
    }

    protected void onClose() {
        log.info(String.format("socket close success host=%s,port=%d %tc%n", this.getHost(), this.getPort(), new Date()));
    }

    protected void sendQueue(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sendQueue(sendInterval);
            }
        }, "queue");
        t.setDaemon(true);
        t.start();
    }

    private void sendQueue(int speed){
        do{
            if(isConnected()) {
                Message msg = (Message)queue.poll();
                if(msg != null){
                    send(msg);
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException ex) { }
                }
            } else break;
        } while (queue.size() > 0);
    }

    private void startThreads(){
        if (this.keepAlive && this.keepAliveInterval > 0) {
            this.heartbeat = new SafeThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(keepAliveInterval);
                    } catch (InterruptedException ex) { }
                    //检查建立socket连接
                    if (isConnected()){
                        try {
                            if(queue.isEmpty()){
                                heartbeat();
                            }
                        } catch (IOException ex) {
                            log.error("heartbeat", ex);
                        }
                    }
                }
            }, "heartbeat");
            this.heartbeat.start();
        }

        this.receiver = new SafeThread(new Runnable() {
            @Override
            public void run() {
                //建立socket连接
                if (isConnected()) {
                    try {
                        Message msg = in.read();
                        if (msg != null) {
                            onReceive(msg);
                        } else {
                            //返回空,关闭socket
                            disconnect();
                            onError("socket connection receive msg null");
                        }
                    } catch (IOException ex) {
                        //主线程退出
                        if(Connection.this != null) {
                            Connection.this.disconnect();
                        }
                        onError("socket connection receive msg error: " + ex.getMessage(), ex);
                    }
                }
            }
        }, "receiver");
        this.receiver.start();
    }

    private void killThreads(){
        if (this.heartbeat != null) {
            this.heartbeat.kill();
            this.heartbeat = null;
        }
        if(this.receiver != null){
            this.receiver.kill();
            this.receiver = null;
        }
    }

    private final class SafeThread extends Thread {
        private boolean alive = true;

        public SafeThread(Runnable target, String name) {
            super(target, name);
            setDaemon(false);
        }

        public void kill() {
            //安全退出线程
            this.alive = false;
        }

        @Override
        public final void run() {
            while (alive) {
                try {
                    super.run();
                } catch (Exception ex) {
                    log.error("thread error 1", ex);
                } catch (Throwable t) {
                    log.error("thread error 2", t);
                }
            }
        }
    }
}
