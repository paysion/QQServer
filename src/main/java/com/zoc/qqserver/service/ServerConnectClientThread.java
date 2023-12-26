package com.zoc.qqserver.service;

import com.zoc.qqcommon.Message;
import com.zoc.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

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
                System.out.println("服务端和客户端 " + userId + " 保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 这里的message后边会使用到，暂时没发现这个message有什么用，暂且放一边
                // 后面会使用message, 根据message的类型，做相应的业务处理
                Message message = (Message) ois.readObject();
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    // 客户端要在线用户列表
                    /*
                        在线用户列表形式 100  200  紫霞仙子
                     */
                    System.out.println(message.getSender() + " 要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    // 返回message
                    // 构建一个Message 对象，返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {// 普通的聊天消息
                    // 获取消息接收者的userId,根据userId获取对应的线程
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getClientThread(message.getGetter());
                    Socket socket = serverConnectClientThread.getSocket();
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    // 这一块是消息转发，将消息转发给对应userId的线程
                    oos.writeObject(message);
                    // 构造回复的消息，但在这里似乎先不考虑回复的消息？先考虑给客户端返回消息并显示？
                    // 消息其实不用回复，转发就好了，服务端不用对消息做处理
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        //取出在线用户id
                        String onLineUserId = iterator.next().toString();
                        if (!onLineUserId.equals(message.getSender())) {//排除群发消息的这个用户
                            //进行转发message
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    // 将message发送给接收者的线程socket
                    ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getClientThread(message.getGetter()).getSocket().getOutputStream());
                    oos.writeObject(message);
                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + " 退出");
                    // 将这个客户端对应线程，从集合删除.
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    // 关闭连接
                    socket.close();
                    // 退出线程
                    break;
                }
                else {
                    System.out.println("是其他类型的message, 暂时不处理....");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //为了更方便的得到Socket
    public Socket getSocket() {
        return socket;
    }
}
