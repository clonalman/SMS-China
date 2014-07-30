package com.netgao.sms.protocol;

import java.io.IOException;

/**
 * 短信通道会话
 * User: gaudi.gao
 * Date: 14-6-18
 * Time: 下午4:06
 * To change this template use File | Settings | File Templates.
 */
public interface Session extends java.io.Closeable {
    String getSessionId();
    boolean isAuthenticated();
    boolean authenticate();
    void heartbeat();
    void submit(String content, String spNumber, String userNumber);
    void send(Message message);
    void process(Message message) throws IOException;
}
