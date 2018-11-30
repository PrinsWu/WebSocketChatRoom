package com.prins.wschatroom.component;

/**
 * @author prinswu
 * @version v1.0
 * @since v1.0 2018/11/26
 */
public class CrContact {
    private String status;
    private String crid;
    private String[] uids;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public String[] getUids() {
        return uids;
    }

    public void setUids(String[] uids) {
        this.uids = uids;
    }
}
