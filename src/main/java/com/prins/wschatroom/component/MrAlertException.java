package com.prins.wschatroom.component;

/**
 * @author prinswu
 * @version v1.0
 * @since v1.0 2018/11/28
 */
public class MrAlertException extends Exception {
    private MrAlert mrAlert;

    public MrAlertException(MrAlert mrAlert) {
        this.mrAlert = mrAlert;
    }

    public MrAlert getMrAlert() {
        return mrAlert;
    }

    public void setMrAlert(MrAlert mrAlert) {
        this.mrAlert = mrAlert;
    }
}
