package com.zoc.qqserver.service;


import com.zoc.qqcommon.Message;
import com.zoc.qqcommon.MessageType;
import com.zoc.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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

    // 这是一个无参构造器
    public QQServer() {
        try {
            // 注意：端口可以写在配置文件
            System.out.println("服务端在9999端口监听...");
            // 启动推送新闻的线程
            new Thread(new SendNewsToAllService()).start();
            ss = new ServerSocket(9999);

            while (true) {
                Socket socket = ss.accept();
                // 得到socket关联的对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 得到socket关联的对象输出流
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                // 读取客户端发送的User对象,因为知道客户端发送的是User对象，所以可以使用强转
                User user = (User) ois.readObject();
                // 创建一个Message对象回复客户端信息
                Message message = new Message();
                if (checkUser(user.getUserId(), user.getPasswd())) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    // 将message对象回复给客户端
                    oos.writeObject(message);
                    // 创建一个线程，和客户端保持通信，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user.getUserId());
                    // 启动该线程
                    serverConnectClientThread.start();
                    // 把该线程对象，放入到一个集合当中方便管理
                    ManageClientThreads.addClientThread(user.getUserId(), serverConnectClientThread);
                } else { // 登录失败
                    System.out.println("用户 id=" + user.getUserId() + " pwd=" + user.getPasswd() + " 验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    // 关闭socket
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //如果服务器退出了while，说明服务器端不在监听，因此关闭ServerSocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
