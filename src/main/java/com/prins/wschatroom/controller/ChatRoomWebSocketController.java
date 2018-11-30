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
    CrMessageService crMessageService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/connect")
    @SendTo("/topic/crcontact")
    public CrContact connectWs(CrMessage crMessage) throws CrAlertException {
        String mrid = crMessage.getCrid();
        String uid = crMessage.getSender();
        crMessageService.connectChatRoom(mrid, uid);

        //alert user join
        CrAlert alert = crMessageService.createCrAlert("info", String.format("User[%s] join talk!", uid));
        messagingTemplate.convertAndSend("/topic/alert", alert);
        //message user join
        crMessage.setSender("sysinfo");
        crMessage.setMessage(uid + " join talk!");
        crMessage = crMessageService.sendCrMessage(crMessage);
        messagingTemplate.convertAndSend("/topic/crmessage", crMessage);

        return crMessageService.getAllContactByCrid(mrid);
    }

    @MessageMapping("/disconnect")
    @SendTo("/topic/crcontact")
    public CrContact disconnectWs(CrMessage crMessage) throws CrAlertException {
        String mrid = crMessage.getCrid();
        String uid = crMessage.getSender();
        crMessageService.disconnectChatRoom(mrid, uid);

        //alert user exit
        CrAlert alert = crMessageService.createCrAlert("warning", String.format("User[%s] exit talk!", uid));
        messagingTemplate.convertAndSend("/topic/alert", alert);

        //message user exit
        crMessage.setSender("sysdanger");
        crMessage.setMessage(uid + " exit talk!");
        crMessage = crMessageService.sendCrMessage(crMessage);
        messagingTemplate.convertAndSend("/topic/crmessage", crMessage);

        return crMessageService.getAllContactByCrid(mrid);
    }

    @MessageMapping("/sendCrMessage")
    @SendTo("/topic/crmessage")
    public CrMessage[] sendMrMessage(CrMessage crMessage) throws CrAlertException {
        return new CrMessage[] {crMessageService.sendCrMessage(crMessage)};
    }

    @MessageExceptionHandler
    @SendTo("/topic/alert")
    public CrAlert handleException(Throwable exception) {
        return ((CrAlertException)exception).getCrAlert();
    }

    @GetMapping(value = "/chatroom")
    public String viewDefaultMeetingRoomPage(Model model) {
        model.addAttribute("crid", crMessageService.getDefaultCrid());
        return "chatroom";
    }

    @GetMapping(value = "/chatroom/{crid}")
    public String viewMeetingRoomPage(Model model, @PathVariable("crid") String crid) {
        model.addAttribute("crid", crid);
        return "chatroom";
    }
}
