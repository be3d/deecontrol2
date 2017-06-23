package com.ysoft.dctrl.safeq.lpr;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by pilar on 12.4.2017.
 */
public interface LprSender {
    void send(String userName, String queue, String fileName, InputStream jobData, long length) throws IOException;
}
