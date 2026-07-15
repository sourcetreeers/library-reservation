package com.library.dto;

/**
 * 处理争议请求体
 */
public class HandleDisputeDTO {

    /** 争议类型：complaint / appeal */
    private String type;
    /** 处理结果状态 */
    private String status;
    /** 回复意见 */
    private String reply;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
}
