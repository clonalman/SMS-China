package com.netgao.sms.protocol;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gaudi.gao
 * Date: 14-6-16
 * Time: 下午5:14
 * To change this template use File | Settings | File Templates.
 */
public interface Reader {
    Message read() throws IOException;
}
