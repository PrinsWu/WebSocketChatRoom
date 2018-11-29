package com.prins.wschatroom.component;

import com.sun.security.auth.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * This is not work.
 * @author prinswu
 * @version v1.0
 * @since v1.0 2018/11/28
 */
//@Component
@Slf4j
@Deprecated
public class WebSocketEventListener {

    @Autowired
    MrMessageService mrMessageService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

//    @EventListener
    public void handleConnectListener(SessionConnectedEvent event) {
        //這裡處理uid不適合，因為此刻ws connection尚未建立，所以送到topic的訊息client收不到。
        log.info("[ws-connected] socket connect: {}", event.getMessage());
        StompHeaderAccessor stompAccessor = StompHeaderAccessor.wrap(event.getMessage());
        MessageHeaderAccessor
                accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        accessor.getMessageHeaders();
        GenericMessage<?> generic = (GenericMessage<?>) accessor.getHeader("simpConnectMessage");
        Map map = (Map) generic.getHeaders().get("nativeHeaders");
        log.info(map.toString());
        String uid = (String) ((List)map.get("uid")).get(0);
//        log.info("accessor is NULL?" + (null == accessor));
//        List<String> uids = accessor.getNativeHeader("uid");
//        log.info("uids is NULL?" + (null == uids));
//        uids.stream().forEach(log::info);

//        String uid = accessor.getFirstNativeHeader("uid");
        Principal principal = new UserPrincipal(uid);
        stompAccessor.setUser(principal);
        log.info("====> connect uid:{}", uid);

        String mrid = mrMessageService.getDefaultMrid();
        mrMessageService.connectMeetingRoom(mrid, uid);

        //alert user join
        MrAlert alert = mrMessageService.createMrAlert("info", String.format("User[%s] join talk!", uid));
        messagingTemplate.convertAndSend("/topic/alert", alert);
        log.info("====> send alert");

        //message user join
        MrMessage mrMessage = new MrMessage();
        mrMessage.setMrid(mrid);
        mrMessage.setSender("sysinfo");
        mrMessage.setMessage(uid + " join talk!");
//        mrMessage = mrMessageService.sendMrMessage(mrMessage);
        messagingTemplate.convertAndSend("/topic/mrmessage", mrMessage);
        log.info("====> send join message");

        messagingTemplate.convertAndSend("/topic/mrcontact", mrMessageService.getAllContactByMrid(mrid));
        log.info("====> send mrcontact");
    }

//    @EventListener
    public void handleDisconnectListener(SessionDisconnectEvent event) {
        log.info("[ws-disconnect] socket disconnect: {}", event.getMessage());
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("====> disconnect uid:{}", accessor.getUser().getName());

        String mrid = mrMessageService.getDefaultMrid();
        String uid = accessor.getUser().getName();
        mrMessageService.disconnectMeetingRoom(mrid, uid);

        //alert user exit
        MrAlert alert = mrMessageService.createMrAlert("warning", String.format("User[%s] exit talk!", uid));
        messagingTemplate.convertAndSend("/topic/alert", alert);
        log.info("====> send alert");

        //message user exit
        MrMessage mrMessage = new MrMessage();
        mrMessage.setMrid(mrid);
        mrMessage.setSender("sysdanger");
        mrMessage.setMessage(uid + " exit talk!");
//        mrMessage = mrMessageService.sendMrMessage(mrMessage);
        messagingTemplate.convertAndSend("/topic/mrmessage", mrMessage);
        log.info("====> send exit message");

        messagingTemplate.convertAndSend("/topic/mrcontact", mrMessageService.getAllContactByMrid(mrid));
        log.info("====> send mrcontact");
    }
}
