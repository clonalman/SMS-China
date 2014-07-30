package com.netgao.sms.protocol;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gaudi.gao
 * Date: 14-6-16
 * Time: 下午5:13
 * To change this template use File | Settings | File Templates.
 */
public interface Writer {
    void write(Message message) throws IOException;
}
