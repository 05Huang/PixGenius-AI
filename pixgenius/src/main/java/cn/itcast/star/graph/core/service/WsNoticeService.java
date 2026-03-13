package cn.hx.pix.genius.core.service;

public interface WsNoticeService {

    // 一对一发送消息
    public void sendToUser(String clientId,String message);

    // 发送全部发送消息
    public void sendToAll(String message);
}
