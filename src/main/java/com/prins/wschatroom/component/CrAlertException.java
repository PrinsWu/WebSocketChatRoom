package com.prins.wschatroom.component;

/**
 * @author prinswu
 * @version v1.0
 * @since v1.0 2018/11/28
 */
public class CrAlertException extends Exception {
    private CrAlert crAlert;

    public CrAlertException(CrAlert crAlert) {
        this.crAlert = crAlert;
    }

    public CrAlert getCrAlert() {
        return crAlert;
    }

    public void setCrAlert(CrAlert crAlert) {
        this.crAlert = crAlert;
    }
}
