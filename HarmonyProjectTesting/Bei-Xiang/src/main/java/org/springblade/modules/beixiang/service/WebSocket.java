package org.springblade.modules.beixiang.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springblade.modules.beixiang.entity.Message;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{userId}")  // 接口路径 ws://XXX:XXX/webSocket/deviceId;
@Slf4j
public class WebSocket {

	//与某个客户端的连接会话，需要通过它来给客户端发送数据
	public Session session;
	/**
	 * 用户Id
	 */
	public String userId;
	private static CopyOnWriteArraySet<WebSocket> webSockets =new CopyOnWriteArraySet<>();
	// 用来存在线连接用户信息
	private static ConcurrentHashMap<String,Session> sessionPool = new ConcurrentHashMap<String,Session>();

	/**
	 * 链接成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam(value="userId")String userId) {
		try {
			Session oldSession = sessionPool.get(userId);
			if(oldSession!=null){
				sessionPool.remove(userId);
				oldSession.close();
				for (WebSocket webSocket : webSockets){
					if(webSocket.userId.equals(userId)){
						webSockets.remove(webSocket);
						break;
					}
				}
			}
			this.session = session;
			this.userId = userId;
			webSockets.add(this);
			sessionPool.put(userId, session);
			log.info("【websocket消息】有新的连接:"+userId+"，总数为:"+webSockets.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 链接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		try {
			webSockets.remove(this);
			sessionPool.remove(this.userId);
			log.info("【websocket消息】连接断开，总数为:"+webSockets.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message
	 */
	@OnMessage
	public void onMessage(Session session, String message) {
		for (String key : sessionPool.keySet()) {
			Session sen = sessionPool.get(key);
			if(sen.equals(session)){
				log.info("【websocket消息】收到客户端{"+key+"}消息:"+message);
			}
		}
		session.getAsyncRemote().sendText("PONG");
	}

	/** 发送错误时的处理
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {

		log.error("用户错误,原因:"+error.getMessage());
		error.printStackTrace();
	}


	// 此为广播消息
	public void sendAllMessage(String message) {
		log.info("【websocket消息】广播消息:"+message);
		for(WebSocket webSocket : webSockets) {
			try {
				if(webSocket.session.isOpen()) {
					webSocket.session.getAsyncRemote().sendText(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 此为单点消息
	public void sendOneMessage(String userId, String message) {
		Session session = sessionPool.get(userId);
		if (session != null&&session.isOpen()) {
			try {
				log.error("【websocket消息】 单点消息"+userId+":"+message);
				session.getAsyncRemote().sendText(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// 此为单点消息
	public void sendOneMessage(String userId, Message message) {
		Session session = sessionPool.get(userId);
		String content = JSONObject.toJSONString(message);
		if (session != null&&session.isOpen()) {
			try {
				log.error("【websocket消息】 单点消息"+userId+":"+message);
				session.getAsyncRemote().sendText(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 此为单点消息(多人员)
	public void sendMoreMessage(String[] userIds, Message message) {
		for(String userId:userIds) {
			Session session = sessionPool.get(userId);
			if (session != null&&session.isOpen()) {
				try {
					log.error("【websocket消息】 单点消息"+userId+":"+message);
					String content = JSONObject.toJSONString(message);
					session.getAsyncRemote().sendText(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
