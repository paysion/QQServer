package com.zoc.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类用于管理和客户端通信的线程
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    // 返回 hm
    public static HashMap<String,ServerConnectClientThread> getHm() {
        return hm;
    }

    // 根据userId返回ServerConnectClientThread线程
    public static ServerConnectClientThread getClientThread(String userId) {
        return hm.get(userId);
    }

    // 编写方法返回在线用户列表
    public static String getOnlineUser() {
        // 遍历hashmap的key获取用户列表,使用迭代器？
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        // 这里涉及到迭代器的使用？
        while (iterator.hasNext()) {
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList;
    }

    // 添加线程对象到 hm 集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    public static void removeServerConnectClientThread(String userId) {
        hm.remove(userId);
    }
}
