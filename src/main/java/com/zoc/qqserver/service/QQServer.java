package com.zoc.qqserver.service;


import com.zoc.qqcommon.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是服务器，在监听9999，等待客户端的连接，并保持通信
 */
public class QQServer {
    private ServerSocket ss = null;
    // 创建一个集合，存放多个用户，如果是这些用户登录，就认为是合法
    // 这里我们也可以使用 ConcurrentHashMap ，可以处理并发的集合，没有线程安全
    // HashMap 没有处理线程安全，因此在多线程情况下是不安全
    // ConcurrentHashMap 处理的线程安全，即线程同步处理，在多线程情况是安全的
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static { // 在静态代码块，初始化 validUsers

        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子", "123456"));
        validUsers.put("菩提老祖", new User("菩提老祖", "123456"));

    }

    // 验证用户是否有效的方法
    private boolean checkUser(String userId,String pwd) {
        User user = validUsers.get(userId);
        // user为空，代表数据库中没有该用户
        if (user == null) {
            return false;
        }
        // 密码不等返回false，但是这么判断是否会有遗漏？
        if (!user.getPasswd().equals(pwd)) {
            return false;
        }
        return true;
    }

    public QQServer() {
        // 注意：端口可以写在配置文件
        System.out.println("服务端在9999端口监听...");
        // 启动推送新闻的线程
        new Thread(new SendNewsToAllService()).start();
        try {
            ss = new ServerSocket(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
