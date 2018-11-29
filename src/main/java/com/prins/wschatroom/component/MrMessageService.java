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
public class MrMessageService {

    private Set<String> mridSet = new HashSet<String>();
//    private Map<String, List<MrMessage>> mrMessageMap = new HashMap();
    private Map<String, Set<String>> mrIdSet = new HashMap();
    private String defaultMrid = "default";
    private DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @PostConstruct
    public void initialize() {
        mridSet.add(defaultMrid);
//        mrMessageMap.put(defaultMrid, new ArrayList<MrMessage>());
        mrIdSet.put(defaultMrid, new LinkedHashSet<>());
    }

    public String getDefaultMrid() {
        return defaultMrid;
    }

    public MrMessage connectMeetingRoom(String mrid, String userid) {
        if (StringUtils.isEmpty(mrid)) {
            mrid = defaultMrid;
        }
        MrMessage mrMessage = new MrMessage();
        if (!mridSet.contains(mrid)) {
            mrMessage.setStatus("fail");
            mrMessage.setSender("System");
            mrMessage.setSendTime(df.format(new Date()));
            mrMessage.setMessage(String.format("The Meeting Room[{}] not exist!", mrid));
        } else if(mrIdSet.get(mrid).contains(userid)) {
            mrMessage.setStatus("fail");
            mrMessage.setSender("System");
            mrMessage.setSendTime(df.format(new Date()));
            mrMessage.setMessage(String.format("The user id[{}] existed in Meeting Room[{}]!", userid, mrid));
        } else {
            Set<String> ids = mrIdSet.get(mrid);
            ids.add(userid);
            mrMessage.setStatus("success");
            mrMessage.setSender("System");
            mrMessage.setSendTime(df.format(new Date()));
            mrMessage.setMessage(String.format("Welcome to Meeting Room[{}]!", mrid));
            log.info(String.format("connect ids size:%d", ids.size()));
        }
        return mrMessage;
    }

    public MrMessage disconnectMeetingRoom(String mrid, String userid) {
        if (StringUtils.isEmpty(mrid)) {
            mrid = defaultMrid;
        }
        MrMessage mrMessage = new MrMessage();
        if (!mridSet.contains(mrid)) {
            mrMessage.setStatus("fail");
            mrMessage.setSender("System");
            mrMessage.setSendTime(df.format(new Date()));
            mrMessage.setMessage(String.format("The Meeting Room[{}] not exist!", mrid));
        } else if(!mrIdSet.get(mrid).contains(userid)) {
            mrMessage.setStatus("fail");
            mrMessage.setSender("System");
            mrMessage.setSendTime(df.format(new Date()));
            mrMessage.setMessage(String.format("The user id[{}] not existed in Meeting Room[{}]!", userid, mrid));
        } else {
            Set<String> ids = mrIdSet.get(mrid);
            ids.remove(userid);
            mrMessage.setStatus("success");
            mrMessage.setSender("System");
            mrMessage.setSendTime(df.format(new Date()));
            mrMessage.setMessage(String.format("Bye!"));
            log.info(String.format("disconnect ids size:%d", ids.size()));
        }
        return mrMessage;
    }

    public MrContact getAllContactByMrid(String mrid) {
        MrContact mrContact = new MrContact();
        mrContact.setStatus("success");
        mrContact.setMrid(mrid);
        Set<String> ids = mrIdSet.get(mrid);
        mrContact.setUids(ids.toArray(new String[ids.size()]));
        return mrContact;
    }

    public MrMessage sendMrMessage(MrMessage mrMessage) throws MrAlertException {
        String uid = mrMessage.getSender();
        if (uid.equals("error")) {
            // simulate exception and send alert to client
            MrAlert mrAlert = createMrAlert("danger", "System error, please try again!");
            throw new MrAlertException(mrAlert);
        }

        String mrid = getDefaultMrid();
        if (!StringUtils.isEmpty(mrMessage.getMrid())) {
            mrid = mrMessage.getMrid();
        }

        mrMessage.setMrid(mrid);
        mrMessage.setSendTime(df.format(new Date()));
        mrMessage.setStatus("Success");
        log.info("service get message:{}", mrMessage.getMessage());
        return mrMessage;
    }

    public MrAlert createMrAlert(String type, String msg) {
        MrAlert alert = new MrAlert();
        alert.setType(type);
        alert.setMessage(msg);
        alert.setAlertSender("sys");
        alert.setAlertTime(df.format(new Date()));
        return alert;
    }
}
