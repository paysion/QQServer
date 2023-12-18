package com.zoc.qqserver.service;

import java.util.HashMap;

/**
 * 该类用于管理和客户端通信的线程
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    // 返回 hm
    public static HashMap<String,ServerConnectClientThread> getHm() {
        return hm;
    }


}
