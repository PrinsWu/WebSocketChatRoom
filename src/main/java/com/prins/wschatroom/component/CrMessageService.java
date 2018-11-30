package com.prins.wschatroom.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author prinswu
 * @version v1.0
 * @since v1.0 2018/11/26
 */
@Service
@Slf4j
public class CrMessageService {

    private Set<String> cridSet = new HashSet<String>();
    private Map<String, Set<String>> crIdSet = new HashMap();
    private String defaultCrid = "default";
    private DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @PostConstruct
    public void initialize() {
        cridSet.add(defaultCrid);
        crIdSet.put(defaultCrid, new LinkedHashSet<>());
    }

    public String getDefaultCrid() {
        return defaultCrid;
    }

    public CrMessage connectChatRoom(String mrid, String userid) {
        if (StringUtils.isEmpty(mrid)) {
            mrid = defaultCrid;
        }
        CrMessage crMessage = new CrMessage();
        if (!cridSet.contains(mrid)) {
            crMessage.setStatus("fail");
            crMessage.setSender("System");
            crMessage.setSendTime(df.format(new Date()));
            crMessage.setMessage(String.format("The Meeting Room[{}] not exist!", mrid));
        } else if(crIdSet.get(mrid).contains(userid)) {
            crMessage.setStatus("fail");
            crMessage.setSender("System");
            crMessage.setSendTime(df.format(new Date()));
            crMessage.setMessage(String.format("The user id[{}] existed in Meeting Room[{}]!", userid, mrid));
        } else {
            Set<String> ids = crIdSet.get(mrid);
            ids.add(userid);
            crMessage.setStatus("success");
            crMessage.setSender("System");
            crMessage.setSendTime(df.format(new Date()));
            crMessage.setMessage(String.format("Welcome to Meeting Room[{}]!", mrid));
            log.info(String.format("connect ids size:%d", ids.size()));
        }
        return crMessage;
    }

    public CrMessage disconnectChatRoom(String crid, String userid) {
        if (StringUtils.isEmpty(crid)) {
            crid = defaultCrid;
        }
        CrMessage crMessage = new CrMessage();
        if (!cridSet.contains(crid)) {
            crMessage.setStatus("fail");
            crMessage.setSender("System");
            crMessage.setSendTime(df.format(new Date()));
            crMessage.setMessage(String.format("The Meeting Room[{}] not exist!", crid));
        } else if(!crIdSet.get(crid).contains(userid)) {
            crMessage.setStatus("fail");
            crMessage.setSender("System");
            crMessage.setSendTime(df.format(new Date()));
            crMessage.setMessage(String.format("The user id[{}] not existed in Meeting Room[{}]!", userid, crid));
        } else {
            Set<String> ids = crIdSet.get(crid);
            ids.remove(userid);
            crMessage.setStatus("success");
            crMessage.setSender("System");
            crMessage.setSendTime(df.format(new Date()));
            crMessage.setMessage(String.format("Bye!"));
            log.info(String.format("disconnect ids size:%d", ids.size()));
        }
        return crMessage;
    }

    public CrContact getAllContactByCrid(String crid) {
        CrContact crContact = new CrContact();
        crContact.setStatus("success");
        crContact.setCrid(crid);
        Set<String> ids = crIdSet.get(crid);
        crContact.setUids(ids.toArray(new String[ids.size()]));
        return crContact;
    }

    public CrMessage sendCrMessage(CrMessage crMessage) throws CrAlertException {
        String uid = crMessage.getSender();
        if (uid.equals("error")) {
            // simulate exception and send alert to client
            CrAlert mrAlert = createCrAlert("danger", "System error, please try again!");
            throw new CrAlertException(mrAlert);
        }

        String crid = getDefaultCrid();
        if (!StringUtils.isEmpty(crMessage.getCrid())) {
            crid = crMessage.getCrid();
        }

        crMessage.setCrid(crid);
        crMessage.setSendTime(df.format(new Date()));
        crMessage.setStatus("Success");
        log.info("service get message:{}", crMessage.getMessage());
        return crMessage;
    }

    public CrAlert createCrAlert(String type, String msg) {
        CrAlert alert = new CrAlert();
        alert.setType(type);
        alert.setMessage(msg);
        alert.setAlertSender("sys");
        alert.setAlertTime(df.format(new Date()));
        return alert;
    }
}
