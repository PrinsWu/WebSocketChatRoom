package com.prins.wschatroom.component;

/**
 * @author prinswu
 * @version v1.0
 * @since v1.0 2018/11/28
 */
public class MrAlert {
    private String type;
    private String message;
    private String alertSender;
    private String alertTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlertSender() {
        return alertSender;
    }

    public void setAlertSender(String alertSender) {
        this.alertSender = alertSender;
    }

    public String getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(String alertTime) {
        this.alertTime = alertTime;
    }
}
