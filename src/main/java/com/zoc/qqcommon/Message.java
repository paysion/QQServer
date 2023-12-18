package com.zoc.qqcommon;

import lombok.Data;

import java.io.Serializable;


@Data
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;//发送者
    private String getter;//接收者
    private String content;//消息内容
    private String sendTime;//发送时间
    private String mesType;//消息类型[可以在接口定义消息类型]

    //进行扩展 和文件相关的成员
    private byte[] fileBytes;
    private int fileLen = 0;
    private String dest; //将文件传输到哪里
    private String src; //源文件路径
}
