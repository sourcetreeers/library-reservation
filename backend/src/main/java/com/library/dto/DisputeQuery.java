package com.library.dto;

/**
 * 争议查询条件
 */
public class DisputeQuery {

    private Integer current = 1;
    private Integer size = 10;
    /** 类型筛选：complaint / appeal / 不传表示全部 */
    private String type;
    /** 状态筛选 */
    private String status;

    public Integer getCurrent() { return current; }
    public void setCurrent(Integer current) { this.current = current; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
