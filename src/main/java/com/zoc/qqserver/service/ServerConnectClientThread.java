package com.zoc.qqserver.service;

import com.zoc.qqcommon.Message;
import com.zoc.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerConnectClientThread extends Thread{

    private Socket socket;
    // 连接到服务端的用户id，该线程要知道是和哪个用户在进行通信
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    // 这里线程处于run的状态，可以发送or接收消息
    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("服务端和客户端" + userId + " 保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 这里的message后边会使用到，暂时没发现这个message有什么用，暂且放一边
                Message message = (Message) ois.readObject();
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    // 客户端要在线用户列表
                    System.out.println(message.getSender() + " 要在线用户列表");
                    ManageClientThreads.getOnlineUser();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
