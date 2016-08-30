package com.cleverm.smartpen.statistic.model;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "t_stats".
 */
public class Stats {

    private Long id;
    private long actionId;
    private long timePoit;
    private Long timeStay;
    private Long clientId;
    private Long orgId;
    private Long tableId;
    private String desc;
    private Long secondid;
    private String querydata;
    private Boolean flag;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Stats() {
    }

    public Stats(Long id) {
        this.id = id;
    }

    public Stats(Long id, long actionId, long timePoit, Long timeStay, Long clientId, Long orgId, Long tableId, String desc, Long secondid, String querydata, Boolean flag) {
        this.id = id;
        this.actionId = actionId;
        this.timePoit = timePoit;
        this.timeStay = timeStay;
        this.clientId = clientId;
        this.orgId = orgId;
        this.tableId = tableId;
        this.desc = desc;
        this.secondid = secondid;
        this.querydata = querydata;
        this.flag = flag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public long getTimePoit() {
        return timePoit;
    }

    public void setTimePoit(long timePoit) {
        this.timePoit = timePoit;
    }

    public Long getTimeStay() {
        return timeStay;
    }

    public void setTimeStay(Long timeStay) {
        this.timeStay = timeStay;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getSecondid() {
        return secondid;
    }

    public void setSecondid(Long secondid) {
        this.secondid = secondid;
    }

    public String getQuerydata() {
        return querydata;
    }

    public void setQuerydata(String querydata) {
        this.querydata = querydata;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
