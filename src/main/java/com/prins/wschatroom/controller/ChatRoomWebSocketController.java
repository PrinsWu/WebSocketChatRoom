package com.prins.wschatroom.controller;

import com.prins.wschatroom.component.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author prinswu
 * @version v1.0
 * @since v1.0 2018/11/26
 */
@Controller
@Slf4j
public class ChatRoomWebSocketController {

    @Autowired
    MrMessageService mrMessageService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/connect")
    @SendTo("/topic/mrcontact")
    public MrContact connectWs(MrMessage mrMessage) throws MrAlertException {
        String mrid = mrMessage.getMrid();
        String uid = mrMessage.getSender();
        mrMessageService.connectMeetingRoom(mrid, uid);

        //alert user join
        MrAlert alert = mrMessageService.createMrAlert("info", String.format("User[%s] join talk!", uid));
        messagingTemplate.convertAndSend("/topic/alert", alert);
        //message user join
        mrMessage.setSender("sysinfo");
        mrMessage.setMessage(uid + " join talk!");
        mrMessage = mrMessageService.sendMrMessage(mrMessage);
        messagingTemplate.convertAndSend("/topic/mrmessage", mrMessage);

        return mrMessageService.getAllContactByMrid(mrid);
    }

    @MessageMapping("/disconnect")
    @SendTo("/topic/mrcontact")
    public MrContact disconnectWs(MrMessage mrMessage) throws MrAlertException {
        String mrid = mrMessage.getMrid();
        String uid = mrMessage.getSender();
        mrMessageService.disconnectMeetingRoom(mrid, uid);

        //alert user exit
        MrAlert alert = mrMessageService.createMrAlert("warning", String.format("User[%s] exit talk!", uid));
        messagingTemplate.convertAndSend("/topic/alert", alert);

        //message user exit
        mrMessage.setSender("sysdanger");
        mrMessage.setMessage(uid + " exit talk!");
        mrMessage = mrMessageService.sendMrMessage(mrMessage);
        messagingTemplate.convertAndSend("/topic/mrmessage", mrMessage);

        return mrMessageService.getAllContactByMrid(mrid);
    }

    @MessageMapping("/sendMrMessage")
    @SendTo("/topic/mrmessage")
    public MrMessage[] sendMrMessage(MrMessage mrMessage) throws MrAlertException {
        return new MrMessage[] {mrMessageService.sendMrMessage(mrMessage)};
    }

    @MessageExceptionHandler
    @SendTo("/topic/alert")
    public MrAlert handleException(Throwable exception) {
        return ((MrAlertException)exception).getMrAlert();
    }

    @GetMapping(value = "/chatroom")
    public String viewDefaultMeetingRoomPage(Model model) {
        model.addAttribute("mrid", mrMessageService.getDefaultMrid());
        return "chatroom";
    }

    @GetMapping(value = "/chatroom/{mrid}")
    public String viewMeetingRoomPage(Model model, @PathVariable("mrid") String mrid) {
        model.addAttribute("mrid", mrid);
        return "chatroom";
    }
}
